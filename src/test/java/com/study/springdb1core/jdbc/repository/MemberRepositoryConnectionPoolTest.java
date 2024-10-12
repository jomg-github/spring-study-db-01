package com.study.springdb1core.jdbc.repository;

import static com.study.springdb1core.jdbc.connection.ConnectionConst.PASSWORD;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.URL;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.study.springdb1core.jdbc.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

class MemberRepositoryConnectionPoolTest {

    MemberRepositoryConnectionPool repository;

    @BeforeEach
    void setUp() {
        // 기본 DriverManager
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // HikariCP
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryConnectionPool(dataSource);
    }

    @Test
    void crud() throws SQLException, InterruptedException {
        // create, read
        Member newMember = repository.save(new Member(5000L));
        Member findMember = repository.findById(newMember.getMemberId());
        assertThat(findMember).isEqualTo(newMember);

        // update
        newMember.setMoney(99999L);
        repository.update(newMember);
        Member updateMember = repository.findById(newMember.getMemberId());
        assertThat(newMember.getMemberId()).isEqualTo(updateMember.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(99999L);

        // delete
        repository.delete(newMember.getMemberId());
        assertThrows(NoSuchElementException.class, () -> repository.findById(newMember.getMemberId()));

        Thread.sleep(1000);
    }

}