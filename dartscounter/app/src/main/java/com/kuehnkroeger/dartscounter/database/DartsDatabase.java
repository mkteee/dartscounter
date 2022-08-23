package com.kuehnkroeger.dartscounter.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Database with two entities, {@link Player} and {@link Match}.
 * Provides method to get an instance of the database.
 */
@Database(entities = {Player.class, Match.class}, version = 3)
public abstract class DartsDatabase extends RoomDatabase {

    /** dao object of {@link Player}*/
    public abstract PlayerDao playerDao();
    /** dao object of {@link Match}*/
    public abstract MatchDao matchDao();
    /** class instance of database */
    private static DartsDatabase instance;

    /**
     * if no instance of database exists a new one will be built and then returned
     * @param context Application from which the database can be built
     * @return instance of database
     */
    static synchronized DartsDatabase getDatabase(final Context context) {
        if(instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), DartsDatabase.class,
                    "matchdata").build();
        return instance;
    }

}
