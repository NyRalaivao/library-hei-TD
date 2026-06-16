package com.library.hei.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "book_format")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookFormat {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_book", nullable = false)
  @JsonIgnoreProperties({"formats", "arrivals", "hibernateLazyInitializer", "handler"})
  private Book book;

  @Enumerated(EnumType.STRING)
  @Column(name = "cover_type", nullable = false)
  private CoverType coverType;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(nullable = false)
  private Integer stock;

  public enum CoverType {
    POCKET,
    SMALL,
    MEDIUM,
    LARGE,
    HARDCOVER,
    PAPERBACK
  }
}
