package com.study.springdb1core.jdbc.connection;

import static com.study.springdb1core.jdbc.connection.ConnectionConst.PASSWORD;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.URL;
import static com.study.springdb1core.jdbc.connection.ConnectionConst.USERNAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("connection = {}, class = {}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
