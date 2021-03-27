package ua.kpi.comsys.io8225.labworks.ui.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BooksDao {
    @Query("SELECT * FROM BooksEntity")
    List<BooksEntity> getAll();

    @Query("SELECT * FROM BooksEntity WHERE isbn13 = :isbn13")
    BooksEntity getByIsbn13(long isbn13);

    @Query("UPDATE BooksEntity " +
            "SET authors = :authors, description = :desc, pages = :pages, " +
            "publisher = :publisher, rating = :rating, year = :year " +
            "WHERE isbn13 = :isbn13")
    void setInfoByIsbn13(long isbn13, String authors, String desc, long pages, String publisher, String rating, long year);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(BooksEntity booksEntity);

    @Update
    void update(BooksEntity booksEntity);

    @Delete
    void delete(BooksEntity booksEntity);
}
