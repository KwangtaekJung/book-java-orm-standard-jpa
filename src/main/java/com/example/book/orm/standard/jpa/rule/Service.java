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
import java.util.List;

@Entity
@Getter @Setter
public class Service {

    @Id @GeneratedValue
    @Column(name = "service_id")
    private Long id;

    private String state;
    private String type;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private Rule rule;

    //==Convenient Method===
    public void setRule(Rule rule) {
        if (this.rule != null) {
            this.rule.getServices().remove(this);
        }
        this.rule = rule;
        this.rule.getServices().add(this);
    }
}
