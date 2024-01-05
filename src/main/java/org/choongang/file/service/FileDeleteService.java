package org.choongang.file.service;

import lombok.RequiredArgsConstructor;
import org.choongang.member.MemberUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileDeleteService {

    private final FileInfoService infoService;
    private final MemberUtil memberUtil;

    public void delete(Long seq) {

    }
}
