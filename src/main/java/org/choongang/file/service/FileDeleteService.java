package org.choongang.file.service;

import lombok.RequiredArgsConstructor;
import org.choongang.file.entities.FileInfo;
import org.choongang.member.MemberUtil;
import org.choongang.member.entities.Member;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileDeleteService {

    private final FileInfoService infoService;
    private final MemberUtil memberUtil;

    public void delete(Long seq) {
        FileInfo data = infoService.get(seq);

        // 파일 삭제 권한 체크
        Member member = memberUtil.getMember();

        if (!memberUtil.isAdmin()) {

        }

    }
}
