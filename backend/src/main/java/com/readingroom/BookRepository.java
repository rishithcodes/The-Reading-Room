package com.readingroom;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BookRepository extends JpaRepository<Book,Integer> {
 long countByCategoryId(Integer categoryId);
 long countByAuthorId(Integer authorId);
 long countByPublisherId(Integer publisherId);
}
