package org.ssafy.datacontest.enums;

import lombok.Getter;

@Getter
public enum Category {
    DESIGN("디자인"),
    DEVELOP("개발"),
    VIDEO("영상"),
    PLAN("기획"),
    INDUSTRY("산업/제품"),
    ART_CREATIVE("예술/창작"),
    ENGINEERING_IT("공학/IT"),
    DATA_AI("데이터/AI"),
    FASHION_LIFESTYLE("패션/라이프스타일");

    private final String message;

    Category(String message) {
        this.message = message;
    }
}
