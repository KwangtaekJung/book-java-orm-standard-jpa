package com.example.book.orm.standard.jpa;

import com.example.book.orm.standard.jpa.entity.Member;
import com.example.book.orm.standard.jpa.entity.Team;
import com.example.book.orm.standard.jpa.repository.springdatajpa.SpringDataJpaMemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import java.util.List;

import static org.springframework.data.domain.Sort.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@TestMethodOrder(value = MethodOrderer.DisplayName.class)
public class SpringDataJpaTest {

    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    public EntityManager em;

    @Autowired
    private SpringDataJpaMemberRepository memberRepository;

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
    @DisplayName("001. Spring Data Jpa - 페이징과 정렬")
    public void springDataJpa_페이징과정렬() {
        PageRequest pageRequest =
                PageRequest.of(0, 10, Sort.by(Direction.DESC, "username"));

        Page<Member> result = memberRepository.findByUsernameStartingWith("U", pageRequest);

        List<Member> members = result.getContent();
        System.out.println("members = " + members);

        long totalElements = result.getTotalElements();
        System.out.println("totalElements = " + totalElements);

        int totalPages = result.getTotalPages();
        System.out.println("totalPages = " + totalPages);

        boolean hasNext = result.hasNext();
        System.out.println("hasNext = " + hasNext);
    }
}
