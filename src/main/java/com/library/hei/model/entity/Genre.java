package com.library.hei.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genre")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Genre {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column
  private String description;

  @JsonIgnore
  @ManyToMany(mappedBy = "genres")
  @Builder.Default
  private List<Book> books = new ArrayList<>();
}
