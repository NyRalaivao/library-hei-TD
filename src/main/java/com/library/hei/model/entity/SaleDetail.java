package com.library.hei.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sale_detail")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SaleDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_sale", nullable = false)
  @JsonIgnoreProperties({"saleDetails", "payment", "hibernateLazyInitializer", "handler"})
  private Sale sale;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_format", nullable = false)
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private BookFormat bookFormat;

  @Column(nullable = false)
  private Integer quantity;

  @Column(name = "unit_price", nullable = false)
  private BigDecimal unitPrice;

  @Column(name = "total_price", nullable = false)
  private BigDecimal totalPrice;

  @PrePersist
  @PreUpdate
  public void computeTotal() {
    if (unitPrice != null && quantity != null) {
      totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
  }
}
