package org.choongang.commons;

public class Pagination {

    private int page;
    private int total;
    private int ranges;
    private int limit;

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

        this.page = page;
        this.total = total;
        this.ranges = ranges;
        this.limit = limit;
    }
}
