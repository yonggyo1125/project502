package org.choongang.board.service.comment;

import lombok.RequiredArgsConstructor;
import org.choongang.board.entities.CommentData;
import org.choongang.board.repositories.CommentDataRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentDeleteService {
    private final CommentDataRepository commentDataRepository;
    private final CommentInfoService commentInfoService;

    public CommentData delete(Long seq) {

        CommentData data = commentInfoService.get(seq);
        CommentData returnData = new ModelMapper().map(data, CommentData.class);

        commentDataRepository.delete(data);
        commentDataRepository.flush();

        return returnData;
    }
}
