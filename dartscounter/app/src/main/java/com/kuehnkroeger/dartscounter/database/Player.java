package com.kuehnkroeger.dartscounter.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Simple class to represent each registered player with his name
 */
@Entity(tableName = "players")
public class Player implements Serializable {

    /** id to uniquely identify player */
    @PrimaryKey(autoGenerate = true)
    public int id;
    /** name of player*/
    public String name;
    @Ignore
    static final long serialVersionUID = 257L;

    /** default constructor */
    public Player(String name) {
        this.name = name;
    }

    /**
     * String representation of player includes only his name
     * @return representation of player
     */
    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
