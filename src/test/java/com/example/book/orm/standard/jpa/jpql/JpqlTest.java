package com.example.book.orm.standard.jpa.jpql;

import com.example.book.orm.standard.jpa.entity.Member;
import com.example.book.orm.standard.jpa.entity.Team;
import com.example.book.orm.standard.jpa.repository.MemberRepository;
import com.example.book.orm.standard.jpa.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JpqlTest {

    @Autowired
    EntityManagerFactory emf;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    //@Autowired로 SpringBoot의 객체를 주입받을 경우에는 @Transactional로 영속성 관리가 된다.
    //따라서 EntityManager 로 영속성 관리를 하려면 EntityManagerFactory로 직접 만들어서 사용한다.
    private EntityManager em;

    @BeforeEach
    @Transactional
    public void beforeAll() {
        em = emf.createEntityManager();

        Team team = new Team();
        team.setId(1L);
        team.setName("Team1");
        teamRepository.save(team);

        Member member1 = new Member();
        member1.setId(1L);
        member1.setUsername("User1");
        member1.setAge(10);
        member1.setTeam(team);
        memberRepository.save(member1);

        Member member2 = new Member();
        member2.setId(2L);
        member2.setUsername("User2");
        member2.setAge(20);
        member2.setTeam(team);
        memberRepository.save(member2);
    }

    @Test
    public void lazyLoading() {
        Member member = em.find(Member.class, 1L);
        System.out.println(member.getUsername());
        System.out.println(member.getTeam().getName());
    }

    @Test
    public void lazyLoading_with_JPQL() {
        String jpql = "select m from Member m";

        TypedQuery<Member> query = em.createQuery(jpql, Member.class);
        List<Member> resultList = query.getResultList();

        System.out.println("member.username = " + resultList.get(0).getUsername());
        System.out.println("member.team.name = " + resultList.get(0).getTeam().getName());
    }

    @Test
    public void jpql_orderBy_결과변수() {
        String jpql = "SELECT t.name, COUNT(m.age) as cnt " +
                " FROM Member m left join m.team t" +
                " GROUP BY t.name" +
                " ORDER BY cnt";

        Query query = em.createQuery(jpql);
        List resultList = query.getResultList();

        for (Object o : resultList) {
            Object[] result = (Object[]) o;
            System.out.println("Team Name: " + result[0]);
            System.out.println("Count of User Age: " + result[1]);
        }
    }
}
