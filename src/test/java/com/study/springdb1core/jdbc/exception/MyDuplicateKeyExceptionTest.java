package com.study.springdb1core.jdbc.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.study.springdb1core.jdbc.domain.Member;
import com.study.springdb1core.jdbc.repository.exception.MyDBException;
import com.study.springdb1core.jdbc.repository.exception.MyDuplicateKeyException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.*;

@Slf4j
class MyDuplicateKeyExceptionTest {

    Repository repository;
    Service service;

    @BeforeEach
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @RequiredArgsConstructor
    static class Service {

        private final Repository repository;

        public void create(Long memberId) {
            try {
                Member save = repository.save(new Member(memberId, 0L));
                log.info("saved member id = {}", save.getMemberId());
            } catch (MyDuplicateKeyException e) {
                log.info("키 중복 = [{}]", memberId);
                Long newMemberId = generateNewMemberId(memberId);
                log.info("키 생성 = [{}]", newMemberId);
                repository.save(new Member(newMemberId, 0L));
            }
        }

        private Long generateNewMemberId(Long memberId) {
            return memberId + 1;
        }
    }

    @RequiredArgsConstructor
    static class Repository {

        private final DataSource dataSource;
        private static final int H2_DUPLICATE_KEY_ERROR_CODE = 23505;

        public Member save(Member member) {
            String sql = "insert into tb_member (member_id, money) values (?, ?)";

            try (Connection con = dataSource.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

                pstmt.setLong(1, member.getMemberId());
                pstmt.setLong(2, member.getMoney());
                pstmt.executeUpdate();

                return member;

            } catch (SQLException e) {
                if (e.getErrorCode() == H2_DUPLICATE_KEY_ERROR_CODE) {
                    throw new MyDuplicateKeyException(e);
                } else {
                    throw new MyDBException(e);
                }
            }
        }
    }

    @Test
    void duplicateKeyTest() {
        // given
        // when
        // then
        long lastMemberId = 94L;
        service.create(lastMemberId);

    }
}