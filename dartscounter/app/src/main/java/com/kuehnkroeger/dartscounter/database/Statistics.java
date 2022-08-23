package com.kuehnkroeger.dartscounter.database;

import java.io.Serializable;

/**
 * holds stats for a match for both players.
 */
public class Statistics implements Serializable {

    /** Collection of important stats */
    public int pointsPlayer1;
    public int pointsPlayer2;

    public int sumDart1Player1;
    public int amountDart1Player1;
    public int sumDart1Player2;
    public int amountDart1Player2;
    public int sumDart2Player1;
    public int amountDart2Player1;
    public int sumDart2Player2;
    public int amountDart2Player2;
    public int sumDart3Player1;
    public int amountDart3Player1;
    public int sumDart3Player2;
    public int amountDart3Player2;
    public int finishPlayer1;
    public int finishPlayer2;
    public int highestScorePlayer1;
    public int highestScorePlayer2;
    public int plus70Player1;
    public int plus70Player2;
    public int plus90Player1;
    public int plus90Player2;
    public int plus130Player1;
    public int plus130Player2;

    static final long serialVersionUID = 257L;
}
