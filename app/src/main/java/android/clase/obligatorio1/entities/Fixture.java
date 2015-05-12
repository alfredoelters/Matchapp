package android.clase.obligatorio1.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alfredo on 10/05/15.
 */
public class Fixture implements Serializable {
    public static final String JSON_DATE = "date";
    public static final String JSON_MATCH_DAY = "matchday";
    public static final String JSON_HOME_TEAM_NAME = "homeTeamName";
    public static final String JSON_AWAY_TEAM_NAME = "awayTeamName";
    public static final String JSON_RESULT = "result";
    public static final String JSON_GOALS_HOME_TEAM = "goalsHomeTeam";
    public static final String JSON_GOALS_AWAY_TEAM = "goalsAwayTeam";

    public static final String JSON_LINKS = "_links";
    public static final String JSON_SELF_LINK = "soccerseason";
    public static final String JSON_SOCCER_SEASON_LINK = "homeTeam";
    public static final String JSON_HOME_TEAM_LINKS = "awayTeam";



    private static final DateFormat responseDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

    private Team homeTeam;
    private Team awayTeam;
    private SoccerSeason soccerSeason;
    private Integer goalsHomeTeam;
    private Integer goalsAwayTeam;
    private Date date;
    private Integer matchDay;


    private String selfLink;
    private String soccerSeasonLink;
    private String homeTeamLink;
    private String awayTeamLink;

    public Fixture(JSONObject json) throws JSONException, ParseException {
        homeTeam = new Team();
        awayTeam = new Team();
        soccerSeason = new SoccerSeason();
        if (json != null) {
            goalsAwayTeam = json.getInt(JSON_GOALS_AWAY_TEAM);
            goalsHomeTeam = json.getInt(JSON_GOALS_HOME_TEAM);
            homeTeam.setName(json.getString(JSON_HOME_TEAM_NAME));
            awayTeam.setName(json.getString(JSON_AWAY_TEAM_NAME));
            date = responseDateFormat.parse(json.getString(JSON_DATE));
            matchDay = json.getInt(JSON_MATCH_DAY);
            JSONObject result = json.getJSONObject(JSON_RESULT);
            goalsHomeTeam = result.getInt(JSON_GOALS_HOME_TEAM);
            goalsAwayTeam = result.getInt(JSON_GOALS_AWAY_TEAM);

        }
    }


    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public SoccerSeason getSoccerSeason() {
        return soccerSeason;
    }

    public void setSoccerSeason(SoccerSeason soccerSeason) {
        this.soccerSeason = soccerSeason;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(Integer matchDay) {
        this.matchDay = matchDay;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getSoccerSeasonLink() {
        return soccerSeasonLink;
    }

    public void setSoccerSeasonLink(String soccerSeasonLink) {
        this.soccerSeasonLink = soccerSeasonLink;
    }

    public String getHomeTeamLink() {
        return homeTeamLink;
    }

    public void setHomeTeamLink(String homeTeamLink) {
        this.homeTeamLink = homeTeamLink;
    }

    public String getAwayTeamLink() {
        return awayTeamLink;
    }

    public void setAwayTeamLink(String awayTeamLink) {
        this.awayTeamLink = awayTeamLink;
    }
}
