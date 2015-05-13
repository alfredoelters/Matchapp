package android.clase.obligatorio1.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by alfredo on 12/05/15.
 */
public class LeagueTableStanding implements Serializable{
    public static String JSON_POSITION = "position";
    public static String JSON_TEAM_NAME = "teamName";
    public static String JSON_PLAYED_GAMES = "playedGames";
    public static String JSON_POINTS = "points";
    public static String JSON_GOALS = "goals";
    public static String JSON_GOALS_AGAINST = "goalsAgainst";
    public static String JSON_GOALS_DIFFERENCE = "goalDifference";

    public static String JSON_LINKS = "_links";
    public static String JSON_TEAM_LINK = "team";

    private Integer position;
    private String teamName;
    private Integer playedGames;
    private Integer points;
    private Integer goals;
    private Integer goalsAgainst;
    private Integer goalDifference;


    public  LeagueTableStanding(JSONObject json) throws JSONException {
        if (json != null) {
            position = json.getInt(JSON_POINTS);
            teamName = json.getString(JSON_TEAM_NAME);
            playedGames = json.getInt(JSON_PLAYED_GAMES);
            points = json.getInt(JSON_POINTS);
            goals = json.getInt(JSON_GOALS);
            goalsAgainst = json.getInt(JSON_GOALS_AGAINST);
            goalDifference = json.getInt(JSON_GOALS_DIFFERENCE);
        }
    }

    public Integer getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(Integer goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public Integer getGoals() {
        return goals;
    }

    public void setGoals(Integer goals) {
        this.goals = goals;
    }

    public Integer getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(Integer goalDifference) {
        this.goalDifference = goalDifference;
    }

    public Integer getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(Integer playedGames) {
        this.playedGames = playedGames;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}