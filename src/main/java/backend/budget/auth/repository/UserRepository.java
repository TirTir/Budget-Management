package backend.budget.auth.repository;

import backend.budget.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserName(String userName);
    Optional<User> findByUserName(String userName);
    List<User> findByDiscordWebhookUrlIsNotNull();
}