package org.choongang.board.service;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.choongang.board.controllers.BoardDataSearch;
import org.choongang.board.entities.*;
import org.choongang.board.repositories.BoardDataRepository;
import org.choongang.board.repositories.SaveBoardDataRepository;
import org.choongang.commons.ListData;
import org.choongang.commons.Pagination;
import org.choongang.commons.Utils;
import org.choongang.member.MemberUtil;
import org.choongang.member.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaveBoardDataService {
    private final MemberUtil memberUtil;
    private final BoardInfoService boardInfoService;
    private final SaveBoardDataRepository saveBoardDataRepository;
    private final BoardDataRepository boardDataRepository;
    private final HttpServletRequest request;

    /**
     * 게시글 번호 찜하기
     *
     * @param bSeq
     */
    public void save(Long bSeq) {
        if (!memberUtil.isLogin()) {
            return;
        }
        try {
            SaveBoardData data = new SaveBoardData();
            data.setBSeq(bSeq);
            data.setMSeq(memberUtil.getMember().getSeq());

            saveBoardDataRepository.saveAndFlush(data);
        } catch (Exception e) {}
    }

    public void delete(Long bSeq) {
        if (!memberUtil.isLogin()) {
            return;
        }

        SaveBoardDataId id = new SaveBoardDataId(bSeq, memberUtil.getMember().getSeq());

        SaveBoardData data = saveBoardDataRepository.findById(id).orElse(null);
        if (data != null) { // 찜한 기록이 있는 경우만 삭제
            saveBoardDataRepository.delete(data);
            saveBoardDataRepository.flush();
        }
    }

    /**
     * 찜 게시글 비우기
     *
     */
    public void deleteAll() {
        if (!memberUtil.isLogin()) {
            return;
        }


        QSaveBoardData saveBoardData = QSaveBoardData.saveBoardData;
        List<SaveBoardData> items = (List<SaveBoardData>)saveBoardDataRepository.findAll(saveBoardData.mSeq.eq(memberUtil.getMember().getSeq()));

        saveBoardDataRepository.deleteAll(items);
        saveBoardDataRepository.flush();
    }

    /**
     * 찜한 게시글 인지 체크
     *
     * @return
     */
    public boolean saved(Long bSeq) {

        if (memberUtil.isLogin()) {
            SaveBoardDataId id = new SaveBoardDataId(bSeq, memberUtil.getMember().getSeq());

            return saveBoardDataRepository.existsById(id);
        }

        return false;
    }

    /**
     * 찜한 게시글 목록
     *
     * @param search
     * @return
     */
    public ListData<BoardData> getList(BoardDataSearch search) {

        int page = Utils.onlyPositiveNumber(search.getPage(), 1);
        int limit = Utils.onlyPositiveNumber(search.getLimit(), 20);

        Member member = memberUtil.getMember();
        List<Long> bSeqs = saveBoardDataRepository.getBoardDataSeqs(member.getSeq());

        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(boardData.seq.in(bSeqs));

        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<BoardData> data = boardDataRepository.findAll(andBuilder, pageable);

        Pagination pagination = new Pagination(page, (int)data.getTotalElements(), 10, limit, request);

        List<BoardData> items = data.getContent();
        items.forEach(boardInfoService::addBoardData);

        return new ListData<>(items, pagination);
    }
}
