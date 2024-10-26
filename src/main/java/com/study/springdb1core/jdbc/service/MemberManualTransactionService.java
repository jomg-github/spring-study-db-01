package com.study.springdb1core.jdbc.service;

import com.study.springdb1core.jdbc.domain.Member;
import com.study.springdb1core.jdbc.repository.MemberManualTransactionRepository;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MemberManualTransactionService {

    private final DataSource dataSource;
    private final MemberManualTransactionRepository memberRepository;

    public void transfer(Long fromId, Long toId, Long money) throws SQLException {
        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false);

            transfer(fromId, toId, money, con);

            con.commit();
        } catch (Exception e) {
            con.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }

    }

    private void transfer(Long fromId, Long toId, Long money, Connection con) throws SQLException {
        Member from = memberRepository.findById(con, fromId);
        Member to = memberRepository.findById(con, toId);

        from.decrease(money);
        to.increase(money);

        memberRepository.update(con, from);

        if (from.getMemberId() == 25L) {
            throw new RuntimeException("에러 발생");
        }

        memberRepository.update(con, to);
    }

    private void release(Connection con) {
        if (con != null) {
            try {
                con.commit();
                con.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

}
