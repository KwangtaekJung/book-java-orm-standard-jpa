package com.example.book.orm.standard.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Team {

    @Id
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    //영속성 전이: 자식 연관 관계의 Entity도 함께 영속 상태로 만든다.
    @OneToMany(mappedBy = "team", cascade = CascadeType.PERSIST)
    List<Member> members = new ArrayList<>();

    public void setMember(Member member) {
        //연관 관계 주인쪽에 구현되어 있는 Convenient Method 활용
        member.setTeam(this);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
