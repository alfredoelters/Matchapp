package android.clase.obligatorio1.entities;

import android.clase.obligatorio1.constants.JsonKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * created by Alfredo El Ters and Mathias Cabano on 09/05/15.
 * Class to represent a league returned by the Web Service.
 */
public class SoccerSeason implements Serializable {
    private String caption;
    private String league;
    private String year;
    private Integer numberOfTeams;
    private Integer numberOfGames;

    private String selfLink;
    private String teamsLink;
    private String fixturesLink;
    private String leagueTableLink;


    public SoccerSeason() {

    }

    public SoccerSeason(JSONObject json) throws JSONException {
        caption = json.getString(JsonKeys.JSON_CAPTION);
        league = json.getString(JsonKeys.JSON_LEAGUE);
        year = json.getString(JsonKeys.JSON_YEAR);
        numberOfTeams = json.getInt(JsonKeys.JSON_NUMBER_OF_TEAMS);
        numberOfGames = json.getInt(JsonKeys.JSON_NUMBER_OF_GAMES);
        JSONObject links = json.getJSONObject(JsonKeys.JSON_LINKS);
        selfLink = links.getJSONObject(JsonKeys.JSON_SELF_LINK).getString(JsonKeys.JSON_HREF);
        teamsLink = links.getJSONObject(JsonKeys.JSON_TEAMS_LINK).getString(JsonKeys.JSON_HREF);
        fixturesLink = links.getJSONObject(JsonKeys.JSON_FIXTURE_LINK).getString(JsonKeys.JSON_HREF);
        leagueTableLink = links.getJSONObject(JsonKeys.JSON_LEAGUE_TABLE_LINK).getString(JsonKeys.JSON_HREF);
    }

    public String toString() {
        return caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getNumberOfTeams() {
        return numberOfTeams;
    }

    public void setNumberOfTeams(Integer numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }

    public Integer getNumberOfGames() {
        return numberOfGames;
    }

    public void setNumberOfGames(Integer numberOfGames) {
        this.numberOfGames = numberOfGames;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getTeamsLink() {
        return teamsLink;
    }

    public void setTeamsLink(String teamsLink) {
        this.teamsLink = teamsLink;
    }

    public String getFixturesLink() {
        return fixturesLink;
    }

    public void setFixturesLink(String fixturesLink) {
        this.fixturesLink = fixturesLink;
    }

    public String getLeagueTableLink() {
        return leagueTableLink;
    }

    public void setLeagueTableLink(String leagueTableLink) {
        this.leagueTableLink = leagueTableLink;
    }

}
