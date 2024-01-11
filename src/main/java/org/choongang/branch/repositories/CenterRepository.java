package org.choongang.branch.repositories;

import org.choongang.branch.entities.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CenterRepository extends JpaRepository<Center, Long>, QuerydslPredicateExecutor<Center> {
}
