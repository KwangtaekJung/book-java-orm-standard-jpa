package com.example.book.orm.standard.jpa.repository;

import com.example.book.orm.standard.jpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
