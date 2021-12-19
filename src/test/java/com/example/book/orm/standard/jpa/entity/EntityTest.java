package com.example.book.orm.standard.jpa.entity;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EntityTest {

    @Autowired
    EntityManager em;

    @AfterAll
    static void afterAll() {
        System.out.println("=========After ALL");
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void entityMappingTest() {
        Team team = new Team();
        team.setName("Team1");
        em.persist(team);

        Member member1 = new Member();
        member1.setUsername("User1");
        member1.setTeam(team);
        em.persist(member1);

        Member member2 = new Member();
        member2.setUsername("User2");
        member2.setTeam(team);
        em.persist(member2);

    }
}
