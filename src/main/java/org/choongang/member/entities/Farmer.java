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

    @Column(length=15)
    private String businessPermitNum; // 사업자 번호

    @Transient
    private FileInfo businessPermit;

}
