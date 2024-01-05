package org.choongang.file.service;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.choongang.configs.FileProperties;
import org.choongang.file.entities.FileInfo;
import org.choongang.file.entities.QFileInfo;
import org.choongang.file.repositories.FileInfoRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.asc;

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
     * 파일 목록 조회
     *
     * @param gid
     * @param location
     * @param mode - ALL : 기본값 - 완료, 미완료 모두 조회
     *               DONE : 완료된 파일
     *               UNDONE : 미완료된 파일
     * @return
     */
    public List<FileInfo> getList(String gid, String location, String mode) {
        QFileInfo fileInfo = QFileInfo.fileInfo;

        mode = StringUtils.hasText(mode) ? mode : "ALL";

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(fileInfo.gid.eq(gid));

        if (StringUtils.hasText(location)) {
            builder.and(fileInfo.location.eq(location));
        }

        if (!mode.equals("ALL")) {
            builder.and(fileInfo.done.eq(mode.equals("DONE")));
        }

        List<FileInfo> items = (List<FileInfo>)repository.findAll(builder, Sort.by(asc("createdAt")));

        items.forEach(this::addFileInfo);

        return items;
    }

    public List<FileInfo> getList(String gid) {
        return getList(gid, null, "ALL");
    }

    public List<FileInfo> getList(String gid, String location) {
        return getList(gid, location, "ALL");
    }

    public List<FileInfo> getListDone(String gid) {
        return getList(gid, null, "DONE");
    }

    public List<FileInfo> getListDone(String gid, String location) {
        return getList(gid, location, "DONE");
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
        String fileUrl = request.getContextPath() + fileProperties.getUrl() + dir + "/" + fileName;

        fileInfo.setFilePath(filePath);
        fileInfo.setFileUrl(fileUrl);

    }
}
