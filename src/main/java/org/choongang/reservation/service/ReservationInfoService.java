package org.choongang.reservation.service;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.service.CenterInfoService;
import org.choongang.commons.ListData;
import org.choongang.commons.Pagination;
import org.choongang.commons.Utils;
import org.choongang.member.MemberUtil;
import org.choongang.reservation.controllers.RequestReservation;
import org.choongang.reservation.controllers.ReservationSearch;
import org.choongang.reservation.entities.QReservation;
import org.choongang.reservation.entities.Reservation;
import org.choongang.reservation.repositories.ReservationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.Sort.Order.desc;

@Service
@RequiredArgsConstructor
public class ReservationInfoService {
    private final ReservationRepository reservationRepository;
    private final CenterInfoService centerInfoService;
    private final HttpServletRequest request;
    private final MemberUtil memberUtil;

    public Reservation get(Long bookCode) {
        Reservation reservation = reservationRepository.findById(bookCode).orElseThrow(ReservationNotFoundException::new);

        return reservation;
    }

    public RequestReservation getForm(Long bookCode) {
        Reservation reservation = get(bookCode);
        RequestReservation form = new ModelMapper().map(reservation, RequestReservation.class);

        form.setStatus(reservation.getStatus().name());
        form.setBookType(reservation.getBookType().name());

        LocalDateTime bookDateTime = reservation.getBookDateTime();
        form.setDate(bookDateTime.toLocalDate());
        form.setTime(bookDateTime.toLocalTime());
        form.setPersons(reservation.getCapacity());
        form.setCCode(reservation.getCenter().getCCode());
        form.setMember(reservation.getMember());

        return form;
    }

    public ListData<Reservation> getList(ReservationSearch search) {

        QReservation reservation = QReservation.reservation;

        BooleanBuilder andBuilder = new BooleanBuilder();

        //검색 조건 키워드들
        List<Long> memberSeq = search.getMemberSeq();
        List<Long> bookCode = search.getBookCode();
        List<String> userId = search.getUserId();
        LocalDate sDate = search.getSDate();
        LocalDate eDate = search.getEDate();


        //회원번호로 조회
        if(memberSeq != null && !memberSeq.isEmpty()) {
            andBuilder.and(reservation.member.seq.in(memberSeq));
        }

        if(bookCode != null && !bookCode.isEmpty()) {
            andBuilder.and(reservation.bookCode.in(bookCode));
        }

        if(userId != null && !userId.isEmpty()) {
            andBuilder.and(reservation.member.userId.in(userId));
        }

        if(sDate != null) {
            andBuilder.and(reservation.bookDateTime.goe(LocalDateTime.of(
                    sDate, LocalTime.of(0,0,0))));
        }

        if(eDate != null) {
            andBuilder.and(reservation.bookDateTime.loe(LocalDateTime.of(eDate, LocalTime.of(23,59,59))));

        }

        String sopt = search.getSopt();
        String skey = search.getSkey();

        sopt = StringUtils.hasText(sopt) ? sopt : "all";
        if (StringUtils.hasText(skey)) {
            skey = skey.trim();

        }
        //검색 조건 페이징

        int page = Utils.onlyPositiveNumber(search.getPage(),1);
        int limit = Utils.onlyPositiveNumber(search.getLimit(),20);

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt")));
        Page<Reservation> data = reservationRepository.findAll(andBuilder, pageable);

        Pagination pagination = new Pagination(page, (int)data.getTotalElements(), 10, limit, request);

        return new ListData<>(data.getContent(), pagination);
    }

    public ListData<Reservation> getMyList(ReservationSearch search) {
        if (!memberUtil.isLogin()) {
            return null;
        }

        search.setUserId(Arrays.asList(memberUtil.getMember().getUserId()));

        return getList(search);
    }

    public int getAvailableCapacity(Long cCode, LocalDateTime bookDateTime) {

        CenterInfo center = centerInfoService.get(cCode);

        Integer current = reservationRepository.getTotalCapacity(cCode, bookDateTime);

        int curr = Objects.requireNonNullElse(current, 0);

        return center.getBookCapacity() - curr;
    }
}
