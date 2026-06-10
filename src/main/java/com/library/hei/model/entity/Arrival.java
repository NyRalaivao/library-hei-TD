package com.library.hei.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "arrival")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Arrival {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_book", nullable = false)
  private Book book;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_library")
  private Library library;

  @Column(name = "arrival_date", nullable = false)
  private LocalDate arrivalDate;

  @Column(nullable = false)
  private Integer quantity;
}
