package org.choongang.product.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.choongang.commons.entities.Base;
import org.choongang.member.entities.Farmer;

@Data
@Builder
@Entity
@NoArgsConstructor @AllArgsConstructor
@Table(indexes = @Index(name="idx_category_listOrder", columnList = "listOrder DESC, createdAt DESC"))
public class Category extends Base {
    @Id
    @Column(length=30)
    private String cateCd; // 분류코드 -> 회원아이디_분류코드

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="farmer_seq")
    private Farmer farmer;

    @Column(length=60, nullable = false)
    private String cateNm; // 분류명

    private int listOrder; // 진열 가중치

    private boolean active; // 사용 여부
}
