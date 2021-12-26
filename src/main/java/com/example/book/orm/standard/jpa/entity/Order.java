package com.example.book.orm.standard.jpa.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ORDERS")
@Data
public class Order {

        @Id
        @Column(name = "ORDER_ID")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "MEMBER_ID")
        private Member member;      //주문 회원

        @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
        private List<OrderItem> orderItems = new ArrayList<OrderItem>();

        @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @JoinColumn(name = "DELIVERY_ID")
        private Delivery delivery;  //배송정보

        private Date orderDate;     //주문시간

        @Enumerated(EnumType.STRING)
        private OrderStatus status;//주문상태


        //==연관관계 메서드==//
        public void setMember(Member member) {
            this.member = member;
            member.getOrders().add(this);
        }

        public void addOrderItem(OrderItem orderItem) {
            orderItems.add(orderItem);
            orderItem.setOrder(this);
        }

        public void setDelivery(Delivery delivery) {
            this.delivery = delivery;
            delivery.setOrder(this);
        }

    }
