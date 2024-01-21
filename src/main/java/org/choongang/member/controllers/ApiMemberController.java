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
