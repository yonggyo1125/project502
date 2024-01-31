package org.choongang.board.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.board.entities.BoardData;
import org.choongang.board.service.BoardInfoService;
import org.choongang.board.service.SaveBoardDataService;
import org.choongang.commons.ListData;
import org.choongang.commons.rests.JSONData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class ApiBoardController {

    private final SaveBoardDataService saveBoardDataService;
    private final BoardInfoService boardInfoService;

    @GetMapping("/save_post/{bSeq}")
    public JSONData<Object> savePost(@PathVariable("bSeq") Long bSeq) {
        saveBoardDataService.save(bSeq);

        return new JSONData<>();
    }

    @DeleteMapping("/save_post/{bSeq}")
    public JSONData<Object> deleteSavePost(@PathVariable("bSeq") Long bSeq) {
        saveBoardDataService.delete(bSeq);

        return new JSONData<>();
    }

    @GetMapping("/view_post")
    public JSONData<List<BoardData>> viewPost(@RequestParam("seq") List<Long> seqs) {

        BoardDataSearch search = new BoardDataSearch();
        search.setLimit(10000);
        search.setSeq(seqs);

        ListData<BoardData> data = boardInfoService.getList(search);

        return new JSONData<>(data.getItems());
    }
}
