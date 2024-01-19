package org.choongang.board.service.comment;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.choongang.board.controllers.comment.RequestComment;
import org.choongang.board.entities.BoardData;
import org.choongang.board.entities.CommentData;
import org.choongang.board.entities.QCommentData;
import org.choongang.board.repositories.BoardDataRepository;
import org.choongang.board.repositories.CommentDataRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;

@Service
@RequiredArgsConstructor
public class CommentInfoService {

    private final CommentDataRepository commentDataRepository;
    private final BoardDataRepository boardDataRepository;

    /**
     * 댓글 단일 조회
     *
     * @param seq : 댓글 번호
     * @return
     */
    public CommentData get(Long seq) {
        CommentData data = commentDataRepository.findById(seq).orElseThrow(CommentNotFoundException::new);

        return data;
    }

    public RequestComment getForm(Long seq) {
        CommentData data = get(seq);
        RequestComment form = new ModelMapper().map(data, RequestComment.class);

        form.setBoardDataSeq(data.getBoardData().getSeq());

        return form;
    }

    /**
     * 게시글별 댓글 목록 조회
     *
     * @param boardDataSeq
     * @return
     */
    public List<CommentData> getList(Long boardDataSeq) {
        QCommentData commentData = QCommentData.commentData;
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(commentData.boardData.seq.eq(boardDataSeq));

        List<CommentData> items = (List<CommentData>)commentDataRepository.findAll(andBuilder, Sort.by(desc("listOrder"), asc("createdAt")));

        return items;
    }

    /**
     * 게시글별 댓글 수 업데이트
     *
     * @param boardDataSeq : 게시글 번호
     */
    public void updateCommentCount(Long boardDataSeq) {
        BoardData data = boardDataRepository.findById(boardDataSeq).orElse(null);
        if (data == null) {
            return;
        }

        int total = commentDataRepository.getTotal(boardDataSeq);

        data.setCommentCount(total);

        boardDataRepository.flush();

    }
}
