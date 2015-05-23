package android.clase.obligatorio1.entities;

import android.clase.obligatorio1.constants.JsonKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * created by Alfredo El Ters and Mathias Cabano on 12/05/15.
 * Class to represent a row of the positions table of a league returned by the Web Service.
 */
public class LeagueTableStanding implements Serializable {
    private Integer position;
    private String teamName;
    private Integer playedGames;
    private Integer points;
    private Integer goals;
    private Integer goalsAgainst;
    private Integer goalDifference;


    public LeagueTableStanding(JSONObject json) throws JSONException {
        if (json != null) {
            position = json.getInt(JsonKeys.JSON_POSITION);
            teamName = json.getString(JsonKeys.JSON_TEAM_NAME);
            playedGames = json.getInt(JsonKeys.JSON_PLAYED_GAMES);
            points = json.getInt(JsonKeys.JSON_POINTS);
            goals = json.getInt(JsonKeys.JSON_GOALS);
            goalsAgainst = json.getInt(JsonKeys.JSON_GOALS_AGAINST);
            goalDifference = json.getInt(JsonKeys.JSON_GOALS_DIFFERENCE);
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