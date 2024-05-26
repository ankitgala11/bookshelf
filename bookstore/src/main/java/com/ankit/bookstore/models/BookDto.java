package com.ankit.bookstore.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class BookDto {

    @NotEmpty(message = "The Book name is required")
    private String name;

    @NotEmpty(message = "The Author name is required")
    private String author;

    @Min(0)
    private double price;

    @Size(min = 10, message = "The description should be at least 10 characters")
    @Size(max = 3000, message = "The description cannot exceed 3000 characters")
    private String description;

    private MultipartFile imageFile;

    // Getter methods
    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    // Setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
