package android.clase.obligatorio1.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by alfredo on 09/05/15.
 */
public class SoccerSeason implements Serializable {
    public static final String JSON_CAPTION = "caption";
    public static final String JSON_LEAGUE = "league";
    public static final String JSON_YEAR = "year";
    public static final String JSON_NUMBER_OF_TEAMS = "numberOfTeams";
    public static final String JSON_NUMBER_OF_GAMES = "numberOfGames";
    public static final String JSON_LINKS = "_links";
    public static final String JSON_SELF_LINK = "self";
    public static final String JSON_TEAMS_LINK = "teams";
    public static final String JSON_FIXTURE_LINK = "fixtures";
    public static final String JSON_LEAGUE_TABLE_LINK = "leagueTable";


    private String caption;
    private String league;
    private String year;
    private Integer numberOfTeams;
    private Integer numberOfGames;

    private String selfLink;
    private String teamsLink;
    private String fixturesLink;
    private String leagueTableLink;

    public SoccerSeason(){

    }

    public SoccerSeason(JSONObject json) throws JSONException {
        caption = json.getString(JSON_CAPTION);
        league = json.getString(JSON_LEAGUE);
        year = json.getString(JSON_YEAR);
        numberOfTeams = json.getInt(JSON_NUMBER_OF_TEAMS);
        numberOfGames = json.getInt(JSON_NUMBER_OF_GAMES);
        JSONObject links = json.getJSONObject(JSON_LINKS);
        selfLink = links.getString(JSON_SELF_LINK);
        teamsLink = links.getString(JSON_TEAMS_LINK);
        fixturesLink = links.getString(JSON_FIXTURE_LINK);
        leagueTableLink = links.getString(JSON_LEAGUE_TABLE_LINK);
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
