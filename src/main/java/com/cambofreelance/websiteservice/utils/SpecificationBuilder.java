package com.cambofreelance.websiteservice.utils;

import com.cambofreelance.websiteservice.model.dto.request.FilterRequest;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * A generic JPA Specification builder that supports dynamic filters and global search. Works for
 * any entity type.
 */
public class SpecificationBuilder<T> {

    public static <T> Specification<T> build(List<FilterRequest> filters, String search,
                                             List<String> searchFields) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 🔍 Global search (if applicable)
            if (search != null && !search.isBlank() && searchFields != null
                && !searchFields.isEmpty()) {
                String keyword = "%" + search.toLowerCase() + "%";
                List<Predicate> searchPredicates = new ArrayList<>();

                for (String field : searchFields) {
                    try {
                        Path<String> path = (Path<String>) getPath(root, field);
                        searchPredicates.add(cb.like(cb.lower(path.as(String.class)), keyword));
                    } catch (Exception ignored) {
                        // Skip invalid field
                    }
                }

                if (!searchPredicates.isEmpty()) {
                    predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
                }
            }

            // 🎯 Dynamic filters
            Optional.ofNullable(filters).orElse(List.of()).forEach(filter -> {
                String field = filter.getField();
                String operator = filter.getOperator();
                Object value = filter.getValue();

                if (field == null || operator == null) {
                    return;
                }

                try {
                    Path<?> path = getPath(root, field);

                    switch (operator.toLowerCase()) {
                        case "=" -> predicates.add(cb.equal(path, value));
                        case "!=" -> predicates.add(cb.notEqual(path, value));
                        case ">" -> predicates.add(
                            cb.greaterThan(path.as(Comparable.class), (Comparable) value));
                        case ">=" -> predicates.add(
                            cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) value));
                        case "<" -> predicates.add(
                            cb.lessThan(path.as(Comparable.class), (Comparable) value));
                        case "<=" -> predicates.add(
                            cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) value));
                        case "like" -> {
                            if (value instanceof String strVal) {
                                predicates.add(cb.like(cb.lower(path.as(String.class)),
                                    "%" + strVal.toLowerCase() + "%"));
                            }
                        }
                        case "in" -> {
                            if (value instanceof Collection<?> collection) {
                                predicates.add(path.in(collection));
                            }
                        }
                        case "not in" -> {
                            if (value instanceof Collection<?> collection) {
                                predicates.add(cb.not(path.in(collection)));
                            }
                        }
                        case "is null" -> predicates.add(cb.isNull(path));
                        case "is not null" -> predicates.add(cb.isNotNull(path));
                    }
                } catch (Exception ignored) {
                    // Invalid field or type mismatch — skip this filter
                }
            });

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Supports nested fields using dot notation (e.g., "parent.name").
     */
    private static Path<?> getPath(Path<?> root, String field) {
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
            path = path.get(part);
        }
        return path;
    }
}
