package com.maher.book.book;


import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public Book toBook(BookRequest request){
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .shareable(request.shareable())
                .shareable(false)
                .build();
    }


}
