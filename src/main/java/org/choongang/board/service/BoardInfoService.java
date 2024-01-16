package org.choongang.board.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.choongang.board.entities.BoardData;
import org.choongang.board.repositories.BoardDataRepository;
import org.choongang.file.entities.FileInfo;
import org.choongang.file.service.FileInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardInfoService {

    private final EntityManager em;
    private final BoardDataRepository boardDataRepository;
    private final FileInfoService fileInfoService;

    /**
     * 게시글 조회
     *
     * @param seq : 게시글 번호
     * @return
     */
    public BoardData get(Long seq) {
        BoardData boardData = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

        addBoardData(boardData);

        return boardData;
    }

    /**
     * 게시글 추가 정보 처리
     *
     * @param boardData
     */
    public void addBoardData(BoardData boardData) {
        String gid = boardData.getGid();

        List<FileInfo> editorFiles = fileInfoService.getListDone(gid, "editor");
        List<FileInfo> attachFiles = fileInfoService.getListDone(gid, "attach");

        boardData.setEditorFiles(editorFiles);
        boardData.setAttachFiles(attachFiles);
    }
}
