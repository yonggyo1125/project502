package org.choongang.member.repositories;

import org.choongang.member.entities.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface FarmerRepository extends JpaRepository<Farmer, Long>, QuerydslPredicateExecutor<Farmer> {
}
