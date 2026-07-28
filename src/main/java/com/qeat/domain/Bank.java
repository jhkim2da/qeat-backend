package com.qeat.domain;

import lombok.Getter;

@Getter
public enum Bank {
    KB("국민은행"),
    SHINHAN("신한은행"),
    WOORI("우리은행"),
    HANA("하나은행"),
    NH("농협은행"),
    IBK("기업은행"),
    KAKAO("카카오뱅크"),
    TOSS("토스뱅크");

    private final String label;

    Bank(String label) {
        this.label = label;
    }
}