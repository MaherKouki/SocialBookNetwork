package com.maher.book.feedback;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.net.ssl.SSLSession;

public interface FeedbackRepository extends JpaRepository<Feedback,Integer> {

}
