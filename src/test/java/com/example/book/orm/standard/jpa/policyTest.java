package com.example.book.orm.standard.jpa;

import com.example.book.orm.standard.jpa.entity.rule.Network;
import com.example.book.orm.standard.jpa.entity.rule.Policy;
import com.example.book.orm.standard.jpa.entity.rule.Rule;
import com.example.book.orm.standard.jpa.entity.rule.Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class policyTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    @DisplayName("연관 관계 저장 - 주인쪽(@ManyToOne) 쪽에 설정")
    public void create() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        Policy policy = new Policy();
        policy.setName("Policy1");
        em.persist(policy);
        transaction.commit();

        transaction.begin();
        Rule rule1 = new Rule();
        rule1.setState("Creating");
        rule1.setPolicy(policy);
        em.persist(rule1);
        transaction.commit();

        transaction.begin();
        Rule rule2 = new Rule();
        rule2.setState("Creating");
        rule2.setPolicy(policy);
        em.persist(rule2);
        transaction.commit();

        transaction.begin();
        Network sourceNetwork = new Network();
        sourceNetwork.setType("SOURCE");
        sourceNetwork.setIpAddress("10.10.10.10");
        sourceNetwork.setRule(rule1);
        em.persist(sourceNetwork);
        transaction.commit();

        transaction.begin();
        Network sourceNetwork2 = new Network();
        sourceNetwork2.setType("SOURCE");
        sourceNetwork2.setIpAddress("11.11.11.11");
        sourceNetwork2.setRule(rule1);
        em.persist(sourceNetwork2);
        transaction.commit();

        transaction.begin();
        Network network3 = new Network();
        network3.setType("DESTINATION");
        network3.setIpAddress("20.20.20.20");
        network3.setRule(rule1);
        em.persist(network3);
        transaction.commit();

        transaction.begin();
        Network network4 = new Network();
        network4.setType("DESTINATION");
        network4.setIpAddress("21.21.21.21");
        network4.setRule(rule1);
        em.persist(network4);
        transaction.commit();

        transaction.begin();
        Service service1 = new Service();
        service1.setType("TCP");
        service1.setRule(rule1);
        em.persist(service1);
        transaction.commit();

        transaction.begin();
        Service service2 = new Service();
        service2.setType("UDP");
        service2.setRule(rule1);
        em.persist(service2);
        transaction.commit();

        //엔티티 조회
        Policy policy1 = em.find(Policy.class, 4L);
        System.out.println(policy1.getRules().get(0).getSourceNetworks().get(1).getIpAddress());

        System.out.println("END");
    }

    @Test
    @DisplayName("연관 관계 저장 - 주인 아닌 쪽(@OneToMany) 쪽에 설정하기")
    public void entityTest() {

        //원래 연관관계 저장은 주인쪽에서 설정해야한다.
        //편의 상 주인이 아닌쪽에서 설정하려면, 주인쪽에 설정되어 있는 편의 메소드를 콜하는 방식으로 가능하게 해준다.

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();

        Rule rule1 = new Rule();
        rule1.setState("Creating");
        em.persist(rule1);

        Policy policy = new Policy();
        policy.setName("Policy1");
        policy.setRule(rule1);
        em.persist(policy);

        transaction.commit();

        System.out.println("END");
    }
}
