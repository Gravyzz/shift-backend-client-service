package ru.shift.userimporter.core.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.shift.userimporter.core.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> withFilters(Long phone, String name, String lastName, String email) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (phone != null) {
                predicates.add(cb.equal(root.get("phone"), phone.toString()));
            }
            if (name != null) {
                predicates.add(cb.equal(root.get("firstName"), name));
            }
            if (lastName != null) {
                predicates.add(cb.equal(root.get("lastName"), lastName));
            }
            if (email != null) {
                predicates.add(cb.equal(root.get("email"), email));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
