package org.choongang.reservation.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.choongang.branch.entities.Center;
import org.choongang.commons.entities.Base;
import org.choongang.member.entities.Member;
import org.choongang.reservation.constants.DonationType;

import java.time.LocalDateTime;

@Data
@Entity
public class Reservation extends Base {
    @Id @GeneratedValue
    private Long bookCode; // 예약 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_seq")
    private Member member;

    @Column(length=15, nullable = false)
    private String donnerTel; // 헌혈자 전화번호


    @Enumerated(EnumType.STRING)
    @Column(length=25, nullable = false)
    private DonationType bookType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cCode")
    private Center center;

    private LocalDateTime bookDateTime;
}
