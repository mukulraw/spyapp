package kkactive_india.in.spyapp.callsDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface callsDao {

    @Query("SELECT * FROM callLogs")
    List<callsDb> getAll();

    @Query("DELETE FROM callLogs")
     void nukeTable();

    @Insert
    void insertAll(callsDb... calls);

    @Insert
    void insert(callsDb... calls);

    @Delete
    void delete(callsDb calls);

}
