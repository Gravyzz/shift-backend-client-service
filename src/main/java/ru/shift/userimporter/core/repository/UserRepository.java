package ru.shift.userimporter.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.shift.userimporter.core.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByPhone(String phone);

    @Query(value = """
            SELECT * FROM users
            WHERE (CAST(:phone AS text) IS NULL OR phone = :phone)
              AND (CAST(:name AS text) IS NULL OR first_name = :name)
              AND (CAST(:lastName AS text) IS NULL OR last_name = :lastName)
              AND (CAST(:email AS text) IS NULL OR email = :email)
            ORDER BY id
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<User> search(@Param("phone") String phone,
                      @Param("name") String name,
                      @Param("lastName") String lastName,
                      @Param("email") String email,
                      @Param("limit") int limit,
                      @Param("offset") int offset);
}
