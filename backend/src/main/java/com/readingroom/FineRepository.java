package com.readingroom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FineRepository extends JpaRepository<Fine,Integer> {
 List<Fine> findByUserId(Integer userId);
 java.util.Optional<Fine> findByTransactionId(Integer transactionId);
 long countByStatusIn(List<String> statuses);
}
