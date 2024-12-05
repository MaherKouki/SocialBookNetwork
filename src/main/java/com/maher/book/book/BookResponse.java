package com.maher.book.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {

    private Integer id;
    private String title;
    private String authName;
    private String isbn;
    private String synopsis;
    private String owner;
    private byte[] cover;
    private boolean archived;
    private double rate;
    private boolean shareable;


}
