package org.choongang.board.service;

import lombok.RequiredArgsConstructor;
import org.choongang.board.entities.BoardData;
import org.choongang.commons.exceptions.UnAuthorizedException;
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
            throw new UnAuthorizedException();
        }
    }
}
