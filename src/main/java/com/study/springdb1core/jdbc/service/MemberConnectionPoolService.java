package com.study.springdb1core.jdbc.service;

import com.study.springdb1core.jdbc.domain.Member;
import com.study.springdb1core.jdbc.repository.MemberConnectionPoolRepository;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberConnectionPoolService {

    private final MemberConnectionPoolRepository memberRepository;

    public void transfer(Long fromId, Long toId, Long money) throws SQLException {
        Member from = memberRepository.findById(fromId);
        Member to = memberRepository.findById(toId);

        from.decrease(money);
        to.increase(money);

        memberRepository.update(from);

        if (from.getMemberId() == 25L) {
            throw new RuntimeException("에러 발생");
        }

        memberRepository.update(to);
    }

}
