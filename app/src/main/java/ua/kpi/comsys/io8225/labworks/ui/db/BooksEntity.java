package ua.kpi.comsys.io8225.labworks.ui.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;

import ua.kpi.comsys.io8225.labworks.ui.books_list.Book;

@Entity
public class BooksEntity {
    @PrimaryKey
    private long isbn13;
    private String title;
    private String subtitle;
    private String authors;
    private String publisher;
    private long pages;
    private long year;
    private String rating;
    @ColumnInfo(name = "Description")
    private String desc;
    private String price;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image;

    @Ignore
    public BooksEntity(long isbn13, String title, String subtitle, String price){
        this.isbn13 = isbn13;
        this.title = title;
        this.subtitle = subtitle;
        this.price = price;
        this.authors = "";
        this.desc = "";
        this.pages = 0;
        this.publisher = "";
        this.rating = "";
        this.year = 0;
        this.image = null;
    }

    public BooksEntity(long isbn13, String title, String subtitle, String price,
                      String authors, String desc, long pages, String publisher, String rating, long year){
        this.isbn13 = isbn13;
        this.title = title;
        this.subtitle = subtitle;
        this.price = price;
        this.authors = authors;
        this.desc = desc;
        this.pages = pages;
        this.publisher = publisher;
        this.rating = rating;
        this.year = year;
        this.image = null;
    }

    public Book makeBook(){
        return new Book(title, subtitle, isbn13+"", price, getBitmapImage());
    }

    public Book makeBookInfo(){
        Book result = makeBook();

        result.bookYear = year + "";
        result.bookRating = rating;
        result.bookPages = pages + "";
        result.bookDescription = desc;
        result.bookPublisher = publisher;
        result.bookAuthors = authors;

        return result;
    }

    public void setImage(Bitmap bitmap){
        image = getBitmapAsByteArray(bitmap);
    }

    public Bitmap getBitmapImage() {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public void setInfo(String authors, String desc, long pages, String publisher, String rating, long year){
        this.authors = authors;
        this.desc = desc;
        this.pages = pages;
        this.publisher = publisher;
        this.rating = rating;
        this.year = year;
    }

    public long getIsbn13() {
        return isbn13;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getAuthors() {
        return authors;
    }

    public String getDesc() {
        return desc;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }

    public long getPages() {
        return pages;
    }

    public long getYear() {
        return year;
    }

    public byte[] getImage() {
        return image;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setIsbn13(long isbn13) {
        this.isbn13 = isbn13;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
