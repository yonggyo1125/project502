package org.choongang.board.service;

import lombok.RequiredArgsConstructor;
import org.choongang.board.entities.BoardData;
import org.choongang.commons.exceptions.UnAuthorizedException;
import org.choongang.member.entities.Member;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardAuthService {

    private final BoardInfoService infoService;

    /**
     * 게시글 관련 권한 체크
     *
     * @param mode
     *          edit, delete
     *
     * @param seq : 게시글 번호
     */
    public void check(String mode, Long seq) {
        BoardData data = infoService.get(seq);

        if ((mode.equals("edit") && !data.isEditable())
                || (mode.equals("delete") && !data.isDeletable())) {
            Member member = data.getMember();

            // 비회원 -> 비밀번호 확인 필요
            if (member == null) {
                throw new GuestPasswordCheckException();
            }

            // 회원인 경우 -> alert -> back
            throw new UnAuthorizedException();
        }
    }
}
