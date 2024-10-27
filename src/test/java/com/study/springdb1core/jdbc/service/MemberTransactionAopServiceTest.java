package com.study.springdb1core.jdbc.service;

import static com.study.springdb1core.jdbc.connection.ConnectionConst.PASSWORD;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.URL;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.study.springdb1core.jdbc.domain.Member;
import com.study.springdb1core.jdbc.repository.MemberTransactionRepository;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootTest
@Slf4j
class MemberTransactionAopServiceTest {

    public static final Long FROM_MEMBER_ID_FOR_EXCEPTION = 25L;

    @Autowired
    private MemberTransactionAopService memberService;

    @Autowired
    private MemberTransactionRepository memberRepository;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        @Bean
        public MemberTransactionRepository memberRepository(DataSource dataSource) {
            return new MemberTransactionRepository(dataSource);
        }

        @Bean
        public MemberTransactionAopService memberService(MemberTransactionRepository memberRepository) {
            return new MemberTransactionAopService(memberRepository);
        }

    }

    @Test
    @DisplayName("AOP 테스트")
    void test_aop() {
        // given
        // when
        // then
        log.info("memberService = {}", memberService.getClass());
        assertThat(AopUtils.isAopProxy(memberService)).isTrue();
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
    @DisplayName("이체 실패 - @Transactional 적용")
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