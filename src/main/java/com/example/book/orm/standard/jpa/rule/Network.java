package com.example.book.orm.standard.jpa.rule;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
@Getter @Setter
public class Network {

    @Id @GeneratedValue
    @Column(name = "network_id")
    private Long id;

    private String type;
    private String ipAddress;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private Rule rule;

    public void setRule(Rule rule) {
        if (this.rule != null) {
            //TODO
        }

        this.rule = rule;
        if (Objects.equals(this.type, "SOURCE")) {
            this.rule.getSourceNetworks().add(this);
        } else if (Objects.equals(this.type, "DESTINATION")) {
            this.rule.getDestinationNetworks().add(this);
        }
    }
}
