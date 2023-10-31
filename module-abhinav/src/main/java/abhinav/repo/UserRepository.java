package abhinav.repo;


import abhinav.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    User findByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE last_login <= :sevenDaysAgo", nativeQuery = true)
    List<User> findUsersNotLoggedInFor7Days(@Param("sevenDaysAgo") LocalDate sevenDaysAgo);

    @Query("SELECT u FROM User u WHERE u.lastLogin >= :lastLoginTime")
    List<User> findUsersNotLoggedInSince(@Param("lastLoginTime") LocalDateTime lastLoginTime);



}
