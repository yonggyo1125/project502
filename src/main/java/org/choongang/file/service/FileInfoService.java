package org.choongang.file.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.choongang.configs.FileProperties;
import org.choongang.file.entities.FileInfo;
import org.choongang.file.repositories.FileInfoRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileInfoService {

    private final FileProperties fileProperties;
    private final FileInfoRepository repository;
    private final HttpServletRequest request;

    public FileInfo get(Long seq) {
        FileInfo fileInfo = repository.findById(seq)
                            .orElseThrow(FileNotFoundException::new);


        addFileInfo(fileInfo); // 파일 추가 정보 처리

        return fileInfo;
    }

    /**
     * 파일 추가 정보 처리
     *      - 파일 서버 경로(filePath)
     *      - 파일 URL(fileUrl)
     * @param fileInfo
     */
    public void addFileInfo(FileInfo fileInfo) {
        long seq = fileInfo.getSeq();
        long dir = seq % 10L;
        String fileName = seq + fileInfo.getExtension();

        String filePath = fileProperties.getPath() + dir + "/" + fileName;
        String fileUrl = request.getContextPath() + "/" + dir + "/" + fileName;

        fileInfo.setFilePath(filePath);
        fileInfo.setFileUrl(fileUrl);

    }
}
