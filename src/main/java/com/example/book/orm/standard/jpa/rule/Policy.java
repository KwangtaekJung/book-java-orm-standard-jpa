package com.example.book.orm.standard.jpa.rule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Policy {

    @Id @GeneratedValue
    @Column(name = "policy_id")
    private Long id;

    private String name;
    private String state;

    @OneToMany(mappedBy = "policy")
    private List<Rule> rules = new ArrayList<>();

    //===Convenient Method===
    public void setRule(Rule rule) {
        rule.setPolicy(this);
//        this.rules.add(rule);
    }
}
