package org.choongang.member.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.choongang.commons.entities.Base;

@Entity
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class Address extends Base {
    @Id @GeneratedValue
    private Long seq;

    @ManyToOne
    @JoinColumn(name="member_seq")
    private AbstractMember member;

    @Column(length=60, nullable = false)
    private String title; // 주소 명칭

    @Column(length=10, nullable = false)
    private String zonecode;

    @Column(length=100, nullable = false)
    private String address;

    @Column(length=100)
    private String addressSub;

    private boolean defaultAddress; // 기본 주소

}
