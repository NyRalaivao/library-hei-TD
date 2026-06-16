package com.library.hei.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "sale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_customer", nullable = false)
  @JsonIgnoreProperties({"sales", "hibernateLazyInitializer", "handler"})
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_user", nullable = false)
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private User seller;

  @Column(name = "sale_date", nullable = false)
  private LocalDateTime saleDate;

  @Column(name = "total_amount", nullable = false)
  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private SaleStatus status = SaleStatus.PENDING;

  @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @JsonIgnoreProperties("sale")
  private List<SaleDetail> saleDetails = new ArrayList<>();

  @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL)
  @JsonIgnoreProperties("sale")
  private Payment payment;

  @PrePersist
  public void prePersist() {
    if (saleDate == null) saleDate = LocalDateTime.now();
    if (totalAmount == null) totalAmount = BigDecimal.ZERO;
  }

  public enum SaleStatus {
    PENDING,
    DONE,
    CANCELLED
  }
}
