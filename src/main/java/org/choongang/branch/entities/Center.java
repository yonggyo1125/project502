package org.choongang.branch.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.choongang.commons.entities.BaseMember;

@Data
@Entity
public class Center extends BaseMember {
    @Id @GeneratedValue
    private Long cCode; // 지점 코드

    @Column(length=80, nullable = false)
    private String cName; // 지점명

    @Column(length=10, nullable = false)
    private String zonecode; // 우편번호

    @Column(length=100, nullable = false)
    private String address; // 주소

    private String addressSub; // 나머지 주소

    @Column(length=15, nullable = false)
    private String telNum; // 전화번호

    @Column(length=80, nullable = false)
    private String operHour; // 운영시간

    @Column(length=40)
    private String bookYoil; //  예약 요일 설정

    @Column(length=20)
    private String bookAvl; // 예약 가능 시간  09:00-18:00

    /**
     * 13:00-14:00
     * 15:30-16:00
     */
    @Lob
    private String bookNotAvl; // 예약 불가 시간

    private boolean bookHday; // 공휴일 예약 가능 여부

    private int bookBlock; // 예약 블록 10, 20, 30

    private int bookCapacity; // -1이면 무제한, 0이면 예약 불가
}
