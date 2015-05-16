package android.clase.obligatorio1.entities;

import android.clase.obligatorio1.constants.JsonKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * created by Alfredo El Ters and Mathias Cabano on 12/05/15.
 * Class that consists of a simplification of a fixture, present in the head2head section returned
 * by the Web Service.
 */
public class Match implements Serializable {
    /**
     * Format in which the WS sends the dates for this request
     */
    private static final DateFormat responseDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

    private Date date;
    private String homeTeamName;
    private String awayTeamName;
    private Integer goalsHomeTeam;
    private Integer goalsAwayTeam;

    public Match(JSONObject json) throws JSONException, ParseException {
        if (json != null) {
            date = responseDateFormat.parse(json.getString(JsonKeys.JSON_DATE));
            homeTeamName = json.getString(JsonKeys.JSON_HOME_TEAM_NAME);
            awayTeamName = json.getString(JsonKeys.JSON_AWAY_TEAM_NAME);
            JSONObject result = json.getJSONObject(JsonKeys.JSON_RESULT);
            goalsHomeTeam = result.getInt(JsonKeys.JSON_GOALS_HOME_TEAM);
            goalsAwayTeam = result.getInt(JsonKeys.JSON_GOALS_AWAY_TEAM);
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public Integer getGoalsHomeTeam() {
        return goalsHomeTeam;
    }

    public void setGoalsHomeTeam(Integer goalsHomeTeam) {
        this.goalsHomeTeam = goalsHomeTeam;
    }

    public Integer getGoalsAwayTeam() {
        return goalsAwayTeam;
    }

    public void setGoalsAwayTeam(Integer goalsAwayTeam) {
        this.goalsAwayTeam = goalsAwayTeam;
    }
}
