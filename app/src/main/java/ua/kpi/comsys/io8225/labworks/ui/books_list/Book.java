package ua.kpi.comsys.io8225.labworks.ui.books_list;

public class Book {
    public String bookTitle;
    public String bookSubtitle;
    public String bookIsbn13;
    public String bookPrice;
    public String bookImagePath;

    public String bookDescription;
    public String bookAuthors;
    public String bookPublisher;
    public String bookPages;
    public String bookYear;
    public String bookRating;

    public Book(String title, String subtitle, String price, String isbn13, String imagePath) {
        bookTitle = title;
        bookSubtitle = subtitle;
        bookIsbn13 = price;
        bookPrice = isbn13;
        bookImagePath = imagePath;
    }

    public Book(String title, String subtitle, String price, String isbn13, String imagePath,
                String description, String authors, String publisher, String pages, String year, String rating) {
        bookTitle = title;
        bookSubtitle = subtitle;
        bookIsbn13 = price;
        bookPrice = isbn13;
        bookImagePath = imagePath;
        bookDescription = description;
        bookAuthors = authors;
        bookPublisher = publisher;
        bookPages = pages;
        bookYear = year;
        bookRating = rating;
    }
}
