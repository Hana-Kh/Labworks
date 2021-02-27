package ua.kpi.comsys.io8225.labworks.ui.books_list;

public class Book {
    public String bookTitle;
    public String bookSubtitle;
    public String bookIsbn13;
    public String bookPrice;
    public String bookImagePath;

    public Book(String title, String subtitle, String price, String isbn13, String imagePath){
        bookTitle = title;
        bookSubtitle = subtitle;
        bookIsbn13 = price;
        bookPrice = isbn13;
        bookImagePath = imagePath;
    }
}
