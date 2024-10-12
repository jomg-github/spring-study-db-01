package com.study.springdb1core.jdbc.connection;

import static com.study.springdb1core.jdbc.connection.ConnectionConst.PASSWORD;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.URL;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
public class DBConnectionTest {

    @Test
    void driverManager_test() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        log.info("connection1={}, class={}", con1, con1.getClass());
        log.info("connection2={}, class={}", con2, con2.getClass());
    }

    @Test
    void dataSourceDriverManger_test() throws SQLException {
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        getConnection1And2(dataSource);
    }

    @Test
    void dataSourceHikariConnectionPool_test() throws SQLException, InterruptedException {
        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(URL);
        datasource.setUsername(USERNAME);
        datasource.setPassword(PASSWORD);
        datasource.setMaximumPoolSize(1); // default 10, con2를 connectionTimeout 시간만큼 기다림
        datasource.setConnectionTimeout(2000); // default 30000ms
        getConnection1And2(datasource);

        Thread.sleep(1000); // 테스트가 종료되기 때문에 pool 추가하는 log까지 확인
    }

    private void getConnection1And2(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();

        log.info("connection1={}, class={}", con1, con1.getClass());
        log.info("connection2={}, class={}", con2, con2.getClass());
    }

}
