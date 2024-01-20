package org.choongang.member.repositories;

import org.choongang.member.entities.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface FollowingRepository extends JpaRepository<Following, Long>, QuerydslPredicateExecutor<Following> {
}
