package com.example.book.orm.standard.jpa;

import com.example.book.orm.standard.jpa.entity.Member;
import com.example.book.orm.standard.jpa.entity.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    EntityManager em;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        Team team = new Team();
        team.setId(1L);
        team.setName("Team1");
        em.persist(team);

        Member member1 = new Member();
        member1.setId(1L);
        member1.setUsername("User1");
        member1.setTeam(team);
        em.persist(member1);

        Member member2 = new Member();
        member2.setId(2L);
        member2.setUsername("User2");
        member2.setTeam(team);
        em.persist(member2);

        em.flush();
    }
}
