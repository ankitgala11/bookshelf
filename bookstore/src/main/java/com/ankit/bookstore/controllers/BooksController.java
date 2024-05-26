package com.ankit.bookstore.controllers;

import com.ankit.bookstore.models.Book;
import com.ankit.bookstore.models.BookDto;
import com.ankit.bookstore.services.BooksRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {

    @Autowired
    private BooksRepository repo;

    @GetMapping({"", "/"}) // Add this annotation to map GET requests
    public String showBooks(Model model) {
        List<Book> books = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("books", books);
        return "books/index";
    }

    @GetMapping("/create") // Add this annotation to map GET requests
    public String showCreatePage(Model model) {
        BookDto bookDto = new BookDto();
        model.addAttribute("bookDto", bookDto);
        return "books/createBook";
    }

    @PostMapping("/create") // Add this annotation to map POST requests
    public String createBook(
            @Valid @ModelAttribute BookDto bookDto,
            BindingResult result
    )  {
        if (bookDto.getImageFile() == null || bookDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("bookDto", "imageFile", "The image file is required"));
        }

        if (result.hasErrors()) {
            return "books/createBook";
        }


        MultipartFile image = bookDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());

        }

        Book book = new Book();
        book.setName(bookDto.getName());
        book.setAuthor(bookDto.getAuthor());
        book.setDescription(bookDto.getDescription());
        book.setCreatedAt(createdAt);
        book.setPrice(bookDto.getPrice());
        book.setImageFileName(storageFileName);

        repo.save(book);


        return "redirect:/books";
    }


    @GetMapping("/edit") // Add this annotation to map POST requests
    public String showEditPage(
            Model model,
            @RequestParam int id
    )  {

        try {

            Book book = repo.findById(id).get();
            model.addAttribute("book" , book);

            BookDto bookDto = new BookDto();
            bookDto.setName(book.getName());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setDescription(book.getDescription());
            bookDto.setPrice(book.getPrice());

            model.addAttribute("bookDto" , bookDto);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/books";

        }
        return "books/edit";


    }

    @PostMapping("/edit") // Add this annotation to map POST requests
    public String updateBook(
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute BookDto bookDto,
            BindingResult result
    )  {

        try {

            Book book = repo.findById(id).get();
            model.addAttribute("book" , book);

            if (result.hasErrors()) {
                return "books/createBook";
            }


            if (!bookDto.getImageFile().isEmpty()) {
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + book.getImageFileName());

                try {
                    Files.delete(oldImagePath);

                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.getMessage());
                }

                MultipartFile image = bookDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }

                book.setImageFileName(storageFileName);

            }


            book.setName(bookDto.getName());
            book.setAuthor(bookDto.getAuthor());
            book.setDescription(bookDto.getDescription());
            book.setPrice(bookDto.getPrice());

            repo.save(book);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());


        }
            return "redirect:/books";

    }


    @GetMapping("/delete") // Add this annotation to map POST requests
    public String deleteBook(
            @RequestParam int id
    )  {

        try {

            Book book = repo.findById(id).get();

            Path image = Paths.get( "public/images/" + book.getImageFileName());

            try{
                Files.delete(image);
            }catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }

            repo.delete(book);


        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());


        }
        return "redirect:/books";

    }

}