package org.choongang.member.service.follow;

import lombok.RequiredArgsConstructor;
import org.choongang.member.MemberUtil;
import org.choongang.member.entities.Follower;
import org.choongang.member.entities.Following;
import org.choongang.member.entities.Member;
import org.choongang.member.entities.QFollower;
import org.choongang.member.repositories.FollowerRepository;
import org.choongang.member.repositories.FollowingRepository;
import org.choongang.member.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final MemberUtil memberUtil;
    private final MemberRepository memberRepository;
    private final FollowerRepository followerRepository;
    private final FollowingRepository followingRepository;

    /**
     *
     * @param mSeq
     */
    public void following(Long mSeq) {
        if (!memberUtil.isLogin()) {
            return;
        }

        Member member = memberUtil.getMember();

        Following following = new Following();
        following.setSeq(mSeq);
        following.setMember(member);

        followingRepository.saveAndFlush(following);


        Member followerMember = memberRepository.findById(mSeq).orElse(null);

        Follower follower = new Follower();
        follower.setSeq(member.getSeq());
        follower.setMember(followerMember);
        followerRepository.saveAndFlush(follower);
    }

    /**
     * mSeq 회원을 팔로잉 하는 회원들
     * @param mSeq
     * @return
     */
    public List<Member> getFollowers(Long mSeq) {
        QFollower follower = QFollower.follower;

        //List<Follower> members = followerRepository.findAll(QFollow)

        return null;
    }

    public List<Member> getFollowees(Long mSeq) {

        return null;
    }
}
