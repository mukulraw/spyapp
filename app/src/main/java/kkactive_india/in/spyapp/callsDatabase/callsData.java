package kkactive_india.in.spyapp.callsDatabase;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;


@Database(entities = { callsDb.class }, version = 1)
@TypeConverters({Converters.class})
public abstract class callsData extends RoomDatabase {

    public abstract callsDao callsDao();
    private static volatile callsData INSTANCE;

    static callsData getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (callsData.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            callsData.class, "callsData")
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
