package com.library.hei.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_sale", nullable = false, unique = true)
  @JsonIgnoreProperties({"payment", "saleDetails", "hibernateLazyInitializer", "handler"})
  private Sale sale;

  @Column(nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentMethod method;

  public enum PaymentMethod {
    CASH,
    CARD,
    MOBILE_MONEY,
    TRANSFER
  }
}
