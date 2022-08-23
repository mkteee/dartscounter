package com.kuehnkroeger.dartscounter.database;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Match class provides data and statistics for each completed darts match.
 */
@Entity(tableName = "matches")
public class Match implements Serializable {

    /** id to uniquely identify match*/
    @PrimaryKey(autoGenerate = true)
    public int id;
    /** first player that participated */
    @Embedded(prefix = "p1_") public Player player1;
    /** second player that participated */
    @Embedded(prefix = "p2_") public Player player2;
    public boolean winner;
    @Embedded public Statistics statistics;
    @Ignore
    public ArrayList<Integer> pts_history1, pts_history2, throw_history1, throw_history2;
    @Ignore
    private long time;
    @Ignore
    static final long serialVersionUID = 257L;

    /** default constructor */
    public Match(Player player1, Player player2, boolean winner, ArrayList<Integer> pts_history1,
                 ArrayList<Integer> pts_history2, ArrayList<Integer> throw_history1,
                 ArrayList<Integer> throw_history2) {
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
        this.pts_history1 = pts_history1;
        this.pts_history2 = pts_history2;
        this.throw_history1 = throw_history1;
        this.throw_history2 = throw_history2;
        time = System.currentTimeMillis();
    }

    /** constructor used by room */
    public Match(Player player1, Player player2, boolean winner, Statistics statistics) {
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
        this.statistics = statistics;
    }

    public void saveStatisticsFromMatch(){
        statistics = new Statistics();

        statistics.pointsPlayer1 = pts_history1.get(0);
        statistics.pointsPlayer2 = pts_history2.get(0);

        int dartNo = 1;
        for(int throw_value : this.throw_history1){
            if(throw_value == -1)
                dartNo = 1;
            else {
                int score = (throw_value/100)*(throw_value%100);
                if(dartNo == 1) {
                    statistics.sumDart1Player1 += score;
                    statistics.amountDart1Player1++;
                }
                else if(dartNo == 2) {
                    statistics.sumDart2Player1 += score;
                    statistics.amountDart2Player1++;
                }
                else if(dartNo == 3) {
                    statistics.sumDart3Player1 += score;
                    statistics.amountDart3Player1++;
                }
                dartNo++;
            }
        }
        dartNo = 1;
        for(int throw_value : this.throw_history2){
            if(throw_value == -1)
                dartNo = 1;
            else {
                int score = (throw_value/100)*(throw_value%100);
                if(dartNo == 1) {
                    statistics.sumDart1Player2 += score;
                    statistics.amountDart1Player2++;
                }
                else if(dartNo == 2) {
                    statistics.sumDart2Player2 += score;
                    statistics.amountDart2Player2++;
                }
                else if(dartNo == 3) {
                    statistics.sumDart3Player2 += score;
                    statistics.amountDart3Player2++;
                }
                dartNo++;
            }
        }
        int sumDart = 0;
        for(int throw_value : this.throw_history1){
            if(throw_value == -1){
                sumDart=0;
            }else {
                sumDart += throw_value;
            }
        }
        sumDart = 0;
        for(int throw_value : this.throw_history2){
            if(throw_value == -1){
                sumDart=0;
            }else {
                sumDart += throw_value;
            }
        }
        int index = 1;
        sumDart = 0;
        if(winner){
            while((this.throw_history1.get(this.throw_history1.size()-index)) != -1){
                int throw_value = this.throw_history1.get(this.throw_history1.size()-index);
                sumDart += (throw_value/100) * (throw_value%100);
                index++;
            }
            statistics.finishPlayer1 = sumDart;
            statistics.finishPlayer2 = -1;
        }else{
            while((this.throw_history2.get(this.throw_history2.size()-index)) != -1){
                int throw_value = this.throw_history2.get(this.throw_history2.size()-index);
                sumDart += (throw_value/100) * (throw_value%100);
                index++;
            }
            statistics.finishPlayer2 = sumDart;
            statistics.finishPlayer1 = -1;
        }
        sumDart = 0;
        for(int throw_value : this.throw_history1){
            if(throw_value == -1){
                if(sumDart > statistics.highestScorePlayer1)
                    statistics.highestScorePlayer1 = sumDart;
                if(sumDart >= 70)
                    statistics.plus70Player1++;
                if(sumDart >= 90)
                    statistics.plus90Player1++;
                if(sumDart >= 130)
                    statistics.plus130Player1++;
                sumDart = 0;
            }else{
                int score = (throw_value/100) * (throw_value%100);
                sumDart += score;
            }
        }
        if(sumDart > statistics.highestScorePlayer1){
            statistics.highestScorePlayer1 = sumDart;
        }
        sumDart = 0;
        for(int throw_value : this.throw_history2){
            if(throw_value == -1){
                if(sumDart > statistics.highestScorePlayer2)
                    statistics.highestScorePlayer2 = sumDart;
                if(sumDart >= 70)
                    statistics.plus70Player2++;
                if(sumDart >= 90)
                    statistics.plus90Player2++;
                if(sumDart >= 130)
                    statistics.plus130Player2++;
                sumDart = 0;

            }else{
                int score = (throw_value/100) * (throw_value%100);
                sumDart += score;
            }
        }
        if(sumDart > statistics.highestScorePlayer2){
            statistics.highestScorePlayer2 = sumDart;
        }
    }
    @NonNull
    public String toString(Locale l) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, l);
        return String.format(l, "%s - %s # %s", player1.name, player2.name,
                df.format(new Date(time)));
    }
}
