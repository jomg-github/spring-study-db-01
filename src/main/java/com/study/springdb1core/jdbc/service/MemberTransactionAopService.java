package com.study.springdb1core.jdbc.service;

import com.study.springdb1core.jdbc.domain.Member;
import com.study.springdb1core.jdbc.repository.MemberTransactionRepository;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Slf4j
public class MemberTransactionAopService {

    private final MemberTransactionRepository memberRepository;

    @Transactional
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
