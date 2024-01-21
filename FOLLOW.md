# 팔로잉 

## 회원 프로필 이미지 처리  

```java
...

public class MemberInfoService implements UserDetailsService {
    ...

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        ...
        
        // 추가 정보 처리
        addMemberInfo(member);

        return MemberInfo.builder()
                .email(member.getEmail())
                .userId(member.getUserId())
                .password(member.getPassword())
                .member(member)
                .authorities(authorities)
                .build();
      
    }
    
    ...
    
    /**
     * 회원 추가 정보 처리
     *
     * @param member
     */
    public void addMemberInfo(Member member) {
        /* 프로필 이미지 처리 S */
        List<FileInfo> files = fileInfoService.getListDone(member.getGid());
        if (files != null && !files.isEmpty()) {
            member.setProfileImage(files.get(0));
        }
        /* 프로필 이미지 처리 E */
    }
}
```


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

> commons/RequestPaging.java

```java
package org.choongang.commons;

import lombok.Data;

@Data
public class RequestPaging {
    protected int page  = 1;
    protected int limit = 20;
}
```

> member/repositories/FollowRepository.java

```java
package org.choongang.member.repositories;

import jakarta.servlet.http.HttpServletRequest;
import org.choongang.commons.ListData;
import org.choongang.commons.Pagination;
import org.choongang.commons.RequestPaging;
import org.choongang.commons.Utils;
import org.choongang.member.entities.Follow;
import org.choongang.member.entities.Member;
import org.choongang.member.entities.QFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.desc;

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
    default ListData<Member> getFollowings(Member member, RequestPaging paging, HttpServletRequest request) {

        int page = Utils.onlyPositiveNumber(paging.getPage(), 1);
        int limit = Utils.onlyPositiveNumber(paging.getLimit(), 20);

        QFollow follow = QFollow.follow;

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt")));

        Page<Follow> data = findAll(follow.followee.eq(member), pageable);
        List<Follow> follows = data.getContent();
        List<Member> items = null;
        if (follows != null) {
            items = follows.stream().map(Follow::getFollower).toList();
        }

        Pagination pagination = new Pagination(page, (int)data.getTotalElements(), 10, limit);

        return new ListData<>(items, pagination);
    }

    // member를 follow 하는 회원 목록
    default ListData<Member> getFollowers(Member member, RequestPaging paging, HttpServletRequest request) {

        int page = Utils.onlyPositiveNumber(paging.getPage(), 1);
        int limit = Utils.onlyPositiveNumber(paging.getLimit(), 20);

        QFollow follow = QFollow.follow;

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt")));

        Page<Follow> data = findAll(follow.follower.eq(member), pageable);
        List<Follow> follows = data.getContent();
        List<Member> items = null;
        if (follows != null) {
            items = follows.stream().map(Follow::getFollowee).toList();
        }

        Pagination pagination = new Pagination(page, (int)data.getTotalElements(), 10, limit, request);

        return new ListData<>(items, pagination);
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

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.choongang.commons.ListData;
import org.choongang.commons.RequestPaging;
import org.choongang.member.MemberUtil;
import org.choongang.member.entities.Follow;
import org.choongang.member.entities.Member;
import org.choongang.member.entities.QFollow;
import org.choongang.member.repositories.FollowRepository;
import org.choongang.member.repositories.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final MemberUtil memberUtil;
    private final HttpServletRequest request;

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

            // 팔로워과 팔로잉 하는 사용자가 같을 수 없으므로 체크
            if (follower.getUserId().equals(followee.getUserId())) {
                return;
            }

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

    public void follow(String userId) {
        Member follower = memberRepository.findByUserId(userId).orElse(null);

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

    public void unfollow(String userId) {
        Member follower = memberRepository.findByUserId(userId).orElse(null);
        if (follower == null) {
            return;
        }

        unfollow(follower);
    }

    /**
     * 로그인 회원을 follow 한 회원 목록
     * @return
     */
    public ListData<Member> getFollowers(RequestPaging paging) {
        if (!memberUtil.isLogin()) {
            return null;
        }

        return followRepository.getFollowers(memberUtil.getMember(), paging, request);
    }

    /**
     * 로그인 회원이 follow한 회원목록
     *
     * @return
     */
    public ListData<Member> getFollowings(RequestPaging paging) {
        if (!memberUtil.isLogin()) {
            return null;
        }

        return followRepository.getFollowings(memberUtil.getMember(), paging, request);
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


    public long getTotalFollowers() {

        if (memberUtil.isLogin()) {
            return followRepository.getTotalFollowers(memberUtil.getMember());
        }

        return 0L;
    }

    public long getTotalFollowings() {

        if (memberUtil.isLogin()) {
            return followRepository.getTotalFollowings(memberUtil.getMember());
        }

        return 0L;
    }

    /**
     * 팔로우, 팔로잉 목록
     * @param mode : follower - 팔로워 회원 목록, following : 팔로잉 회원 목록
     * @param paging
     * @return
     */
    /**
     * 팔로우, 팔로잉 목록
     * @param mode : follower - 팔로워 회원 목록, following : 팔로잉 회원 목록
     * @param paging
     * @return
     */
    public ListData<Member> getList(String mode, RequestPaging paging) {
        mode = StringUtils.hasText(mode) ? mode : "follower";

        ListData<Member> data = mode.equals("following") ? getFollowings(paging) : getFollowers(paging);
        data.getItems().forEach(memberInfoService::addMemberInfo);

        return data;
    }

    /**
     * 팔로잉 상태인지 체크
     *
     * @param userId
     * @return
     */
    public boolean followed(String userId) {
        if (!memberUtil.isLogin()) {
            return false;
        }

        QFollow follow = QFollow.follow;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(follow.follower.userId.eq(userId))
                .and(follow.followee.in(memberUtil.getMember()));

        return followRepository.exists(builder);
    }
}
```

> src/test/java/.../member/follow/FollowTest.java : 서비스 및 레포지토리 동작 테스트 

```java
package org.choongang.member.follow;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.choongang.commons.ListData;
import org.choongang.commons.RequestPaging;
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
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;

    private Member member1;
    private Member member2;
    private Member member3;

    private RequestPaging paging;

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

        paging = new RequestPaging();
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
        ListData<Member> members1 = followRepository.getFollowers(member2, paging, request);
        ListData<Member> members2 = followRepository.getFollowers(member3, paging, request);

        assertEquals("user1", members1.getItems().get(0).getUserId());
        assertEquals("user1", members2.getItems().get(0).getUserId());
        assertEquals(1, followRepository.getTotalFollowers(member2));
        assertEquals(1, followRepository.getTotalFollowers(member3));
    }

    @Test
    @DisplayName("로그인 회원을 follow한 회원 목록 테스트 - followers")
    void test3() {
        ListData<Member> members = followService.getFollowers(paging);

        assertEquals(0, members.getItems().size());
    }

    @Test
    @DisplayName("로그인 회원이 follow한 회원 목록 테스트 - followings")
    void test4() {
        ListData<Member> members = followService.getFollowings(paging);
        assertEquals(2, members.getItems().size());
    }

}

```

이상이 없다면 다음과 같이 테스트 케이스가 통과 되어야 합니다.

![image1](https://raw.githubusercontent.com/yonggyo1125/project502/master/images/follow/image1.png)



## 마이페이지 : 팔로잉, 팔로워 게시글 조회

> member/service/follow/FollowBoardService.java

```java
package org.choongang.member.service.follow;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.choongang.board.controllers.BoardDataSearch;
import org.choongang.board.entities.BoardData;
import org.choongang.board.entities.QBoardData;
import org.choongang.board.repositories.BoardDataRepository;
import org.choongang.commons.ListData;
import org.choongang.commons.Pagination;
import org.choongang.commons.Utils;
import org.choongang.member.MemberUtil;
import org.choongang.member.entities.Member;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 팔로잉 회원 게시글 목록 조회
 * 팔로워 회원 게시글 목록 조회
 */
@Service
@RequiredArgsConstructor
public class FollowBoardService {

    private final MemberUtil memberUtil;
    private final FollowService followService;
    private final BoardDataRepository boardDataRepository;
    private final HttpServletRequest request;
    private final EntityManager em;

    /**
     *
     * @param mode
     *              follower : 로그인 회원을 팔로잉 하는 회원 게시글 목록
     *              following : 로그인 회원이 팔로잉한 회원 게시글 목록
     * @param search
     * @return
     */
    public ListData<BoardData> getList(String mode, BoardDataSearch search) {

        if (!memberUtil.isLogin()) { // 미로그인 상태 목록 조회 X
            return null;
        }

        int page = Utils.onlyPositiveNumber(search.getPage(), 1);
        int limit = Utils.onlyPositiveNumber(search.getLimit(), 20);
        int offset = (page - 1) * limit;

        mode = StringUtils.hasText(mode) ? mode : "follower";
        List<Member> members = mode.equals("following") ? followService.getFollowings() : followService.getFollowers();

        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(boardData.member.in(members));

        /* 검색 조건 처리 S */

        String sopt = search.getSopt();
        String skey = search.getSkey();

        sopt = StringUtils.hasText(sopt) ? sopt.toUpperCase() : "ALL";

        if (StringUtils.hasText(skey)) {
            skey = skey.trim();

            BooleanExpression subjectCond = boardData.subject.contains(skey); // 제목 - subject LIKE '%skey%';
            BooleanExpression contentCond = boardData.content.contains(skey); // 내용 - content LIKE '%skey%';

            if (sopt.equals("SUBJECT")) { // 제목

                andBuilder.and(subjectCond);

            } else if (sopt.equals("CONTENT")) { // 내용

                andBuilder.and(contentCond);

            } else if (sopt.equals("SUBJECT_CONTENT")) { // 제목 + 내용

                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(subjectCond)
                        .or(contentCond);

                andBuilder.and(orBuilder);

            } else if (sopt.equals("POSTER")) { // 작성자 + 아이디 + 회원명
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(boardData.poster.contains(skey))
                        .or(boardData.member.userId.contains(skey))
                        .or(boardData.member.name.contains(skey));

                andBuilder.and(orBuilder);
            }

        }

        // 특정 사용자로 게시글 한정 : 마이페이지에서 활용 가능
        String userId = search.getUserId();
        if (StringUtils.hasText(userId)) {
            andBuilder.and(boardData.member.userId.eq(userId));
        }

        // 게시글 분류 조회
        String category = search.getCategory();
        if (StringUtils.hasText(category)) {
            category = category.trim();
            andBuilder.and(boardData.category.eq(category));
        }

        /* 검색 조건 처리 E */

        PathBuilder<BoardData> pathBuilder = new PathBuilder<>(BoardData.class, "boardData");
        List<BoardData> items = new JPAQueryFactory(em).selectFrom(boardData)
                .leftJoin(boardData.member)
                .fetchJoin()
                .where(andBuilder)
                .offset(offset)
                .limit(limit)
                .orderBy(new OrderSpecifier(Order.DESC, pathBuilder.get("createdAt")))
                .fetch();

        int total = (int)boardDataRepository.count(andBuilder);
        Pagination pagination = new Pagination(page, total, 10, limit, request);

        return new ListData<>(items, pagination);
    }
}
```

> commons/Utils.java

```java
...

public class Utils {
    ...

    public String backgroundStyle(FileInfo file, int width, int height) {

        String[] data = fileInfoService.getThumb(file.getSeq(), width, height);
        String imageUrl = data[1];

        String style = String.format("background:url('%s') no-repeat center center; background-size:cover;", imageUrl);

        return style;
    }
    
    ...
    
}
```

> resources/templates/front/layouts/mypage.html 

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <meta charset="UTF-8">
    <meta name="_csrf" th:content="${_csrf.token}">
    <meta name="_csrf_header" th:content="${_csrf.headerName}">
    <meta th:if="${siteConfig.siteDescription != null}" name="description" th:content="${siteConfig.siteDescription}">
    <meta th:if="${siteConfig.siteKeywords != null}" name="keywords" th:content="${siteConfig.siteKeywords}">
    <title>
        <th:block th:if="${pageTitle != null}" th:text="${pageTitle + ' - '}"></th:block>
        <th:block th:if="${siteConfig.siteTitle != null}" th:text="${siteConfig.siteTitle}"></th:block>
    </title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/xeicon@2.3.3/xeicon.min.css">
    <link rel="stylesheet" type="text/css" th:href="@{/common/css/style.css?v={v}(v=${siteConfig.cssJsVersion})}">
    <link rel="stylesheet" type="text/css"
          th:each="cssFile : ${addCommonCss}"
          th:href="@{/common/css/{file}.css?v={v}(file=${cssFile}, v=${siteConfig.cssJsVersion})}">

    <link rel="stylesheet" type="text/css" th:href="@{/front/css/style.css?v={v}(v=${siteConfig.cssJsVersion})}">
    <link rel="stylesheet" type="text/css" th:href="@{/front/css/mypage/style.css?v={v}(v=${siteConfig.cssJsVersion})}">

    <link rel="stylesheet" type="text/css"
          th:each="cssFile : ${addCss}"
          th:href="@{/front/css/{file}.css?v={v}(file=${cssFile}, v=${siteConfig.cssJsVersion})}">

    <th:block layout:fragment="addCss"></th:block>

    <script th:src="@{/common/js/common.js?v={v}(v=${siteConfig.cssJsVersion})}"></script>
    <script th:each="jsFile : ${addCommonScript}"
            th:src="@{/common/js/{file}.js?v={v}(file=${jsFile}, v=${siteConfig.cssJsVersion})}"></script>

    <script th:src="@{/front/js/common.js?v={v}(v=${siteConfig.cssJsVersion})}"></script>
    <script th:each="jsFile : ${addScript}"
            th:src="@{/front/js/{file}.js?v={v}(file=${jsFile}, v=${siteConfig.cssJsVersion})}"></script>

    <th:block layout:fragment="addScript"></th:block>
</head>
<body>
<header th:replace="~{front/outlines/header::common}"></header>

<main class="mypage layout_width">
    <aside th:replace="~{front/mypage/_side::menus}"></aside>
    <section layout:fragment="content"></section>
</main>

<footer th:replace="~{front/outlines/footer::common}"></footer>
<iframe name="ifrmProcess" class="dn"></iframe>
</body>
</html>
```

> static/front/css/mypage/style.css

```css
main { display: flex; }
main > aside { width: 250px; background: #f8f8f8; min-height: 700px; }
main > aside > ul { margin-top: 20px;  }
main > aside > ul a { height: 50px; display: block;  line-height: 50px; padding: 0 30px; font-size: 1.1rem; font-weight: 500; background: #d5d5d5; }
main > aside > ul a:hover, main > aside > ul li.on a { background: #222; color: #fff; }
main > aside > ul li { border-top: 1px solid #222; }
main > section { flex-grow: 1; padding: 45px; }
main > section h1 { line-height: 1; margin-bottom: 25px; font-size: 1.5rem; }


/* 찜 게시글 목록 */
.save_post_page .items li { border: 1px solid #f8f8f8; border-radius: 5px; }
.save_post_page .items li + li { margin-top: 10px; }
.save_post_page .subject { padding: 10px 15px; cursor: pointer; }
.save_post_page .content { margin-top: 10px; transition: all 0.5s; min-height: 300px;}
.save_post_page .content > div { background: #f8f8f8; padding: 15px; border-radius: 5px; }
.save_post_page .content.hide { transition: all 0.5s; }

/* 마이페이지 사이드 영역 */
aside .profile_image { width: 200px; height: 200px; border-radius: 50%; margin: 20px auto;  }
aside .profile_box .update_profile_btn { border: 1px solid #596b99; height: 35px; text-align: center; background: #fff; color: #596b99; display: block; width: 200px; margin: 0 auto 10px; font-size: 1.15rem; line-height: 33px; border-radius: 3px;}
aside .user_email { font-size: 1.2rem; font-weight: 500; padding: 0 25px; }
aside .user_nm { padding: 0 25px; margin-bottom: 10px; }
aside .follow_info { text-align: center; }

/* 팔로우, 팔로잉 페이지 */
.follow_page .items .item + .item { border-top: 1px solid #d5d5d5; }
.follow_page .items .item { display: flex; align-items: center; }
.follow_page .items .item button { width: 120px; height: 40px; background: #fff; border: 1px solid #596b99; border-radius: 3px; cursor: pointer; color: #596b99; font-weight: 500; }
.follow_page .items .item button:hover { background: #596b99; color: #fff; }
.follow_page .items .profile { flex-grow: 1; display: flex; align-items: center; }
.follow_page .items .profile_image { width: 80px; height: 80px; border-radius: 50%; margin-right: 20px; }
.follow_page .items .user_nm { font-size: 1.5rem; font-weight: 500; }
.follow_page .items .user_email { font-size: 1.15rem; color: #555; }
```

> resources/templates/front/mypage/_side.html

```html 
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
    xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
    <aside th:fragment="menus">
        <div class="profile_box" sec:authorize="isAuthenticated()">
            <div class="profile_image" th:if="${session.member.profileImage != null}" th:style="${@utils.backgroundStyle(session.member.profileImage, 350, 350)}"></div>

            <a class='update_profile_btn' th:href="@{/mypage/profile}" th:text="#{회원정보_수정}"></a>

            <div class="user_email" th:text="${session.member.email}"></div>
            <div class="user_nm" th:text="${session.member.name}"></div>

            <div class="follow_info">
                <i class="xi-users"></i>
                <a th:href="@{/mypage/follow?mode=follower}">
                    <span class='num' th:text="${@followService.totalFollowers}"></span> <span class="txt">followers</span>
                </a>
                <a th:href="@{/mypage/follow?mode=following}">
                    <span class='num' th:text="${@followService.totalFollowings}"></span> <span class="txt">following</span>
                </a>
            </div>
        </div>
        <!--// profile_box -->
        <ul class="menus">
            <li>
                <a th:href="@{/mypage/save_post}" th:text="#{찜_게시글}"></a>
            </li>
        </ul>
    </aside>
</html>
```



> mypage/controllers/MypageController.java

```java
public class MypageController implements ExceptionProcessor {

    private final SaveBoardDataService saveBoardDataService;
    private final FollowService followService;

    private final Utils utils;
    
    ...


    @GetMapping("/follow")
    public String followList(@RequestParam(name="mode", defaultValue = "follower") String mode, RequestPaging paging, Model model) {
        commonProcess("follow", model);

        ListData<Member> data = followService.getList(mode, paging);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());
        model.addAttribute("mode", mode);

        return utils.tpl("mypage/follow");
    }
    
    ...
    
    private void commonProcess(String mode, Model model) {
        ...

        List<String> addCss = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        List<String> addCommonScript = new ArrayList<>();


        if (mode.equals("save_post")) { // 찜한 게시글 페이지
            pageTitle = Utils.getMessage("찜_게시글", "commons");

            addScript.add("board/common");
            addScript.add("mypage/save_post");
        } else if (mode.equals("follow")) {
            addCommonScript.add("follow");
        }

        ....
        
        model.addAttribute("addCommonScript", addCommonScript);
    }
}
```

> resources/templates/mypage/follow.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{front/layouts/mypage}">

<section layout:fragment="content" class="follow_page">
    <ul class="items">
        <li class="item" th:unless="${items == null || items.isEmpty()}" th:each="item : ${items}" th:object="${item}">
            <div class="profile">
                <div class="profile_image" th:if="*{profileImage != null}" th:style="*{@utils.backgroundStyle(profileImage, 80, 80)}"></div>
                <a class="user_info" th:href="@{/mypage/follow/{userId}(userId=*{userId})}">
                    <div class="user_nm" th:text="*{#strings.concat(name, '(', userId, ')')}"></div>
                    <div class="user_email" th:text="*{email}"></div>
                </a>
            </div>
            <th:block sec:authorize="isAuthenticated()">
                <button type="button" th:if="*{@followService.followed(userId)}" class="follow_action unfollow" th:data-user-id="*{userId}">UnFollow</button>
                <button type="button" th:unless="*{@followService.followed(userId)}" class="follow_action" th:data-user-id="*{userId}">Follow</button>
            </th:block>
        </li>
    </ul>

    <th:block th:replace="~{common/_pagination::pagination}"></th:block>
</section>
</html>
```

팔로우, 팔로잉 목록 적용 화면

![image3](https://raw.githubusercontent.com/yonggyo1125/project502/master/images/follow/image3.png)


# 사용자 페이지 : 팔로우, 언팔로우 기능 구현

> member/controllers/ApiMemberController.java

```java
package org.choongang.member.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.commons.ExceptionRestProcessor;
import org.choongang.commons.rests.JSONData;
import org.choongang.member.service.follow.FollowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class ApiMemberController implements ExceptionRestProcessor {

    private final FollowService followService;
    
    @GetMapping("/follow/{userId}")
    public JSONData<Object> follow(@PathVariable("userId") String userId) {
        followService.follow(userId);

        return new JSONData<>();
    }

    @GetMapping("/unfollow/{userId}")
    public JSONData<Object> unfollow(@PathVariable("userId") String userId) {
        followService.unfollow(userId);

        return new JSONData<>();
    }
}
```

> board/controllers/BoardController.java
 
```java
...

@Controller
@RequestMapping("/board")
public class BoardController extends ExceptionProcessor {
    ...

    private void commonProcess(String bid, String mode, Model model) {

        mode = StringUtils.hasText(mode) ? mode : "list";

        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        List<String> addCommonCss = new ArrayList<>();
        List<String> addCss = new ArrayList<>();

        addScript.add("board/common"); // 게시판 공통 스크립트
        
        addCommonScript.add("follow"); // 팔로잉, 언팔로잉
            
        ...
    }
    
    ...
}
```

> resources/static/common/js/follow.js

```javascript
var commonLib = commonLib || {};

commonLib.followLib = {
    /**
    * 팔로우
    *
    * @param userId : 사용자 ID
    * @param callback : 콜백 함수
    */
    follow(userId, callback) {

        const { ajaxLoad } = commonLib;

        ajaxLoad("GET", `/api/member/follow/${userId}`)
            .then(res => {
                if (typeof callback == 'function') {
                    callback();
                }
            })
            .catch(err => console.error(err));
    },
    /**
    * 언팔로우
    *
    * @param userId : 사용자 ID
    */
    unfollow(userId, callback) {
        const { ajaxLoad } = commonLib;

        ajaxLoad("GET", `/api/member/unfollow/${userId}`)
            .then(res => {
                if (typeof callback == 'function') {
                    callback();
                }
            })
            .catch(err => console.error(err));
    }
};


window.addEventListener("DOMContentLoaded", function() {
    const followings = document.getElementsByClassName("follow_action");
    const { follow, unfollow } = commonLib.followLib;

    // 팔로잉, 언팔로잉 처리
    for(const el of followings) {
        el.addEventListener("click", function() {
            const classList = this.classList;
            const action = classList.contains("unfollow") ? unfollow : follow;
            action(this.dataset.userId, function() {
                    if (classList.contains("unfollow")) {
                        classList.remove("unfollow")
                        el.innerText = "Follow";
                    } else {
                        classList.add("unfollow");
                        el.innerText = "UnFollow";
                    }
            });
        });
    }
});
```

> resources/templates/front/board/skins/default/view.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<th:block th:fragment="default" th:object="${boardData}">
   <div class="board_view layout_width">
       ...
       <div class="post_info">
           <div class="left">
               <span>
                   <th:block th:text="#{작성자}"></th:block> :
                   <th:block th:text="*{poster}"></th:block>
                   <th:block th:if="*{member != null}" th:text="*{'(' + member.email + ')'}"></th:block>
                   <th:block th:if="*{member != null}" sec:authorize="isAuthenticated()">
                        <button type="button" th:if="*{@followService.followed(member.userId)}" class="follow_action unfollow" th:data-user-id="*{member.userId}">UnFollow</button>
                        <button type="button" th:unless="*{@followService.followed(member.userId)}" class="follow_action" th:data-user-id="*{member.userId}">Follow</button>
                   </th:block>
               </span>
               ...
           </div>
       </div>
       ...
       
   </div>
    ....
</th:block>
```

게시글 보기 적용 화면

![image2](https://raw.githubusercontent.com/yonggyo1125/project502/master/images/follow/image2.png)
