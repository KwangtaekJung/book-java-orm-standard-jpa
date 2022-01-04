package com.example.book.orm.standard.jpa.springdatajpa;

import com.example.book.orm.standard.jpa.entity.Member;
import com.example.book.orm.standard.jpa.entity.QMember;
import com.example.book.orm.standard.jpa.entity.QTeam;
import com.example.book.orm.standard.jpa.entity.Team;
import com.example.book.orm.standard.jpa.entity.TeamDTO;
import com.example.book.orm.standard.jpa.repository.springdatajpa.SpringDataJpaMemberRepository;
import com.example.book.orm.standard.jpa.repository.springdatajpa.SpringDataJpaTeamRepository;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.stringPath;

@Tag("ALL")
@Tag("SpringDataJPA")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class SpringDataJPAReadTest {

    @Autowired
    SpringDataJpaMemberRepository memberRepository;

    @Autowired
    SpringDataJpaTeamRepository teamRepository;

    @Autowired
    EntityManager em;

    private final QMember member = new QMember("m");
    private final QTeam team = new QTeam("team");

    private Querydsl querydsl;
//    private EntityManager entityManager;
    private JPAQueryFactory queryFactory;

    @BeforeAll
    public void beforeAll() {

        JpaEntityInformation entityInformation = JpaEntityInformationSupport.getEntityInformation(Team.class, em);
        SimpleEntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;
        EntityPath path = resolver.createPath(entityInformation.getJavaType());
        querydsl = new Querydsl(em, new PathBuilder<>(path.getType(), path.getMetadata()));
        queryFactory = new JPAQueryFactory(em);

        // Team1 ------------------------
        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team1");
        teamRepository.save(team1);

        for (int i = 1; i <= 3; i++) {
            Member member = new Member();
            member.setId((long) (100 + i));
            member.setUsername("User" + member.getId());
            member.setAge(30 + i);
            member.setTeam(team1);
            memberRepository.save(member);
        }

        // Team2 ------------------------
        // CascadeType.PERSIST 를 설정하여 Team을 영속화하면 Member도 함께 영속 상태가 되는지 확인한다.
        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team2");
        teamRepository.save(team2);

        for (int i = 1; i <= 5; i++) {
            Member member = new Member();
            member.setId((long) (200 + i));
            member.setUsername("User" + member.getId());
            member.setAge(40 + i);
            member.setTeam(team2);
            memberRepository.save(member);
        }
    }

    @AfterAll
    public void afterAll() {
        memberRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @Test
    @DisplayName("100. [SpringDataJPA][READ] 기본 Entity 조회 - QueryMethod")
    public void read_members() {
        List<Member> members = memberRepository.findAll();

        Assertions.assertThat(members).isNotNull();
        members.forEach(System.out::println);
    }

    @Test
    @DisplayName("120. [SpringDataJPA][READ] Pageable - QueryDSL")
    public void read_springDataJPA_Pagination_queryMethod() {


        NumberPath<Integer> memberCount = Expressions.numberPath(Integer.class, "mCount");
//        OrderSpecifier<Integer> integerOrderSpecifier = new OrderSpecifier<>(Order.DESC, memberCount);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "mCount"));

        JPAQuery<TeamDTO> jpaQuery = queryFactory
                .select(Projections.constructor(TeamDTO.class,
                        team.name,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(member.count().intValue())
                                        .from(member)
                                        .where(team.id.eq(member.team.id)), memberCount)))
                .from(team);
        //                .orderBy(memberCount.desc())
//                .orderBy(getOrderSpecifier(pageable.getSort()).toArray(OrderSpecifier[]::new))
//                .fetch()
        List<TeamDTO> content = querydsl.applyPagination(pageable, jpaQuery).fetch();
        Page<TeamDTO> teamDTOPage = PageableExecutionUtils.getPage(content, pageable, jpaQuery::fetchCount);

        teamDTOPage.getContent().forEach(System.out::println);

    }

    private List<OrderSpecifier> getOrderSpecifier(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        // Sort
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            PathBuilder<TeamDTO> orderByExpression = new PathBuilder<>(TeamDTO.class, "team");
//            orders.add(new OrderSpecifier(direction, orderByExpression.get(order.getProperty())));
            orders.add(new OrderSpecifier(direction, stringPath(order.getProperty())));
        });
        return orders;
    }

//    @Test
//    @DisplayName("121. [SpringDataJPA][READ] Pagination - QueryDSL")
//    public void read_springDataJPA_closed_projections() {
//
//        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));
//        Page<Team> teamPage = teamRepository.findByConditionsAndPagination(null, pageRequest);
//        List<Team> teams = teamPage.getContent();
//        teams.forEach(System.out::println);
//    }
}
