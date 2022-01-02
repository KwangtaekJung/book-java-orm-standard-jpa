package com.example.book.orm.standard.jpa.repository.jpa;

import com.example.book.orm.standard.jpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

public class JpaMemberRepository {

    @PersistenceContext
    EntityManager em;

    /**
     * CREATE
     */
    public void save(Member member) {
        //Entity 단위로 em.persist 이용한다.
        //insert에 대한 JPQL은 지원하지 않는다.
        //native query(createNativeQuery)를 통해서 가능하긴 하지만 DB에 직접 쓰는 형태이기 때문에 영속성 컨텍스트의 무결성이 깨질 수 있다.
        em.persist(member);
    }

    /**
     * READ
     */
    public Member findOne(Long id) {
        //방법1. em.find
        //방법2. JPQL
        Member member = em.createQuery("select m from Member m where m.id = :id", Member.class)
                .setParameter("id", id)
                .getSingleResult();
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        //EntityManager 직접 제공하는 method 없음. JPQL 이용해야 한다.
        String queryString = "select m from Member m";
        TypedQuery<Member> typedQuery = em.createQuery(queryString, Member.class);

        return typedQuery.getResultList();
    }

    public List<Member> findByName(String name) {
        //EntityManager 직접 제공하는 method 없음. JPQL 이용해야 한다.
        String queryString = "select m from Member m where m.username = :name";
        TypedQuery<Member> typedQuery = em.createQuery(queryString, Member.class)
                .setParameter("name", name);

        return typedQuery.getResultList();
    }

    /**
     * UPDATE
     */
    public Member updateMember(Long id) {
        //JPA는 update 쿼리를 사용하지 않고 변경 감지에 의해 엔티티를 수정한다.
        //JPA가 직접 지원하지는 않지만 query(createNativeQuery)를 이용할 수는 있다.
        //하지만 이 경우 DB에 직접 넣기 때문에 영속성 컨텍스트의 무결성이 깨질수 있으니 주의해야 한다.

        return null;
    }

    /**
     * DELETE
     */
    public void deleteMember(Long id) {
        //엔티티를 삭제하려면 먼저 삭제 대상 엔티티를 조회해야 한다.
        Member member = em.find(Member.class, id);
        em.remove(member);
    }
}
