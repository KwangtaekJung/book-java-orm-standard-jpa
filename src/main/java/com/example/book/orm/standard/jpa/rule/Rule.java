package com.example.book.orm.standard.jpa.rule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Rule {

    @Id @GeneratedValue
    @Column(name = "rule_id")
    private Long id;

    private String state;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;

    @OneToMany(mappedBy = "rule")
    private List<Network> sourceNetworks = new ArrayList<>();

    @OneToMany(mappedBy = "rule")
    private List<Network> destinationNetworks = new ArrayList<>();

    @OneToMany(mappedBy = "rule")
    private List<Service> services = new ArrayList<>();

    //==Convenient Method===
    public void setPolicy(Policy policy) {
        if (this.policy != null) {
            this.policy.getRules().remove(this);
        }
        this.policy = policy; //연관관계의 주인 쪽에 설정해야 DB에 정상적으로 저장된다.
        this.policy.getRules().add(this);
    }
}
