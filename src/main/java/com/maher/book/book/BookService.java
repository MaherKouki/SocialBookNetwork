package com.maher.book.book;


import com.maher.book.common.PageResponse;
import com.maher.book.exception.OperationNotPermittedException;
import com.maher.book.file.FileStorageService;
import com.maher.book.history.BookTransactionHistory;
import com.maher.book.history.BookTransactionHistoryRepository;
import com.maher.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(()-> new EntityNotFoundException("No book found"));

    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {

        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page , size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable ,user.getId());

        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page , size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()) , pageable);
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );

    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page , size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable , user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page , size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable , user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("no book found with Id::" + bookId));
        User user = ((User) connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId() , user.getId())){
            throw new OperationNotPermittedException("You cannot update others books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("no book found with Id::" + bookId));
        User user = ((User) connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId() , user.getId())){
            throw new OperationNotPermittedException("You cannot update others books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;

    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with the id : "+ bookId));

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("the requested book cannot be borrowed since it's archived or not shareable");
        }

        User user = ((User) connectedUser.getPrincipal());

        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot update your owen book");
        }

        final boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());

        if(isAlreadyBorrowed){
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnedApproved(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with the id : "+ bookId));

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("the requested book cannot be borrowed since it's archived or not shareable");
        }

        User user = ((User) connectedUser.getPrincipal());
        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot borrow or return your owen book");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId,user.getId())
                .orElseThrow(()-> new OperationNotPermittedException("You did not borrow this book"));
        bookTransactionHistory.setReturned(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with the id : "+ bookId));

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("the requested book cannot be borrowed since it's archived or not shareable");
        }

        User user = ((User) connectedUser.getPrincipal());
        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot borrow or return your owen book");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId,user.getId())
                .orElseThrow(()-> new OperationNotPermittedException("The book is not returned yet , you cannot approve its return  "));

        bookTransactionHistory.setReturnedApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();

    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with the id : "+ bookId));
        User user = ((User) connectedUser.getPrincipal());
        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot borrow or return your owen book");
        }
        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }
}
