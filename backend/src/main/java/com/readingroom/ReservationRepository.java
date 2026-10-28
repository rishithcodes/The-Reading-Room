package com.readingroom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReservationRepository extends JpaRepository<Reservation,Integer> {
 List<Reservation> findByUserIdOrderByCreatedAtDesc(Integer userId);
 boolean existsByUserIdAndBookIdAndStatus(Integer userId,Integer bookId,String status);
 List<Reservation> findByStatus(String status);
 long countByStatus(String status);
}
