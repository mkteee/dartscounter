package com.kuehnkroeger.dartscounter.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Repository for interaction with database
 */
public class DartsRepository {

    /** dao object of {@link Player}*/
    private PlayerDao playerDao;
    /** dao object of {@link Match}*/
    private MatchDao matchDao;
    /** live list of all players in database*/
    private LiveData<List<Player>> allPlayers;

    /** default constructor */
    public DartsRepository(Application application) {
        DartsDatabase db = DartsDatabase.getDatabase(application);
        playerDao = db.playerDao();
        matchDao = db.matchDao();
        //live data is thread safe so set at creation of repository
        allPlayers = playerDao.getAllPlayers();
    }

    /**
     * inserts a player into the database
     * @param player player to be inserted
     */
    public void insert(Player player) {
        PlayerModifyTask task = new PlayerModifyTask(playerDao, PlayerModifyTask.MODE.INSERT);
        task.execute(player);
    }

    /**
     * inserts a match into the database
     * @param match match to be inserted
     */
    public void insert(Match match) {
        MatchModifyTask task = new MatchModifyTask(matchDao, MatchModifyTask.MODE.INSERT);
        task.execute(match);
    }

    /**
     * updates a player tuple in the database. Player is identified by their id
     * @param player player to be updated
     */
    public void update(Player player) {
        PlayerModifyTask task = new PlayerModifyTask(playerDao, PlayerModifyTask.MODE.UPDATE);
        task.execute(player);
    }

    /**
     * updates a match tuple in the database. Match is identified by its id
     * @param match match to be updated
     */
    public void update(Match match) {
        MatchModifyTask task = new MatchModifyTask(matchDao, MatchModifyTask.MODE.UPDATE);
        task.execute(match);
    }

    /**
     * deletes a player from the database
     * @param player player to be deleted
     */
    public void delete(Player player) {
        PlayerModifyTask task = new PlayerModifyTask(playerDao, PlayerModifyTask.MODE.DELETE);
        task.execute(player);
    }

    /**
     * deletes a match from the database
     * @param match match to be deleted
     */
    public void delete(Match match) {
        MatchModifyTask task = new MatchModifyTask(matchDao, MatchModifyTask.MODE.DELETE);
        task.execute(match);
    }

    /**
     * returns all players in database as livedata
     * @return all players
     */
    public LiveData<List<Player>> getAllPlayers() {
        return allPlayers;
    }

    /**
     * returns a specific player by their id or {@code null} if this id does not exist
     * @param id id to identify the player
     * @return player or {@code null}
     */
    public Player getPlayerById(int id) {
        FetchPlayer task = new FetchPlayer(playerDao);
        task.execute(id);

        try {
            return task.get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * returns list of all matches a player with the given id has participated in.
     * List is empty if id does not exist or player has not yet played a match.
     * @param playerId id to identify the player
     * @return list of all matches player has participated in
     */
    public List<Match> getMatchesByPlayerId(int playerId) {
        FetchMatches task = new FetchMatches(matchDao);
        task.execute(playerId);

        try {
            return task.get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Custom {@link AsyncTask} to fetch a {@link Player} by their id.
     */
    private static class FetchPlayer extends AsyncTask<Integer, Void, Player> {

        private PlayerDao dao;

        @SuppressWarnings("deprecation")
        FetchPlayer(PlayerDao dao){
            this.dao = dao;
        }

        @Override
        protected Player doInBackground(Integer... integers) {
            return dao.getPlayerById(integers[0]);
        }
    }

    /**
     * Custom {@link AsyncTask} to fetch {@link Match}es by {@link Player} participated.
     */
    private static class FetchMatches extends AsyncTask<Integer, Void, List<Match>> {

        private MatchDao dao;

        @SuppressWarnings("deprecation")
        FetchMatches(MatchDao dao) {
            this.dao = dao;
        }

        @Override
        protected List<Match> doInBackground(Integer... integers) {
            return dao.getMatchesByPlayerId(integers[0]);
        }
    }

    /**
     * Custom {@link AsyncTask} that implements insert/update/delete for {@link Player}s.
     */
    private static class PlayerModifyTask extends AsyncTask<Player, Void, Void> {

        private PlayerDao dao;
        private MODE mode;

        private enum MODE {INSERT, UPDATE, DELETE}

        @SuppressWarnings("deprecation")
        PlayerModifyTask(PlayerDao dao, MODE mode){
            this.dao = dao;
            this.mode = mode;
        }

        @Override
        protected Void doInBackground(Player... players) {
            switch(mode) {
                case INSERT:
                    for(Player player : players)
                        dao.insert(player);
                    break;
                case UPDATE:
                    for(Player player: players)
                        dao.update(player);
                    break;
                case DELETE:
                    for(Player player: players)
                        dao.delete(player);
            }
            return null;
        }
    }

    /**
     * Custom {@link AsyncTask} that implements insert/update/delete for {@link Match}es.
     */
    private static class MatchModifyTask extends AsyncTask<Match, Void, Void> {

        private MatchDao dao;
        private MODE mode;

        private enum MODE {INSERT, UPDATE, DELETE}

        @SuppressWarnings("deprecation")
        MatchModifyTask(MatchDao dao, MODE mode){
            this.dao = dao;
            this.mode = mode;
        }

        @Override
        protected Void doInBackground(Match... matches) {
            switch(mode) {
                case INSERT:
                    for(Match match : matches)
                        dao.insert(match);
                    break;
                case UPDATE:
                    for(Match match: matches)
                        dao.update(match);
                    break;
                case DELETE:
                    for(Match match: matches)
                        dao.delete(match);
            }
            return null;
        }
    }

}
