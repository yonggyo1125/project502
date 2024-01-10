package org.choongang.commons;

import java.util.List;
import java.util.stream.IntStream;

public class Pagination {

    private int page;
    private int total;
    private int ranges;
    private int limit;

    private int firstRangePage; // 구간별 첫 페이지
    private int lastRangePage; // 구간별 마지막 페이지

    private int prevRangePage; // 이전 구간 첫 페이지 번호
    private int nextRangePage; // 다음 구간 첫 페이지 번호

    /**
     *
     * @param page : 현재 페이지
     * @param total : 전체 레코드 갯수
     * @param ranges : 페이지 구간 갯수
     * @param limit : 1페이지 당 레코드 갯수
     */
    public Pagination(int page, int total, int ranges, int limit) {

        page = Utils.onlyPositiveNumber(page, 1);
        total = Utils.onlyPositiveNumber(total, 0);
        ranges = Utils.onlyPositiveNumber(ranges, 10);
        limit = Utils.onlyPositiveNumber(limit, 20);

        // 전체 페이지 갯수
        int totalPages = (int)Math.ceil(total / (double)limit);


        // 구간 번호
        int rangeCnt = (page - 1) / ranges;
        int firstRangePage = rangeCnt * ranges + 1;
        int lastRangePage = firstRangePage + ranges - 1;
        lastRangePage = lastRangePage > totalPages ? totalPages : lastRangePage;

        // 이전 구간 첫 페이지
        if (rangeCnt > 0) {
            prevRangePage = firstRangePage - ranges;
        }

        // 다음 구간 첫 페이지
        // 마지막 구간 번호
        int lastRangeCnt = (totalPages - 1) / ranges;
        if (rangeCnt < lastRangeCnt) { // 마지막 구간이 아닌 경우 다음 구간 첫 페이지 계산
            nextRangePage = firstRangePage + ranges;
        }

        this.page = page;
        this.total = total;
        this.ranges = ranges;
        this.limit = limit;
        this.prevRangePage = firstRangePage;
        this.nextRangePage = lastRangePage;
    }

    public List<String[]> getPages() {
        // 0 : 페이지 번호, 1 : 페이지 URL - ?page=페이지번호

        List<String[]> data = IntStream.rangeClosed()

    }
}
