package org.choongang.paging;

import org.choongang.commons.Pagination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PagingTest {

    @Test
    @DisplayName("페이지 구간별 데이터 테스트")
    void pagingTest() {
        Pagination pagination = new Pagination(23, 12345, 5, 20);
        List<String[]> data = pagination.getPages();
        data.forEach(s -> System.out.println(Arrays.toString(s)));
        System.out.println(pagination);

        int totalPages = (int)Math.ceil(pagination.getTotal() / (double)pagination.getLimit());

        assertEquals(totalPages, pagination.getTotalPages());
    }
}
