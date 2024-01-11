package org.choongang.member;

public enum Authority {
    ALL, // 전체(비회원 + 회원 + 관리자)
    USER, // 일반 사용자
    MANAGER, // 부 관리자
    ADMIN // 최고 관리자
}
