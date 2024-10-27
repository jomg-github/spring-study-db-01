package com.study.springdb1core.jdbc.service;

import static com.study.springdb1core.jdbc.connection.ConnectionConst.PASSWORD;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.URL;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.study.springdb1core.jdbc.domain.Member;
import com.study.springdb1core.jdbc.repository.MemberJdbcTemplateRepository;
import com.study.springdb1core.jdbc.repository.MemberRepository;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootTest
@Slf4j
class MemberServiceTest {

    public static final Long FROM_MEMBER_ID_FOR_EXCEPTION = 25L;

    @Autowired
    private MemberTransactionUncheckedExceptionService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        @Bean
        public MemberRepository memberRepository(DataSource dataSource) {
            return new MemberJdbcTemplateRepository(dataSource);
        }

        @Bean
        public MemberTransactionUncheckedExceptionService memberService(MemberRepository memberRepository) {
            return new MemberTransactionUncheckedExceptionService(memberRepository);
        }

    }

    @Test
    @DisplayName("정상 이체")
    void test_1() {
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
    void test_2() {
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