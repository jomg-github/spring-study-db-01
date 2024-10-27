package com.study.springdb1core.jdbc.service;

import com.study.springdb1core.jdbc.domain.Member;
import com.study.springdb1core.jdbc.repository.MemberTransactionRepository;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@RequiredArgsConstructor
@Slf4j
public class MemberTransactionManagerService {

    private final PlatformTransactionManager transactionManager;
    private final MemberTransactionRepository memberRepository;

    public void transfer(Long fromId, Long toId, Long money) throws SQLException {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            bizLogic(fromId, toId, money);

            transactionManager.commit(transaction);
        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(Long fromId, Long toId, Long money) throws SQLException {
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
