package org.choongang.center.repositories;

import org.choongang.center.entities.CenterInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//DB 접근 관련 코드
//JpaRepository<사용테이블(엔티티), Pk타입> 상속: 기본적인 CRUD 사용 가능
@Repository
public interface CenterInfoRepository extends JpaRepository<CenterInfo, Long>, QuerydslPredicateExecutor<CenterInfo> {
    List<CenterInfo> findAll();
    Optional<CenterInfo> findBycCode(Long cCode);

}
