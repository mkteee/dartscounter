package com.kuehnkroeger.dartscounter.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object for {@link Player} class.
 * Supports default {@link #insert(Player)}, {@link #update(Player) and {@link #delete(Player)}}
 * operations and additional specific query operations.
 */
@Dao
public interface PlayerDao {

    @Insert
    void insert(Player player);

    @Update
    void update(Player player);

    @Delete
    void delete(Player player);

    @Query("select * from players")
    LiveData<List<Player>> getAllPlayers();

    @Query("select * from players where id = :id")
    Player getPlayerById(int id);
}
