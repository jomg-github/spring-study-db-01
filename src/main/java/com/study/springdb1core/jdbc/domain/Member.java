package com.study.springdb1core.jdbc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Member {

    private Long memberId;
    private Long money;

    public Member(Long memberId, Long money) {
        this.memberId = memberId;
        this.money = money;
    }

    public Member(Long money) {
        this.money = money;
    }

    public void increase(Long money) {
        this.money = this.money + money;
    }

    public void decrease(Long money) {
        this.money = this.money - money;
    }
}
