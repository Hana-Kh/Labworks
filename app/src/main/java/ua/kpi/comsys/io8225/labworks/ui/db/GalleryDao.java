package ua.kpi.comsys.io8225.labworks.ui.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GalleryDao {
    @Query("SELECT * FROM GalleryEntity")
    List<GalleryEntity> getAll();

    @Query("SELECT imageUrl FROM GalleryEntity")
    List<String> getAllUrls();

    @Query("SELECT * FROM GalleryEntity WHERE id = :id")
    GalleryEntity getById(long id);

    @Query("SELECT * FROM GalleryEntity WHERE imageUrl = :url")
    GalleryEntity getByUrl(String url);

    @Query("UPDATE GalleryEntity SET imageData = :data WHERE imageUrl = :url")
    void setImageBitmapByUrl(String url, byte[] data);

    @Query("UPDATE GalleryEntity SET imageData = :data WHERE id = :id")
    void setImageBitmapById(long id, byte[] data);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(GalleryEntity galleryEntity);

    @Update
    void update(GalleryEntity galleryEntity);

    @Delete
    void delete(GalleryEntity galleryEntity);
}