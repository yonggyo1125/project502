package org.choongang.board.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.choongang.board.controllers.RequestBoard;
import org.choongang.board.entities.Board;
import org.choongang.board.entities.BoardData;
import org.choongang.board.repositories.BoardDataRepository;
import org.choongang.board.repositories.BoardRepository;
import org.choongang.file.service.FileUploadService;
import org.choongang.member.MemberUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BoardSaveService {

    private final BoardAuthService boardAuthService;
    private final BoardRepository boardRepository;
    private final BoardDataRepository boardDataRepository;
    private final FileUploadService fileUploadService;
    private final MemberUtil memberUtil;
    private final HttpServletRequest request;

    private final PasswordEncoder encoder;

    public BoardData save(RequestBoard form) {

        String mode = form.getMode();
        mode = StringUtils.hasText(mode) ? mode : "write";

        Long seq = form.getSeq();

        // 수정 권한 체크
        if (mode.equals("update")) {
            boardAuthService.check(mode, seq);
        }

        BoardData data = null;
        if (seq != null && mode.equals("update")) { // 글 수정
            data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);
        } else { // 글 작성
            data = new BoardData();
            data.setGid(form.getGid());
            data.setIp(request.getRemoteAddr());
            data.setUa(request.getHeader("User-Agent"));
            data.setMember(memberUtil.getMember());

            Board board = boardRepository.findById(form.getBid()).orElse(null);
            data.setBoard(board);
            Long parentSeq = form.getParentSeq();
            data.setParentSeq(parentSeq); // 부모 게시글 번호

            long listOrder = parentSeq == null ?
                            System.currentTimeMillis() :
                            getReplyListOrder(parentSeq);
            data.setListOrder(listOrder);
        }

        data.setPoster(form.getPoster());
        data.setSubject(form.getSubject());
        data.setContent(form.getContent());
        data.setCategory(form.getCategory());
        data.setEditorView(data.getBoard().isUseEditor());


        // 추가 필드 - 정수
        data.setNum1(form.getNum1());
        data.setNum2(form.getNum2());
        data.setNum3(form.getNum3());

        // 추가 필드 - 한줄 텍스트
        data.setText1(form.getText1());
        data.setText2(form.getText2());
        data.setText3(form.getText3());

        // 추가 필드 - 여러줄 텍스트
        data.setLongText1(form.getLongText1());
        data.setLongText2(form.getLongText2());
        data.setLongText3(form.getLongText3());

        // 비회원 비밀번호
        String guestPw = form.getGuestPw();
        if (StringUtils.hasText(guestPw)) {
            String hash = encoder.encode(guestPw);
            data.setGuestPw(hash);
        }

        // 공지글 처리 - 관리자만 가능
        if (memberUtil.isLogin()) {
            data.setNotice(form.isNotice());
        }

        boardDataRepository.saveAndFlush(data);

        // 파일 업로드 완료 처리
        fileUploadService.processDone(data.getGid());

        return data;
    }


    /**
     * 답글 정렬 순서 번호 listOrder
     * @param parentSeq
     * @return
     */
    private long getReplyListOrder(Long parentSeq) {
        BoardData data = boardDataRepository.findById(parentSeq).orElse(null);
        if (data == null) {
            return System.currentTimeMillis();
        }

        /**
         * 답글이 이미 있는 경우 -> 마지막 답글 순서에서  -1
         * 답글이 하나도 없는 경우 -> 부모 게시글 순서에서 -1
         */
        Long lastListOrder = boardDataRepository.getLastReplyListOrder(parentSeq);
        if (lastListOrder == null || lastListOrder.longValue() == 0L) { // 답글이 없는 경우
            return data.getListOrder().longValue() - 1000;

        } else { // 답글이 있는 경우
            return lastListOrder.longValue() - 1;
        }
    }
}
