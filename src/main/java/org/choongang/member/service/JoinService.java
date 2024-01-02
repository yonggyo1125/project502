package org.choongang.member.service;

import lombok.RequiredArgsConstructor;
import org.choongang.member.controllers.JoinValidator;
import org.choongang.member.controllers.RequestJoin;
import org.choongang.member.entities.Member;
import org.choongang.member.repositories.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final MemberRepository memberRepository;
    private final JoinValidator validator;
    private final PasswordEncoder encoder;

    public void process(RequestJoin form, Errors errors) {
        validator.validate(form, errors);
        if (errors.hasErrors()) {
            return;
        }

        // 비밀번호 BCrypt로 해시화
        String hash = encoder.encode(form.getPassword());

        Member member = new ModelMapper().map(form, Member.class);
        member.setPassword(hash);

        process(member);
    }

    public void process(Member member) {
        memberRepository.saveAndFlush(member);
    }
}
