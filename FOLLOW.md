# 팔로잉 

## 엔티티 구성 

> member/entities/Follow.java

```java
package org.choongang.member.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.choongang.commons.entities.Base;

@Data
@Builder
@Entity
@Table(indexes = @Index(name="idx_follow", columnList = "followee, follower", unique = true))
@NoArgsConstructor @AllArgsConstructor
public class Follow extends Base {
    @Id @GeneratedValue
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="followee")
    private Member followee; // 팔로잉 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="follower")
    private Member follower; // 팔로우 회원

}
```

> member/entities/Member.java

```java

... 

public class Member extends Base {
    

}
```

> member/repositories/FollowRepository.java

```java
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
```


> member/service/follow/FollowService.java  

```java
package org.choongang.member.service.follow;

import lombok.RequiredArgsConstructor;
import org.choongang.member.MemberUtil;
import org.choongang.member.entities.Follow;
import org.choongang.member.entities.Member;
import org.choongang.member.repositories.FollowRepository;
import org.choongang.member.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final MemberUtil memberUtil;

    /**
     * 팔로잉
     *
     * @param follower : 팔로잉할 회원
     */
    public void follow(Member follower) {
        // 팔로잉 기능은 회원 전용 기능이므로 로그인상태가 아니라면 처리 안함
        if (!memberUtil.isLogin()) {
            return;
        }

        try {
            Member followee = memberUtil.getMember();

            Follow follow = Follow.builder()
                    .followee(followee)
                    .follower(follower)
                    .build();

            followRepository.saveAndFlush(follow);
        } catch (Exception e) {e.printStackTrace();} // 동일한 follow 데이터가 있으면 Unique 제약 조건 예외가 발생하므로 무시
    }

    public void follow(Long seq) {
        Member follower = memberRepository.findById(seq).orElse(null);
        if (follower == null) {
            return;
        }

        follow(follower);
    }

    /**
     * 언팔로잉
     *
     * @param follower : 팔로잉을 취소할 회원
     */
    public void unfollow(Member follower) {
        // 언팔로잉 기능은 회원 전용 기능이므로 로그인상태가 아니라면 처리 안함
        if (!memberUtil.isLogin()) {
            return;
        }

        if (follower == null) {
            return;
        }

        Member followee = memberUtil.getMember();

        Follow follow = followRepository.findByFolloweeAndFollower(followee, follower);
        followRepository.delete(follow);
        followRepository.flush();
    }

    public void unfollow(Long seq) {
        Member follower = memberRepository.findById(seq).orElse(null);
        if (follower == null) {
            return;
        }

        unfollow(follower);
    }

    /**
     * 로그인 회원을 follow 한 회원 목록
     * @return
     */
    public List<Member> getFollowers() {
        if (!memberUtil.isLogin()) {
            return null;
        }

        return followRepository.getFollowers(memberUtil.getMember());
    }

    /**
     * 로그인 회원이 follow한 회원목록
     * 
     * @return
     */
    public List<Member> getFollowings() {
        if (!memberUtil.isLogin()) {
            return null;
        }

        return followRepository.getFollowings(memberUtil.getMember());
    }
}
```

> src/test/java/.../member/follow/FollowTest.java : 서비스 및 레포지토리 동작 테스트 

```java
package org.choongang.member.follow;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.choongang.member.entities.Member;
import org.choongang.member.repositories.FollowRepository;
import org.choongang.member.repositories.MemberRepository;
import org.choongang.member.service.follow.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@TestPropertySource(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
@DisplayName("팔로우, 팔로잉 테스트")
public class FollowTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private FollowService followService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HttpSession session;

    private Member member1;
    private Member member2;
    private Member member3;

    @BeforeEach
    void init() {
        List<Member> members = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Member member = new Member();
            member.setUserId("user" + i);
            member.setGid("gid");
            member.setPassword("12345");
            member.setEmail("user" + i + "@test.org");
            member.setName("사용자" + i);
            members.add(member);
        }

        memberRepository.saveAllAndFlush(members);

        member1 = members.get(0);
        member2 = members.get(1);
        member3 = members.get(2);

        session.setAttribute("member", member1);

        followService.follow(member2);
        followService.follow(member3);
    }

    /**
     * 테스트 시나리오1
     *
     * 1. member1은 member2, member3을 following 한다.
     *      member1에서 getFollowers 에서 member2와 member3이 나와야 하고
     *                  getTotalFollowers 에서는 2가 나와야 한다.
     */
    @Test
    @DisplayName("테스트 시나리오1")
    void test1() {

        List<Member> members = followRepository.getFollowings(member1);
        System.out.println(members);
        assertTrue(members.stream().map(Member::getUserId).anyMatch(u -> u.equals("user2") || u.equals("user3")));
        assertEquals(members.size(), followRepository.getTotalFollowings(member1));
    }

    /**
     * 테스트 시나리오2
     *
     * member2, member3는 각각 member1이라는 follower를 가지고 있어야 하고 getTotalFollowers()는 1명이 되어야 함
     */
    @Test
    @DisplayName("테스트 시나리오2")
    void test2() {
        List<Member> members1 = followRepository.getFollowers(member2);
        List<Member> members2 = followRepository.getFollowers(member3);

        assertEquals("user1", members1.get(0).getUserId());
        assertEquals("user1", members2.get(0).getUserId());
        assertEquals(1, followRepository.getTotalFollowers(member2));
        assertEquals(1, followRepository.getTotalFollowers(member3));
    }
    
    @Test
    @DisplayName("로그인 회원을 follow한 회원 목록 테스트 - followers")
    void test3() {
        List<Member> members = followService.getFollowers();

        assertEquals(0, members.size());
    }

    @Test
    @DisplayName("로그인 회원이 follow한 회원 목록 테스트 - followings")
    void test4() {
        List<Member> members = followService.getFollowings();
        assertEquals(2, members.size());
    }
}
```

이상이 없다면 다음과 같이 테스트 케이스가 통과 되어야 합니다.

