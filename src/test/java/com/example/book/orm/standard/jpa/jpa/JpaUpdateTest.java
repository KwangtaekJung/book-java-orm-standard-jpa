package com.example.book.orm.standard.jpa.jpa;

import com.example.book.orm.standard.jpa.entity.Member;
import com.example.book.orm.standard.jpa.entity.QMember;
import com.example.book.orm.standard.jpa.entity.QTeam;
import com.example.book.orm.standard.jpa.entity.Team;
import com.example.book.orm.standard.jpa.entity.TeamDTO;
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

@Tag("ALL")
@Tag("JPA")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
//@Transactional  //transaction도 직접 관리한다.
public class JpaUpdateTest {

    //EntityMangerFactory를 통해 영속성 컨텍스트틑 직접 제어하는 이유
    //--테스트에서 실제 DB에 저장되는 시점을 확인하고 싶다.
    //--스프링이 제공하는 어노테이션을 사용하면 method 단위로 transaction 을 설정할 수 밖에 없다.
    //--하나의 테스트 안에서 실제 DB에 저장되는것까지 확인하고자 한다면 더 세밀하게 영속성 컨텍스트 제어가 되어야 한다.
    //--따라서 직접 EntityManager 를 생성해서 사용한다.

    //참고로, @BeforeAll에서 DB에 미리 데이터를 저장하고 싶은 경우...
    //--@Autowired로 SpringBoot의 객체를 주입받을 경우에는 @Transactional로 영속성 관리가 된다.
    //--@BeforeAll에는 @Transactional이 동작하지 않으므로 직접 영속성 관리를 해야 한다.
    //--그리고 직접 영속성 관리를 하려면 EntityManagerFactory로 EntityManager를 직접 만들어서 사용한다.
    //--아래 테스트에서는 @BeforeALL을 사용하지 않는 형태로 작성했으므로 @Autowired로 직접 EntityManager를 주입받았다.

    @Autowired
    EntityManagerFactory emf;

    private EntityManager em;
    private EntityTransaction transaction;

    QMember member = new QMember("m");
    QTeam team = new QTeam("t");

    @BeforeAll
    public void beforeAll() {
        beforeEach();

        // Team1 ------------------------
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team1");
        em.persist(team1);

        for (int i=1; i<=3; i++) {
            Member member = new Member();
            member.setId((long) (100+i));
            member.setUsername("User"+member.getId());
            member.setAge(30+i);
            member.setTeam(team1);
            em.persist(member);
        }

        // Team2 ------------------------
        // CascadeType.PERSIST 를 설정하여 Team을 영속화하면 Member도 함께 영속 상태가 되는지 확인한다.
        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team2");

        for (int i=1; i<=5; i++) {
            Member member = new Member();
            member.setId((long) (200+i));
            member.setUsername("User"+member.getId());
            member.setAge(40+i);
            member.setTeam(team2);
        }
        em.persist(team2);

        afterEach();
    }

    @BeforeEach
    public void beforeEach() {
        em = emf.createEntityManager();
        transaction = em.getTransaction();
        transaction.begin();
    }

    @AfterEach
    public void afterEach() {
        if (transaction.isActive()) {
            transaction.commit();
        }
        em.close();
    }

    @AfterAll
    public void afterAll() {
        beforeEach();

        em.createQuery("DELETE FROM Member m").executeUpdate();
        em.createQuery("DELETE FROM Team t").executeUpdate();

        afterEach();
    }

    @Test
    @DisplayName("100. [JPA][UPDATE] 변경 감지")
    public void update() {
        //JPA는 update 쿼리를 사용하지 않고 변경 감지에 의해 엔티티를 수정한다.
        //JPQL or NativeQuery를 이용할 수는 있지만 이 경우 DB에 직접 넣기 때문에 영속성 컨텍스트의 무결성이 깨질수 있으니 주의해야 한다.

        Member member = em.find(Member.class, 101L);
        System.out.println(member);

        member.setAge(member.getAge() + 5);

        System.out.println(member);
    }

    @Test
    @DisplayName("101. [JPA][UPDATE] JPQL UPDATE")
    public void update_with_JPQL() {
        //JPA는 update 쿼리를 사용하지 않고 변경 감지에 의해 엔티티를 수정한다.
        //JPQL or NativeQuery를 이용할 수는 있지만 이 경우 DB에 직접 넣기 때문에 영속성 컨텍스트의 무결성이 깨질수 있으니 주의해야 한다.
        //실제 DB에 저장은, 역시나 transaction.commit() 하는 시점에 이루어진다.
        Long userId = 101L;
        int userAge = 25;
        Member member = em.find(Member.class, userId);
        System.out.println(member);

        String jpql = "UPDATE Member m SET m.age = :newAge WHERE m.id = :id";

        Query query = em.createQuery(jpql)
                .setParameter("id", userId)
                .setParameter("newAge", userAge);
        int resultCount = query.executeUpdate();

        transaction.commit();  //실제 DB에 저장시킨다.
        em.refresh(member);  //DB와 싱크를 맞춘다.
        System.out.println(member);
        Assertions.assertThat(member.getAge()).isEqualTo(userAge);
    }

    @Test
    @DisplayName("102. [JPA][UPDATE] NativeQuery UPDATE")
    public void update_with_NativeQuery() {
        //JPA는 update 쿼리를 사용하지 않고 변경 감지에 의해 엔티티를 수정한다.
        //JPQL or NativeQuery를 이용할 수는 있지만 이 경우 DB에 직접 넣기 때문에 영속성 컨텍스트의 무결성이 깨질수 있으니 주의해야 한다.
        //실제 DB에 저장은, 역시나 transaction.commit() 하는 시점에 이루어진다.
        //JPQL은 Entity와 Entity의 속성에 대해 대소문자를 구분한다.
        //NativeQuery 은 테이블과 테이블 속성에 대해 대소문자를 구분하지 않는다.

        Long userId = 101L;
        int userAge = 38;
        Member member = em.find(Member.class, userId);
        System.out.println(member);

        String nativeQuery = "UPDATE member SET age = :newAge WHERE member_id = :id";

        Query query = em.createNativeQuery(nativeQuery)
                .setParameter("id", userId)
                .setParameter("newAge", userAge);
        int resultCount = query.executeUpdate();

        System.out.println("resultCount = " + resultCount);

        transaction.commit();
        em.refresh(member);
        System.out.println(member);
    }

    @Test
    @DisplayName("103. [JPA][UPDATE] QueryDSL - UPDATE")
    public void update_queryDsl() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        Long userId = 201L;
        int userAge = 55;

        //Entity 조회
        Member member = em.find(Member.class, userId);
        System.out.println(member);

        //DB Update
        long resultCount = queryFactory
                .update(this.member)
                .where(this.member.id.eq(userId))
                .set(this.member.age, userAge)
                .execute();

        //DB 실제 저장
        transaction.commit();

        //Entity 확인: 아직 Entity에는 반영되어 있지 않다.(무결성 깨짐)
        System.out.println(member);

        //DB와 싱크
        em.refresh(member);
        System.out.println(member);
    }
}
