package com.example.book.orm.standard.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SqlResultSetMapping(name = "memberWithOrderCount",
        entities = {@EntityResult(entityClass = Member.class)},
        columns = {@ColumnResult(name = "ORDER_COUNT")})
public class Member {

    @Id
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

//    @Embedded
//    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    //연관 관계 매핑 Convenient Method
    public void setTeam(Team team) {
        //기존 연관 관계가 있을 경우 제거
        if (this.team != null) {
            this.team.getMembers().remove(this);
        }

        if (team != null) {
            //연관 관계 설정
            this.team = team;
            this.team.getMembers().add(this);
        } else {
            //연관 관계 제거
            this.team = null;
        }
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
