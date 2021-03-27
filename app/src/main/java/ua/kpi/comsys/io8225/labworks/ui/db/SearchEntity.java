package ua.kpi.comsys.io8225.labworks.ui.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;

@Entity
public class SearchEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String searchQueue;
    @TypeConverters({BooksConverter.class})
    public ArrayList<Long> searchedBooks;
}
