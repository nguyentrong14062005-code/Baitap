package thjava.thbuoi2.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Double price;

    private String description;

    // Quan hệ Nhiều - Một: Nhiều sản phẩm thuộc về một danh mục
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}