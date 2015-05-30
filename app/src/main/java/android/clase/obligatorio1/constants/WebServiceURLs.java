package android.clase.obligatorio1.constants;

/**
 * Created by alfredo on 15/05/15.
 */
public class WebServiceURLs {

    private static final String WEB_SERVICE_ENDPOINT = "http://api.football-data.org/alpha/";

    //------------SoccerSeasons WS methods------------

    //------------SoccerSeasons WS methods------------
    //E.g. = GET http://api.football-data.org/alpha/soccerseasons
    public static final String GET_ALL_SOCCER_SEASONS = WEB_SERVICE_ENDPOINT + "soccerseasons";


    //E.g. = GET http://api.football-data.org/alpha/soccerseasons/354
    public static final String GET_SOCCER_SEASON_BY_ID = WEB_SERVICE_ENDPOINT + "soccerseasons/%d";

    //----------------Teams WS methods----------------

    //E.g. = GET http://api.football-data.org/teams/19
    public static final String GET_TEAM_BY_ID = WEB_SERVICE_ENDPOINT + "teams/%d";

    //E.g. = GET http://api.football-data.org/teams/19/players
    public static final String INCOMPLETE_GET_TEAM_PLAYERS = "/players";


    //----------------Fixtures WS methods----------------

    //E.g. = GET http://api.football-data.org/alpha/fixtures/133566
    public static final String GET_FIXTURE_BY_ID = WEB_SERVICE_ENDPOINT + "fixtures/%d";

    //E.g. = GET http://api.football­data.org/alpha/fixtures/?timeFrameStart=2015­04­18&timeFrameEnd=2015­04­18
    public static final String GET_FIXTURES_OF_DATE_FOR_ALL_LEAGUES = WEB_SERVICE_ENDPOINT +
            "fixtures/?timeFrameStart=%s&timeFrameEnd=%s";

    //E.g. = GET http://api.football­data.org/alpha/fixtures/?timeFrameStart=2015­04­18&timeFrameEnd=2015­04­18
    public static final String GET_FIXTURES_OF_DATE_FOR_LEAGUE = WEB_SERVICE_ENDPOINT +
            "soccerseasons/%d/fixtures/?timeFrameStart=%s&timeFrameEnd=%s";

    //This link needs to be added to the soccerseason link
    // //E.g. = GET http://api.football­data.org/alpha/fixtures/?timeFrameStart=2015­04­18&timeFrameEnd=2015­04­18
    public  static final String INCOMPLETE_GET_FIXTURES_OF_DATE_FOR_LEAGUE =
            "/fixtures/?timeFrameStart=%s&timeFrameEnd=%s";

    //----------------League Table WS methods----------------

    //E.g. = GET http://api.football-data.org/alpha/soccerseasons/357/leagueTable
    public static final String GET_LEAGUE_TABLE_BY_ID = WEB_SERVICE_ENDPOINT + "soccerseasons/%d/leagueTable";
}
