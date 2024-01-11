package org.choongang.board.service.config;

import lombok.RequiredArgsConstructor;
import org.choongang.admin.board.controllers.RequestBoardConfig;
import org.choongang.board.entities.Board;
import org.choongang.board.repositories.BoardRepository;
import org.choongang.file.entities.FileInfo;
import org.choongang.file.service.FileInfoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardConfigInfoService {
    private final BoardRepository boardRepository;
    private final FileInfoService fileInfoService;

    /**
     * 게시판 설정 조회
     *
     * @param bid
     * @return
     */
    public Board get(String bid) {
        Board board = boardRepository.findById(bid).orElseThrow(BoardNotFoundException::new);

        addBoardInfo(board);

        return board;

    }

    public RequestBoardConfig getForm(String bid) {
        Board board = get(bid);

        RequestBoardConfig form = new ModelMapper().map(board, RequestBoardConfig.class);
        form.setListAccessType(board.getListAccessType().name());
        form.setViewAccessType(board.getViewAccessType().name());
        form.setWriteAccessType(board.getWriteAccessType().name());
        form.setReplyAccessType(board.getReplyAccessType().name());
        form.setCommentAccessType(board.getCommentAccessType().name());

        form.setMode("edit");

        return form;
    }

    /**
     * 게시판 설정 추가 정보
     *      - 에디터 첨부 파일 목록
     * @param board
     */
    public void addBoardInfo(Board board) {
        String gid = board.getGid();

        List<FileInfo> htmlTopImages = fileInfoService.getListDone(gid, "htmlTop");

        List<FileInfo> htmlBottomImages = fileInfoService.getListDone(gid, "htmlBottom");

        board.setHtmlTopImages(htmlTopImages);
        board.setHtmlBottomImages(htmlBottomImages);
    }
}
