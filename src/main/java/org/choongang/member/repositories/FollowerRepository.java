package org.choongang.member.repositories;

import org.choongang.member.entities.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface FollowerRepository extends JpaRepository<Follower, Long>, QuerydslPredicateExecutor<Follower> {

}
