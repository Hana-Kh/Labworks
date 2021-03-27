package ua.kpi.comsys.io8225.labworks.ui.db;

import androidx.room.TypeConverter;

import java.util.ArrayList;

public class BooksConverter {
    @TypeConverter
    public String fromList(ArrayList<Long> isbns) {
        StringBuilder result = new StringBuilder();
        for (long isbn13 :
                isbns) {
            result.append(isbn13).append(" ");
        }
        return result.toString();
    }

    @TypeConverter
    public ArrayList<Long> toList(String data) {
        ArrayList<Long> result = new ArrayList<>();
        if (data.length() > 0)
            for (String isbn13 :
                    data.split(" ")) {
                result.add(Long.parseLong(isbn13));
            }
        return result;
    }
}