package ua.kpi.comsys.io8225.labworks.ui.books_list;

import android.graphics.Bitmap;

public class Book {
    public String bookTitle;
    public String bookSubtitle;
    public String bookIsbn13;
    public String bookPrice;
    public String bookImagePath;
    public Bitmap bookImage;

    public String bookDescription;
    public String bookAuthors;
    public String bookPublisher;
    public String bookPages;
    public String bookYear;
    public String bookRating;

    public Book(String title, String subtitle, String price, String isbn13, String imagePath) {
        bookTitle = title;
        bookSubtitle = subtitle;
        bookIsbn13 = isbn13;
        bookPrice = price;
        bookImagePath = imagePath;
    }

    public Book(String title, String subtitle, String price, String isbn13, Bitmap image){
        bookTitle = title;
        bookSubtitle = subtitle;
        bookIsbn13 = isbn13;
        bookPrice = price;
        bookImage = image;
    }

    public Book(String title, String subtitle, String price, String isbn13, String imagePath,
                String description, String authors, String publisher, String pages, String year, String rating) {
        bookTitle = title;
        bookSubtitle = subtitle;
        bookIsbn13 = isbn13;
        bookPrice = price;
        bookImagePath = imagePath;
        bookDescription = description;
        bookAuthors = authors;
        bookPublisher = publisher;
        bookPages = pages;
        bookYear = year;
        bookRating = rating;
    }
}
