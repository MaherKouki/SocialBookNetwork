package com.maher.book.feedback;

import com.maher.book.book.Book;

public class FeedbackMapper {


    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                 .note(request.note())
                 .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .archived(false) //not required and has no impact it's just to satify the compiler
                        .shareable(false)  //not required and has no impact it's just to satify the compiler
                        .build()
                )
                .build();
    }
}
