package android.clase.obligatorio1.utils;

import android.clase.obligatorio1.entities.Fixture;
import android.clase.obligatorio1.entities.LeagueTable;
import android.clase.obligatorio1.entities.Team;

import org.json.JSONException;

/**
 * Created by alfredo on 08/05/15.
 * Interface to mask calls to the WS, its also a Singleton
 */
public class WebServiceInterface {
    private static final String WEB_SERVICE_ENDPOINT = "http://api.football-data.org/alpha/";

    //------------SoccerSeasons WS methods------------

    //E.g. = GET http://api.football-data.org/alpha/soccerseasons/354
    private static final String GET_SOCCER_SEASON_BY_ID = WEB_SERVICE_ENDPOINT + "soccerseasons/%d";

    //----------------Teams WS methods----------------

    //E.g. = GET http://api.football-data.org/teams/19
    private static final String GET_FIXTURE_BY_ID = WEB_SERVICE_ENDPOINT + "fixtures/%d";

    //----------------Fixtures WS methods----------------

    //E.g. = GET http://api.football-data.org/alpha/fixtures/133566
    private static final String GET_TEAM_BY_ID = WEB_SERVICE_ENDPOINT + "teams/%d";

    //----------------League Table WS methods----------------

    //E.g. = GET http://api.football-data.org/alpha/soccerseasons/357/leagueTable
    private static final String GET_LEAGUE_TABLE_BY_ID = WEB_SERVICE_ENDPOINT + "soccerseasons/%d/leagueTable";

    private static WebServiceInterface INSTANCE = null;
    private JSONParser parser;

    private WebServiceInterface() {
        parser = new JSONParser();
    }

    public synchronized static WebServiceInterface getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WebServiceInterface();
        }
        return INSTANCE;
    }

    //------------SoccerSeasons methods------------


    //----------------Teams methods----------------

    public Team getTeamById(Integer id){
        if (id == null)
            return null;
        try {
            return new Team(WebServiceUtils.getJSONObjectFromUrl(String.format(GET_TEAM_BY_ID, id)));
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    //----------------Fixtures methods----------------

    public Fixture getFixtureById(Integer id){
        if (id == null)
            return null;
        try {
            return new Fixture(WebServiceUtils.getJSONObjectFromUrl(String.format(GET_FIXTURE_BY_ID, id)));
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    //---------------League Table methods-------------
    public LeagueTable getLeagueTableById(Integer id) {
        if (id == null)
            return null;
        try {
            return new LeagueTable(WebServiceUtils.getJSONObjectFromUrl(String.format(GET_LEAGUE_TABLE_BY_ID, id)));
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }
}
