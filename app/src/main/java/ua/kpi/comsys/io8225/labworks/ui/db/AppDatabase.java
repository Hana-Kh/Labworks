package ua.kpi.comsys.io8225.labworks.ui.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BooksEntity.class, SearchEntity.class, GalleryEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BooksDao bookDao();
    public abstract SearchDao searchTableDao();
    public abstract GalleryDao galleryDao();
}
