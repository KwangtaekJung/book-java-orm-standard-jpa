package com.example.book.orm.standard.jpa.repository.springdatajpa;

import com.example.book.orm.standard.jpa.entity.Member;
import com.example.book.orm.standard.jpa.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long> {

    Page<Member> findByUsernameStartingWith(String name, Pageable pageable);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO Member VALUES(?, ?, ?, ?)", nativeQuery = true)
    int saveWithNativeQuery(Long id, Integer age, String username, Team team);
}
