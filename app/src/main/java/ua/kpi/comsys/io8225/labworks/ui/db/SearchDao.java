package ua.kpi.comsys.io8225.labworks.ui.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SearchDao {
    @Query("SELECT * FROM searchentity")
    List<SearchEntity> getAll();

    @Query("SELECT * FROM searchentity WHERE id = :id")
    SearchEntity getById(long id);

    @Query("SELECT * FROM searchentity WHERE searchQueue = :query ORDER BY id DESC LIMIT 1")
    SearchEntity getLastByQuery(String query);

    @Query("SELECT * FROM searchentity ORDER BY id DESC LIMIT 1")
    SearchEntity getLastSearch();

    @Insert
    void insert(SearchEntity searchEntity);

    @Update
    void update(SearchEntity searchEntity);

    @Delete
    void delete(SearchEntity searchEntity);
}
