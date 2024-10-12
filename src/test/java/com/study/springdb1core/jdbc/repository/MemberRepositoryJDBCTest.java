package com.study.springdb1core.jdbc.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.study.springdb1core.jdbc.domain.Member;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class MemberRepositoryJDBCTest {

    MemberRepositoryJDBC repository = new MemberRepositoryJDBC();

    @Test
    void crud() throws SQLException {
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
    }

}