package thjava.thbuoi2.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private Double price;

    // Nhiều chi tiết đơn hàng thuộc về một đơn hàng
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // Nhiều chi tiết đơn hàng liên kết tới một sản phẩm
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}