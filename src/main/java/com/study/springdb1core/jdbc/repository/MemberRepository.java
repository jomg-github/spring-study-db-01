package com.study.springdb1core.jdbc.repository;

import com.study.springdb1core.jdbc.domain.Member;

public interface MemberRepository {

    Member save(Member member);

    Member findById(Long memberId);

    void update(Member member);

    void delete(long memberId);

}
