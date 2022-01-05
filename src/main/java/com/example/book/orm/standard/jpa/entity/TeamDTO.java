package com.example.book.orm.standard.jpa.entity;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TeamDTO {

    private String name;
    private Integer memberCount;

    @QueryProjection
    public TeamDTO(String name, Integer memberCount) {
        this.name = name;
        this.memberCount = memberCount;
    }
}
