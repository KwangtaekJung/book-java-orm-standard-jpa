package com.example.book.orm.standard.jpa.nativequery;

import com.example.book.orm.standard.jpa.entity.Member;
import com.example.book.orm.standard.jpa.entity.Team;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class NativeQueryTest {

    @Autowired
    EntityManagerFactory emf;

    public EntityManager em;

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
    @DisplayName("001. NativeQuery - 엔티티 조회")
    public void nativeQuery_search_entity() {
        em = emf.createEntityManager();

        String qlString = "SELECT MEMBER_ID, AGE, USERNAME, TEAM_ID" +
                " FROM MEMBER WHERE AGE > ?";

        Query nativeQuery = this.em.createNativeQuery(qlString, Member.class)
                .setParameter(1, 20);

        List resultList = nativeQuery.getResultList();

        for (Object o : resultList) {
           Member member = (Member) o;
            System.out.println("member.username: " + member.getUsername());
        }
    }

    @Test
    @DisplayName("002. NativeQuery - 값 조회")
    public void nativeQuery_search_value() {
        em = emf.createEntityManager();

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
    @DisplayName("003. NativeQuery - 결과 매핑 사용")
    public void nativeQuery_search_결과매핑사용() {
        em = emf.createEntityManager();

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
}
