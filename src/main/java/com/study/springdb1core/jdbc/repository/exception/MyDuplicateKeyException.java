package com.study.springdb1core.jdbc.repository.exception;

import java.sql.SQLException;

public class MyDuplicateKeyException extends MyDBException {

    public MyDuplicateKeyException(SQLException e) {
        super(e);
    }
}
