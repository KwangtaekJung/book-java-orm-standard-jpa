package com.example.book.orm.standard.jpa.querydsl;

import com.example.book.orm.standard.jpa.entity.Member;
import com.example.book.orm.standard.jpa.entity.QMember;
import com.example.book.orm.standard.jpa.entity.QTeam;
import com.example.book.orm.standard.jpa.entity.Team;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class QuerydslTest {

    @Autowired
    EntityManagerFactory emf;

    @Autowired
    public EntityManager em;

    //@BeforeAll에는 @Transactional이 동작하지 않는다.
    @BeforeAll
    public void beforeAll() {
        em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();

        Team team = new Team();
        team.setId(1L);
        team.setName("Team1");
        em.persist(team);

        Member member1 = new Member();
        member1.setId(1L);
        member1.setUsername("User1");
        member1.setAge(10);
        member1.setTeam(team);
        em.persist(member1);

        Member member2 = new Member();
        member2.setId(2L);
        member2.setUsername("User2");
        member2.setAge(20);
        member2.setTeam(team);
        em.persist(member2);

        Team team2= new Team();
        team2.setId(2L);
        team2.setName("Team2");
        em.persist(team2);

        Member member3 = new Member();
        member3.setId(3L);
        member3.setUsername("User3");
        member3.setAge(30);
        member3.setTeam(team2);
        em.persist(member3);

        transaction.commit();

        em.close();
    }

    @Test
    @DisplayName("001. Querydsl 기본 테스트")
    public void querydslTest() {
        em = emf.createEntityManager();

        QMember member = new QMember("m");
        QTeam team = new QTeam("t");

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Member> members = queryFactory
                .select(member)
                .from(member)
                .fetch();

        System.out.println("member.username: " + members.get(0).getUsername());
        System.out.println("member.tea.name: " + members.get(0).getTeam().getName());
    }

    @Test
    @DisplayName("002. Querydsl OrderBy with 집합함수")
    public void querydsl_orderBy() {
        em = emf.createEntityManager();

        QMember member = new QMember("m");
        QTeam team = new QTeam("t");

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Team> teams = queryFactory
                .select(team)
                .from(team)
                .orderBy(team.members.size().desc())
                .fetch();

        for (Team t : teams) {
            System.out.println("team = " + t.getName());
        }
    }
}
