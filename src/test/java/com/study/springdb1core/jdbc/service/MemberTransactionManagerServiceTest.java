package com.study.springdb1core.jdbc.service;

import static com.study.springdb1core.jdbc.connection.ConnectionConst.PASSWORD;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.URL;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.study.springdb1core.jdbc.domain.Member;
import com.study.springdb1core.jdbc.repository.MemberTransactionRepository;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

class MemberTransactionManagerServiceTest {

    public static final Long FROM_MEMBER_ID_FOR_EXCEPTION = 25L;

    private MemberTransactionManagerService memberService;
    private MemberTransactionRepository memberRepository;

    @BeforeEach
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberTransactionRepository(dataSource);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberService = new MemberTransactionManagerService(transactionManager, memberRepository);
    }

    @Test
    @DisplayName("정상 이체")
    void test_1() throws SQLException {
        // given
        long seedMoney = 100000L;
        Member from = memberRepository.save(new Member(seedMoney));
        Member to = memberRepository.save(new Member(seedMoney));

        // when
        long changeAmount = 1000L;
        memberService.transfer(from.getMemberId(), to.getMemberId(), changeAmount);

        // then
        Long fromMoney = memberRepository.findById(from.getMemberId()).getMoney();
        Long toMoney = memberRepository.findById(to.getMemberId()).getMoney();

        assertThat(fromMoney).isEqualTo(seedMoney - changeAmount);
        assertThat(toMoney).isEqualTo(seedMoney + changeAmount);
    }

    @Test
    @DisplayName("이체 실패 - 트랜잭션 수동 적용")
    void test_2() throws SQLException {
        // given
        Member from = memberRepository.findById(FROM_MEMBER_ID_FOR_EXCEPTION);
        long fromSeedMoney = from.getMoney();

        long seedMoney = 100000L;
        Member to = memberRepository.save(new Member(seedMoney));

        // when
        long changeAmount = 1000L;
        assertThatThrownBy(() -> memberService.transfer(from.getMemberId(), to.getMemberId(), changeAmount));

        // then
        Long fromMoney = memberRepository.findById(from.getMemberId()).getMoney();
        Long toMoney = memberRepository.findById(to.getMemberId()).getMoney();

        assertThat(fromMoney).isEqualTo(fromSeedMoney);
        assertThat(toMoney).isEqualTo(seedMoney);
    }

}