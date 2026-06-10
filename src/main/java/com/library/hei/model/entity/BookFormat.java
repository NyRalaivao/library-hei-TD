package com.library.hei.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "book_format")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BookFormat {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_book", nullable = false)
  private Book book;

  @Enumerated(EnumType.STRING)
  @Column(name = "cover_type", nullable = false)
  private CoverType coverType;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(nullable = false)
  private Integer stock;

  public enum CoverType {
    POCKET, SMALL, MEDIUM, LARGE, HARDCOVER, PAPERBACK
  }
}
