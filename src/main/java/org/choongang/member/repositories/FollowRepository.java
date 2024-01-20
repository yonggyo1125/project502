package org.choongang.member.repositories;

import org.choongang.member.entities.Follow;
import org.choongang.member.entities.Member;
import org.choongang.member.entities.QFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long>, QuerydslPredicateExecutor<Follow> {
    Follow findByFolloweeAndFollower(Member followee, Member follower);

    // member가 follow 하는 회원 수
    default long getTotalFollowings(Member member) {
        QFollow follow = QFollow.follow;

        return count(follow.followee.eq(member));
    }

    // member를 follow 하는 회원 수
    default long getTotalFollowers(Member member) {
        QFollow follow = QFollow.follow;

        return count(follow.follower.eq(member));
    }

    // member가 follow 하는 회원 목록
    default List<Member> getFollowings(Member member) {
        QFollow follow = QFollow.follow;

        List<Follow> items = (List<Follow>)findAll(follow.followee.eq(member));

        if (items != null) {
            return items.stream().map(Follow::getFollower).toList();
        }

        return null;
    }

    // member를 follow 하는 회원 목록
    default List<Member> getFollowers(Member member) {
        QFollow follow = QFollow.follow;

        List<Follow> items = (List<Follow>)findAll(follow.follower.eq(member));

        if (items != null) {
            return items.stream().map(Follow::getFollowee).toList();
        }

        return null;
    }
}
