package org.choongang.board.service;

import lombok.RequiredArgsConstructor;
import org.choongang.board.entities.QSaveBoardData;
import org.choongang.board.entities.SaveBoardData;
import org.choongang.board.entities.SaveBoardDataId;
import org.choongang.board.repositories.SaveBoardDataRepository;
import org.choongang.member.MemberUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaveBoardDataService {
    private final MemberUtil memberUtil;
    private final BoardInfoService boardInfoService;
    private final SaveBoardDataRepository saveBoardDataRepository;

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
}
