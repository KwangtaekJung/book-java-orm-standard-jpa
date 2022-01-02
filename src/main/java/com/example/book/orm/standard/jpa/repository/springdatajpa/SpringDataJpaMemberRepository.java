package com.example.book.orm.standard.jpa.repository.springdatajpa;

import com.example.book.orm.standard.jpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long> {

    Page<Member> findByUsernameStartingWith(String name, Pageable pageable);

}
