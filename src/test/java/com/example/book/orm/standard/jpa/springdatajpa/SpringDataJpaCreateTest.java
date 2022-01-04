package com.example.book.orm.standard.jpa.springdatajpa;

import com.example.book.orm.standard.jpa.entity.Member;
import com.example.book.orm.standard.jpa.entity.QMember;
import com.example.book.orm.standard.jpa.entity.QTeam;
import com.example.book.orm.standard.jpa.entity.Team;
import com.example.book.orm.standard.jpa.repository.springdatajpa.SpringDataJpaMemberRepository;
import com.example.book.orm.standard.jpa.repository.springdatajpa.SpringDataJpaTeamRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.Optional;

@Tag("ALL")
@Tag("SpringDataJPA")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
//@Transactional  //transaction도 직접 관리한다.
public class SpringDataJpaCreateTest {
    //SpringDataJPA는 대부분 @Service or @Repository에서 @Transactional이 걸리기 때문에
    //테스트에 별도로 Transaction을 설정하기 않으면 데이터가 실제 DB에 저장된다.
    //따라서 DB 저장 여부를 확인할 수 있다.

    @Autowired
    SpringDataJpaMemberRepository memberRepository;

    @Autowired
    SpringDataJpaTeamRepository teamRepository;

    QMember member = new QMember("m");
    QTeam team = new QTeam("t");

//    @BeforeAll
//    public void beforeAll() {
//    }
//
//    @BeforeEach
//    public void beforeEach() {
//        em = emf.createEntityManager();
//        transaction = em.getTransaction();
//        transaction.begin();
//    }
//
//    @AfterEach
//    public void afterEach() {
//        if (transaction.isActive()) {
//            transaction.commit();
//        }
//        em.close();
//    }
//
//    @AfterAll
//    public void afterAll() {
//        beforeEach();
//
//        em.createQuery("DELETE FROM Member m").executeUpdate();
//        em.createQuery("DELETE FROM Team t").executeUpdate();
//
//        afterEach();
//    }

    @Test
    @DisplayName("100. [SpringDataJPA][CREATE] save()")
    public void save() {

        // Team1 ------------------------
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team1");
        teamRepository.save(team1);

        for (int i=1; i<=3; i++) {
            Member member = new Member();
            member.setId((long) (100+i));
            member.setUsername("User"+member.getId());
            member.setAge(30+i);
            member.setTeam(team1);
            memberRepository.save(member);
        }

        // Team2 ------------------------
        // CascadeType.PERSIST 를 설정하여 Team을 영속화하면 Member도 함께 영속 상태가 되는지 확인한다.
        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team2");
        teamRepository.save(team2);

        for (int i=1; i<=5; i++) {
            Member member = new Member();
            member.setId((long) (200+i));
            member.setUsername("User"+member.getId());
            member.setAge(40+i);
            member.setTeam(team2);
            memberRepository.save(member);
        }
    }

    @Test
    @DisplayName("101. [SpringDataJPA][CREATE] JPQL INSERT")
    @Disabled("JPQ은 INSERT를 지원하지 않는다. 다른 방법이 없나?")
    public void create_jpql() {

    }

    @Test
    @DisplayName("102. [SpringDataJPA][CREATE] NativeQuery INSERT")
    public void create_nativeQuery() {
        //JPA는 INSERT를 지원하지 않지만 Hibernate 구현체는 지원해서 여기서는 사용할 수 있다.
        //Native Query는 위치 기반 파라미터 바인딩만 지원
        Long userId = 501L;

        int resultCount = memberRepository.saveWithNativeQuery(userId, 36, "User" + userId, null);
        System.out.println("resultCount = " + resultCount);

        Optional<Member> optionalMember = memberRepository.findById(userId);
        optionalMember.ifPresent(System.out::println);

        Assertions.assertThat(optionalMember).isNotEmpty();
        Assertions.assertThat(optionalMember.get().getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("103. [SpringDataJPA][CREATE] QueryDSL INSERT")
    @Disabled("미지원??")
    public void springDataJPA_queryDsl_create() {

    }
}
