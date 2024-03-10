package ru.previak.restaurant.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "_order")
@Entity
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    Long userId;

    Date startDate;

    Date endDate;

    @ElementCollection
    @CollectionTable(name = "_order_dishes", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyJoinColumn(name = "dish_id")
    @Column(name = "dish_amount")
    Map<String, Long> dishes;

    @Enumerated(EnumType.STRING)
    OrderStatus status;
}
