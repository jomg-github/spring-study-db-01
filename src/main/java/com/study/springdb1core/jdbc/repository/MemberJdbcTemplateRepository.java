package com.study.springdb1core.jdbc.repository;

import com.study.springdb1core.jdbc.domain.Member;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

@Slf4j
public class MemberJdbcTemplateRepository implements MemberRepository {

    private final SimpleJdbcInsert simpleJdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public MemberJdbcTemplateRepository(DataSource dataSource) {
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("TB_MEMBER").usingGeneratedKeyColumns(MEMBER_ID);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final String MEMBER_ID = "member_id";
    private static final String MONEY = "money";


    @Override
    public Member save(Member member) {
        Number id = simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource()
            .addValue(MONEY, member.getMoney())
        );
        member.setMemberId(id.longValue());
        return member;
    }

    @Override
    public Member findById(Long memberId) {
        String sql = "select * from tb_member where member_id = ?";
        return jdbcTemplate.queryForObject(sql, memberRowMapper(), memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getLong(MEMBER_ID));
            member.setMoney(rs.getLong(MONEY));
            return member;
        };
    }

    @Override
    public void update(Member member) {
        String sql = "update tb_member set money = ? where member_id = ?";
        jdbcTemplate.update(sql, member.getMoney(), member.getMemberId());
    }

    @Override
    public void delete(long memberId) {
        String sql = "delete from tb_member where member_id = ?";
        jdbcTemplate.update(sql, memberId);
    }

}
