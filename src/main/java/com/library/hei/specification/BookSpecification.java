package com.library.hei.specification;

import com.library.hei.entity.Book;
import com.library.hei.entity.Genre;
import com.library.hei.entity.Author;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookSpecification {

    public static Specification<Book> titleContains(String title) {
        return (root, query, criteriaBuilder) ->
                title == null || title.isEmpty() ? criteriaBuilder.conjunction() :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> createdAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                date == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Book> createdBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                date == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Book> hasGenre(String genreId) {
        return (root, query, criteriaBuilder) -> {
            if (genreId == null || genreId.isEmpty()) return criteriaBuilder.conjunction();
            Join<Book, Genre> genreJoin = root.join("genres");
            return criteriaBuilder.equal(genreJoin.get("idGenre"), genreId);
        };
    }

    public static Specification<Book> hasAuthor(String authorId) {
        return (root, query, criteriaBuilder) -> {
            if (authorId == null || authorId.isEmpty()) return criteriaBuilder.conjunction();
            Join<Book, Author> authorJoin = root.join("authors");
            return criteriaBuilder.equal(authorJoin.get("idAuthor"), authorId);
        };
    }

    public static Specification<Book> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Book> isbnEquals(String isbn) {
        return (root, query, criteriaBuilder) ->
                isbn == null || isbn.isEmpty() ? criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("isbn"), isbn);
    }
}