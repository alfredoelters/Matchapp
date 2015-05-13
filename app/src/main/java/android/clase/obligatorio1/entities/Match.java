package android.clase.obligatorio1.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple representation of a fixture
 * Created by alfredo on 12/05/15.
 */
public class Match implements Serializable {
    public static final String JSON_FIXTURE = "fixture";
    public static final String JSON_DATE = "date";
    public static final String JSON_HOME_TEAM_NAME = "homeTeamName";
    public static final String JSON_AWAY_TEAM_NAME = "awayTeamName";
    public static final String JSON_RESULT = "result";
    public static final String JSON_GOALS_HOME_TEAM = "goalsHomeTeam";
    public static final String JSON_GOALS_AWAY_TEAM = "goalsAwayTeam";
    private static final DateFormat responseDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
    private Date date;
    private String homeTeamName;
    private String awayTeamName;
    private Integer goalsHomeTeam;
    private Integer goalsAwayTeam;

    public Match(JSONObject json) throws JSONException, ParseException {
        if (json != null) {
            date = responseDateFormat.parse(json.getString(JSON_DATE));
            homeTeamName = json.getString(JSON_HOME_TEAM_NAME);
            awayTeamName = json.getString(JSON_AWAY_TEAM_NAME);
            JSONObject result = json.getJSONObject(JSON_RESULT);
            goalsHomeTeam = result.getInt(JSON_GOALS_HOME_TEAM);
            goalsAwayTeam = result.getInt(JSON_GOALS_AWAY_TEAM);
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
