package ru.urakovzhanat.bankapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.urakovzhanat.bankapi.entity.UserBalance;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {

}