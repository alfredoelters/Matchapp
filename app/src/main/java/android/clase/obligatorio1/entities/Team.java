package android.clase.obligatorio1.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alfredo on 08/05/15.
 */
public class Team {
    public static final String JSON_NAME = "name";
    public static final String JSON_CODE = "code";
    public static final String JSON_SHORT_NAME = "shortName";
    public static final String JSON_MARKET_VALUE = "squadMarketValue";
    public static final String JSON_CREST_URL = "crestUrl";

    public static final String JSON_LINKS = "_links";
    public static final String JSON_SELF_LINK = "self";
    public static final String JSON_FIXTURES_LINK = "fixtures";
    public static final String JSON_PLAYERS_LINK = "players";

    private String name;
    private String code;
    private String shortName;
    private String squadMarketValue;
    private String crestURL;

    //Links para obtener entidades a partir del API.
    private String selfLink;
    private String fixturesLink;
    private String playersLink;


    public void Team(JSONObject json) throws JSONException{
        name = json.getString(JSON_NAME);
        code = json.getString(JSON_CODE);
        shortName = json.getString(JSON_SHORT_NAME);
        squadMarketValue = json.getString(JSON_CODE);
        crestURL = json.getString(JSON_CREST_URL);
        squadMarketValue = json.getString(JSON_MARKET_VALUE);
        JSONObject links = json.getJSONObject(JSON_LINKS);
        selfLink = links.getString(JSON_SELF_LINK);
        fixturesLink = links.getString(JSON_FIXTURES_LINK);
        playersLink = links.getString(JSON_PLAYERS_LINK);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSquadMarketValue() {
        return squadMarketValue;
    }

    public void setSquadMarketValue(String squadMarketValue) {
        this.squadMarketValue = squadMarketValue;
    }

    public String getCrestURL() {
        return crestURL;
    }

    public void setCrestURL(String crestURL) {
        this.crestURL = crestURL;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getFixturesLink() {
        return fixturesLink;
    }

    public void setFixturesLink(String fixturesLink) {
        this.fixturesLink = fixturesLink;
    }

    public String getPlayersLink() {
        return playersLink;
    }

    public void setPlayersLink(String playersLink) {
        this.playersLink = playersLink;
    }
}
