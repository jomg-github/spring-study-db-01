package com.study.springdb1core.jdbc.repository;

import com.study.springdb1core.jdbc.domain.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
public class MemberTransactionRepository {

    private final DataSource dataSource;

    private static final String MEMBER_ID = "member_id";
    private static final String MONEY = "money";


    public Member save(Member member) throws SQLException {
        Connection con = getConnection();

        try (PreparedStatement pstmt = createPreparesStatement(con, member);
            ResultSet generatedKeys = pstmt.getGeneratedKeys()
        ) {
            if (generatedKeys.next()) {
                member.setMemberId(generatedKeys.getLong(MEMBER_ID));
            } else {
                throw new SQLException();
            }
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private PreparedStatement createPreparesStatement(Connection con, Member member) throws SQLException {
        String sql = "insert into tb_member(money) values(?)";
        PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setLong(1, member.getMoney());

        int affectedRows = pstmt.executeUpdate();

        if (affectedRows == 0) {
            throw new RuntimeException();
        }

        return pstmt;
    }

    public Member findById(Long memberId) throws SQLException {
        Connection con = getConnection();

        try (PreparedStatement pstmt = selectPreparedStatement(con, memberId);
            ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getLong(MEMBER_ID));
                member.setMoney(rs.getLong(MONEY));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private PreparedStatement selectPreparedStatement(Connection con, Long memberId) throws SQLException {
        String sql = "select * from tb_member where member_id = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setLong(1, memberId);
        return pstmt;
    }

    public void update(Member member) throws SQLException {
        Connection con = getConnection();

        try (PreparedStatement pstmt = updatePreparedStatement(con, member)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private PreparedStatement updatePreparedStatement(Connection con,  Member member) throws SQLException {
        String sql = "update tb_member set money = ? where member_id = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setLong(1, member.getMoney());
        pstmt.setLong(2, member.getMemberId());
        return pstmt;
    }

    public void delete(long memberId) throws SQLException {
        try (Connection con = getConnection();
            PreparedStatement pstmt = deletePreparedStatement(con, memberId)
        ) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    private PreparedStatement deletePreparedStatement(Connection con, long memberId) throws SQLException {
        String sql = "delete from tb_member where member_id = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setLong(1, memberId);
        return pstmt;
    }

    private Connection getConnection() throws SQLException {
        // 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 함
        Connection connection = DataSourceUtils.getConnection(dataSource);;
        log.info("get connection={}, class={}", connection, connection.getClass());
        return connection;
    }


}
