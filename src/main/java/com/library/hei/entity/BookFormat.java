package com.library.hei.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "book_format")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookFormat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_format")
    private String idFormat;

    @ManyToOne
    @JoinColumn(name = "id_book", nullable = false)
    private Book book;

    @Column(name = "cover_type")
    private String coverType;

    private BigDecimal price;
    private Integer stock;
}