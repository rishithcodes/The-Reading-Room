package com.readingroom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TransactionRepository extends JpaRepository<BookTransaction,Integer> {
 List<BookTransaction> findByUserIdOrderByIssueDateDesc(Integer userId);
 List<BookTransaction> findByStatusInOrderByDueDateAsc(List<String> statuses);
 long countByStatusIn(List<String> statuses);
}
