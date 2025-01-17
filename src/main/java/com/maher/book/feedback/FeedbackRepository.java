package com.maher.book.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.net.ssl.SSLSession;

public interface FeedbackRepository extends JpaRepository<Feedback,Integer> {


    @Query("""
            select feedback
            from Feedback
            where feedback.book.id = :bookId 
            """)
    Page<Feedback> findAllByBookId(Integer bookId, Pageable pageable);
}
