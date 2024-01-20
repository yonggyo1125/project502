package org.choongang.member.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.choongang.commons.entities.Base;

@Data
@Entity
@IdClass(FollowId.class)
public class Following extends Base {
    @Id
    private Long seq; // following 회원 번호

    @Id
    @ManyToOne
    @JoinColumn(name="memberSeq")
    private Member member;
}
