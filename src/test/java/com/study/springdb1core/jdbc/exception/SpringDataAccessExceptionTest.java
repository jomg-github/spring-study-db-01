package com.study.springdb1core.jdbc.exception;

import static com.study.springdb1core.jdbc.connection.ConnectionConst.PASSWORD;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.URL;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

@Slf4j
class SpringDataAccessExceptionTest {
    private static final int H2_BAD_GRAMMAR_ERROR_CODE = 42122;

    DataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Test
    public void exceptionTranslator() {
        String sql = "select bad_grammer";

        try (Connection con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(H2_BAD_GRAMMAR_ERROR_CODE);

            SQLErrorCodeSQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException dataAccessException = translator.translate("select", sql, e);

            log.info("dataAccessException", dataAccessException);

            assertThat(dataAccessException.getClass()).isEqualTo(BadSqlGrammarException.class);
        }
    }

}