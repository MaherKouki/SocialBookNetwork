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

    public BookResponse toBookResponse(Book book){
        return BookResponse
                .builder()
                .id(book.getId())
                .title(book.getTitle())
                .authName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().fullName())
                //.cover()
                .build();
    }


}
