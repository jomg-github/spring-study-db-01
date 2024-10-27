package com.study.springdb1core.jdbc.service;

import com.study.springdb1core.jdbc.domain.Member;
import com.study.springdb1core.jdbc.repository.MemberTransactionRepository;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
public class MemberTransactionTemplateService {

    private final TransactionTemplate template;
    private final MemberTransactionRepository memberRepository;

    public MemberTransactionTemplateService(PlatformTransactionManager transactionManager, MemberTransactionRepository memberRepository) {
        this.template = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void transfer(Long fromId, Long toId, Long money) throws SQLException {
        template.executeWithoutResult(transactionStatus -> {
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
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
