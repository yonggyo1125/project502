package org.choongang.member.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Data;
import org.choongang.file.entities.FileInfo;

@Data
@Entity
@DiscriminatorValue("F")
public class Farmer extends AbstractMember {
    @Column(length=90, nullable = false)
    private String farmTitle; // 농장 주소

    @Column(length=10, nullable = false)
    private String farmZonecode; // 농장 우편번호

    @Column(length=100, nullable = false)
    private String farmAddress; // 농장 주소

    @Column(length=100)
    private String farmAddressSub; // 농장 나머지 주소

    @Transient
    private FileInfo businessPermit;

}
