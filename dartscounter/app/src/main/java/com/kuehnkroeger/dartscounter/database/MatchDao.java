package com.kuehnkroeger.dartscounter.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object for {@link Match} class.
 * Supports default {@link #insert(Match)}, {@link #update(Match) and {@link #delete(Match)}}
 * operations and additional specific query operations.
 */
@Dao
public interface MatchDao {

    @Insert
    void insert(Match match);

    @Update
    void update(Match match);

    @Delete
    void delete(Match match);

    @Query("select * from matches where p1_id is :id or p2_id is :id")
    List<Match> getMatchesByPlayerId(int id);
}
