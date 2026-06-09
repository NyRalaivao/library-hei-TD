package com.library.hei.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Book {

  @Id
  private String id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String author;

  @Column(name = "page_numbers")
  private Integer pageNumbers;

  @Column
  private String topic;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookFormat format;

  @Column(name = "release_date")
  private Instant releaseDate;

  @Column(name = "creation_datetime", nullable = false, updatable = false)
  private Instant creationDatetime;

  @PrePersist
  public void prePersist() {
    if (creationDatetime == null) {
      creationDatetime = Instant.now();
    }
  }

  public enum BookFormat {
    NOVEL, MANGA, COMIC, ROMANCE, THRILLER, FANTASY, SCIENCE_FICTION, BIOGRAPHY, HISTORY, OTHER
  }
}
