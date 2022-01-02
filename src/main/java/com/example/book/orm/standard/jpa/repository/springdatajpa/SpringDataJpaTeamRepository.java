package com.example.book.orm.standard.jpa.repository.springdatajpa;

import com.example.book.orm.standard.jpa.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaTeamRepository extends JpaRepository<Team, Long> {

}
