package android.clase.obligatorio1.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * created by Alfredo El Ters and Mathias Cabano on 10/05/15.
 * Class to represent a fixture returned by the Web Service
 */
public class Fixture implements Serializable {

    //Json keys
    public static final String JSON_FIXTURE = "fixture";
    public static final String JSON_DATE = "date";
    public static final String JSON_MATCH_DAY = "matchday";
    public static final String JSON_HOME_TEAM_NAME = "homeTeamName";
    public static final String JSON_AWAY_TEAM_NAME = "awayTeamName";
    public static final String JSON_RESULT = "result";
    public static final String JSON_GOALS_HOME_TEAM = "goalsHomeTeam";
    public static final String JSON_GOALS_AWAY_TEAM = "goalsAwayTeam";
    public static final String JSON_HEAD_TO_HEAD = "head2head";

    public static final String JSON_LINKS = "_links";
    public static final String JSON_SELF_LINK = "soccerseason";
    public static final String JSON_SOCCER_SEASON_LINK = "homeTeam";
    public static final String JSON_HOME_TEAM_LINKS = "awayTeam";

    /** Format in which the WS sends the dates for this request */
    private static final DateFormat responseDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

    private Integer goalsHomeTeam;
    private Integer goalsAwayTeam;
    private Date date;
    private Integer matchDay;

    //Links to get related entities from the API.
    private String selfLink;
    private String soccerSeasonLink;
    private String homeTeamLink;
    private String awayTeamLink;

    private Team homeTeam;
    private Team awayTeam;
    private SoccerSeason soccerSeason;
    private Head2Head head2Head;


    public Fixture(JSONObject json) throws JSONException, ParseException {
        homeTeam = new Team();
        awayTeam = new Team();
        soccerSeason = new SoccerSeason();
        if (json != null) {
            JSONObject fixture = json.getJSONObject(JSON_FIXTURE);
            homeTeam.setName(fixture.getString(JSON_HOME_TEAM_NAME));
            awayTeam.setName(fixture.getString(JSON_AWAY_TEAM_NAME));
            date = responseDateFormat.parse(fixture.getString(JSON_DATE));
            matchDay = fixture.getInt(JSON_MATCH_DAY);
            JSONObject result = fixture.getJSONObject(JSON_RESULT);
            goalsHomeTeam = result.getInt(JSON_GOALS_HOME_TEAM);
            goalsAwayTeam = result.getInt(JSON_GOALS_AWAY_TEAM);
            head2Head = new Head2Head(json.getJSONObject(JSON_HEAD_TO_HEAD));
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

    public Head2Head getHead2Head() {
        return head2Head;
    }

    public void setHead2Head(Head2Head head2Head) {
        this.head2Head = head2Head;
    }
}
