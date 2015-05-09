package android.clase.obligatorio1.utils;

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
    private static final String GET_TEAM_BY_ID = WEB_SERVICE_ENDPOINT + "teams/%d";

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
            return new Team(WebServiceUtils.getJSONObjectFormUrl(String.format(GET_TEAM_BY_ID,id)));
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }


}
