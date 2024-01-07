package ru.urakovzhanat.bankapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.urakovzhanat.bankapi.entity.Operation;

import java.util.Date;
import java.util.List;
@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    List<Operation> findByUserId(Long userId);

    @Query("SELECT o FROM Operation o WHERE o.userId = :userId AND o.operationDate BETWEEN :startDate AND :endDate")
    List<Operation> findByUserIdAndOperationDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );
}
