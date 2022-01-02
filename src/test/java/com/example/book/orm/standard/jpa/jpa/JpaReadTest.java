package com.example.book.orm.standard.jpa.jpa;

import com.example.book.orm.standard.jpa.entity.Member;
import com.example.book.orm.standard.jpa.entity.Team;
import com.example.book.orm.standard.jpa.entity.TeamDTO;
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

@Tag("JPA")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
//@Transactional  //transaction도 직접 관리한다.
public class JpaReadTest {

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
    @DisplayName("100. [JPA][READ] em.find() and lazyLoading")
    public void lazyLoading() {
        Member member = em.find(Member.class, 101L);
        System.out.println(member.getUsername());
        System.out.println(member.getTeam().getName());
    }

    @Test
    @DisplayName("101. [JPA][READ] JPQL SELECT and lazyLoading")
    public void lazyLoading_with_JPQL() {
        String jpql = "SELECT m FROM Member m";

        TypedQuery<Member> query = em.createQuery(jpql, Member.class);
        List<Member> resultList = query.getResultList();

        for (Member member : resultList) {
            System.out.println("member.username = " + member.getUsername());
            System.out.println("member.team.name = " + member.getTeam().getName());
        }
    }

    @Test
    @DisplayName("110. [JPA][READ] JPQL Projection - 여러 값 조회")
    public void read_JPQL_Projection_multi_Entity() {
        //여러 값을 조회하는 경우이다.
        //경로 표현식(m.team.name)에 의해 묵시적인 조인이 발생한다.(실제 SQL 쿼리 확인 할 것)
        String jpql = "SELECT m, m.team.name FROM Member m";

        Query query = em.createQuery(jpql);
        List<Object[]> resultList = query.getResultList();

        for (Object[] row : resultList) {
            System.out.println("member: " + row[0]);
            System.out.println("member.team.name = " + row[1]);
        }
    }

    @Test
    @DisplayName("111. [JPA][READ] JPQL Projection - Scala")
    public void read_jqpl_scala_projection() {
        String jpql = "SELECT t.name, SIZE(t.members) FROM Team t";

        List resultList = em.createQuery(jpql)
                .getResultList();

        for (Object o : resultList) {
            Object[] rows = (Object[]) o;
            System.out.println(String.format("teamName(%s) memberCount(%d)", rows[0], rows[1]));
        }
    }

    @Test
    @DisplayName("112. [JPA][READ] JPQL Projection - Closed Class-Based Projection")
    public void read_jqpl_classBased_projection_closed() {
        String jpql = "SELECT new com.example.book.orm.standard.jpa.entity.TeamDTO(t.name, SIZE(t.members)) " +
                " FROM Team t";

        List<TeamDTO> teams = em.createQuery(jpql, TeamDTO.class)
                .getResultList();

        teams.forEach(System.out::println);
    }

    @Test
    @DisplayName("120. [JPA][READ] JPQL 페이징 API")
    @Disabled("TODO")
    public void read_jpql_select_paging() {

    }

    @Test
    @DisplayName("140. [JPA][READ] JPQL ORDER BY with 상태필드경로")
    public void jpql_orderBy_상태필드() {
        String jpql = "SELECT m FROM Member m ORDER BY m.age DESC, m.username ASC";

        List<Member> members = em.createQuery(jpql, Member.class)
                .getResultList();

        members.forEach(System.out::println);
    }

    @Test
    @DisplayName("141. [JPA][READ] JPQL ORDER BY with 결과변수")
    public void jpql_orderBy_결과변수() {
        String jpql = "SELECT t.name, SUM(m.age) as summary " +
                " FROM Member m LEFT JOIN m.team t" +
                " GROUP BY t.name" +
                " ORDER BY summary";

        Query query = em.createQuery(jpql);
        List resultList = query.getResultList();

        for (Object o : resultList) {
            Object[] result = (Object[]) o;
            System.out.println("Team Name: " + result[0]);
            System.out.println("Sum of User Age: " + result[1]);
        }
    }

    @Test
    @DisplayName("200. [JPA][READ] NativeQuery SELECT - 값 조회")
    public void nativeQuery_search_value() {

        String qlString = "SELECT MEMBER_ID, AGE, USERNAME, TEAM_ID" +
                " FROM MEMBER WHERE AGE > ?";

        Query nativeQuery = this.em.createNativeQuery(qlString)
                .setParameter(1, 20);

        List<Object[]> resultList = nativeQuery.getResultList();

        for (Object[] o : resultList) {
            System.out.println("member_id: " + o[0]);
            System.out.println("age: " + o[1]);
            System.out.println("username: " + o[2]);
            System.out.println("team_id: " + o[3]);
        }
    }

    @Test
    @DisplayName("241. [JPA][READ] NativeQuery SELECT - 결과 매핑 사용")
    public void nativeQuery_search_결과매핑사용() {

        String sql = "SELECT M.MEMBER_ID, AGE, USERNAME, TEAM_ID, I.ORDER_COUNT " +
                " FROM MEMBER M" +
                " LEFT JOIN " +
                "     (SELECT IM.MEMBER_ID AS ID, COUNT(*) AS ORDER_COUNT " +
                "      FROM ORDERS O, MEMBER IM " +
                "      WHERE O.MEMBER_ID = IM.MEMBER_ID) I " +
                "ON M.MEMBER_ID = I.ID";

        Query nativeQuery = em.createNativeQuery(sql, "memberWithOrderCount");
        List<Object[]> resultList = nativeQuery.getResultList();

        for (Object[] row : resultList) {
            Member member = (Member) row[0];
            BigInteger orderCount = (BigInteger) row[1];

            System.out.println("member = " + member);
            System.out.println("orderCount = " + orderCount);
        }
    }

    @Test
    @DisplayName("250. [JPA][READ] JOIN(Inner) - JPQL")
    public void join_jpql_inner() {
        //LazyLoading으로 설정했기때문에,
        //JOIN 해도 Team에서 아무것도 조회하지 않으면 SELECT에 Team 관련된 쿼리가 만들어지지는 않는다.
        String jpql = "SELECT m FROM Member m JOIN m.team t";

        List<Member> resultList = em.createQuery(jpql, Member.class).getResultList();

        resultList.forEach(System.out::println);
    }

    @Test
    @DisplayName("251. [JPA][READ] JPQL JOIN - Inner, 여러 엔티티 조회")
    public void join_jpql_inner_multi_entities() {
        String jpql = "SELECT m, t FROM Member m JOIN m.team t";

        List<Object[]> resultList = em.createQuery(jpql).getResultList();

        for (Object[] row : resultList) {
            Member member = (Member) row[0];
            Team team = (Team) row[1];

            System.out.println(member + ", " + team);
        }
    }
}
