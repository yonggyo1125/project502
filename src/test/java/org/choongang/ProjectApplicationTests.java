package org.choongang;

import org.choongang.member.Authority;
import org.choongang.member.entities.Authorities;
import org.choongang.member.entities.Member;
import org.choongang.member.repositories.AuthoritiesRepository;
import org.choongang.member.repositories.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class ProjectApplicationTests {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private AuthoritiesRepository authoritiesRepository;

	@Test @Disabled
	void contextLoads() {
		Member member = memberRepository.findByUserId("user01").orElse(null);

		Authorities authorities = new Authorities();
		authorities.setMember(member);
		authorities.setAuthority(Authority.ADMIN);

		authoritiesRepository.saveAndFlush(authorities);
	}

	@Test
	void test2() {
		String str = "09:00-18:00";
		Pattern pattern = Pattern.compile("(\\d{2}):(\\d{2})-(\\d{2}):(\\d{2})");

		Matcher matcher = pattern.matcher(str);


		System.out.println(matcher.find());
		System.out.println(matcher.group(1));
		System.out.println(matcher.group(2));
		System.out.println(matcher.group(3));
		System.out.println(matcher.group(4));
	}

}
