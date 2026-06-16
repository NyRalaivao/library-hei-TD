package com.library.hei.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

  @Id
  @Column(name = "id_book")
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String title;

  @Column(unique = true)
  private String isbn;

  @Column(nullable = false)
  private BigDecimal price;

  // Relations
  @ManyToMany
  @JoinTable(
      name = "book_genre",
      joinColumns = @JoinColumn(name = "id_book"),
      inverseJoinColumns = @JoinColumn(name = "id_genre"))
  @Builder.Default
  private List<Genre> genres = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "book_author",
      joinColumns = @JoinColumn(name = "id_book"),
      inverseJoinColumns = @JoinColumn(name = "id_author"))
  @Builder.Default
  private List<Author> authors = new ArrayList<>();

  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @JsonIgnore
  private List<BookFormat> formats = new ArrayList<>();

  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
  @Builder.Default
  @JsonIgnore
  private List<Arrival> arrivals = new ArrayList<>();
}
