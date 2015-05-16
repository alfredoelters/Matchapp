package android.clase.obligatorio1.utils;

import android.clase.obligatorio1.constants.JsonKeys;
import android.clase.obligatorio1.constants.WebServiceURLs;
import android.clase.obligatorio1.entities.Fixture;
import android.clase.obligatorio1.entities.LeagueTable;
import android.clase.obligatorio1.entities.Match;
import android.clase.obligatorio1.entities.SoccerSeason;
import android.clase.obligatorio1.entities.Team;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * created by Alfredo El Ters and Mathias Cabano on 08/05/15.
 * Interface to mask calls to the WS, its also a Singleton
 */
public class WebServiceInterface {
    private static final DateFormat RESPONSE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static WebServiceInterface INSTANCE = null;

    private WebServiceInterface() {

    }

    public synchronized static WebServiceInterface getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WebServiceInterface();
        }
        return INSTANCE;
    }

    //------------SoccerSeasons methods------------
    public List<SoccerSeason> getSoccerSeasons(){
        JSONArray response = WebServiceUtils.getJSONArrayFromUrl(WebServiceURLs.GET_ALL_SOCCER_SEASONS);
        return null;
    }

    //----------------Teams methods----------------

    /**
     * Method to get one team by its id
     *
     * @param id
     * @return team
     */
    public Team getTeamById(Integer id) {
        if (id == null)
            return null;
        try {
            return new Team(WebServiceUtils.getJSONObjectFromUrl(String.format(WebServiceURLs.GET_TEAM_BY_ID, id)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //----------------Fixtures methods----------------

    /**
     * Method to get one fixture by its id
     *
     * @param id
     * @return fixture
     */
    public Fixture getFixtureById(Integer id) {
        if (id == null)
            return null;
        try {
            return new Fixture(WebServiceUtils.getJSONObjectFromUrl(String.format(WebServiceURLs.GET_FIXTURE_BY_ID, id)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to get all fixtures played on particular date. If the parameter
     * soccerSeasonId is specified, it filters the results to that particular soccer season
     *
     * @param date
     * @param soccerSeasonId
     * @return List of selected fixtures
     */
    public List<Match> getFixturesForDate(Date date, Integer soccerSeasonId) {
        String request;
        String dateString = RESPONSE_DATE_FORMAT.format(date);
        if (soccerSeasonId == null) {
            request = String.format(WebServiceURLs.GET_FIXTURES_OF_DATE_FOR_ALL_LEAGUES, dateString, dateString);
        } else {
            request = String.format(WebServiceURLs.GET_FIXTURES_OF_DATE_FOR_LEAGUE, soccerSeasonId,
                    dateString, dateString);
        }
        JSONObject response = WebServiceUtils.getJSONObjectFromUrl(request);
        List<Match> result;
        try {
            JSONArray fixturesJson = response.getJSONArray(JsonKeys.JSON_FIXTURES);
            result = new ArrayList<>();
            JSONObject fixture;
            for (int i = 0; i < fixturesJson.length(); i++) {
                fixture = fixturesJson.getJSONObject(i);
                result.add(new Match(fixture));
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    //---------------League Table methods-------------

    /**
     * Method to get a league table by its id
     *
     * @param id
     * @return league table
     */
    public LeagueTable getLeagueTableById(Integer id) {
        if (id == null)
            return null;
        try {
            return new LeagueTable(WebServiceUtils.getJSONObjectFromUrl(
                    String.format(WebServiceURLs.GET_LEAGUE_TABLE_BY_ID, id)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
