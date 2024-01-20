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
