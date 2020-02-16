package nodopezzz.android.wishlist.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DBItemDao {
    @Query("SELECT * FROM DBItem WHERE content = :content")
    List<DBItem> getAllByContent(String content);

    @Query("SELECT * FROM DBItem WHERE id = :id AND content = :content")
    DBItem getMovieById(String id, String content);

    @Insert
    void insert(DBItem DBItem);

    @Update
    void update(DBItem DBItem);

    @Delete
    void delete(DBItem DBItem);
}
