package android.clase.obligatorio1.entities;

import android.clase.obligatorio1.constants.JsonKeys;
import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * created by Alfredo El Ters and Mathias Cabano on 08/05/15.
 * Class to represent a team returned by the Web Service.
 */
public class Team implements Serializable {
    private String name;
    private String code;
    private String shortName;
    private String squadMarketValue;
    private Bitmap crestImage;

    //Links to get related entities from the API.
    private String crestURL;
    private String selfLink;
    private String fixturesLink;
    private String playersLink;

    private List<Player> players;

    public Team() {
        players = new ArrayList<>();
    }

    public Team(JSONObject json) throws JSONException {
        name = json.getString(JsonKeys.JSON_NAME);
        code = json.getString(JsonKeys.JSON_CODE);
        shortName = json.getString(JsonKeys.JSON_SHORT_NAME);
        squadMarketValue = json.getString(JsonKeys.JSON_CODE);
        crestURL = json.getString(JsonKeys.JSON_CREST_URL);
        squadMarketValue = json.getString(JsonKeys.JSON_SQUAD_MARKET_VALUE);
        JSONObject links = json.getJSONObject(JsonKeys.JSON_LINKS);
        selfLink = links.getJSONObject(JsonKeys.JSON_SELF_LINK).getString(JsonKeys.JSON_HREF);
        fixturesLink = links.getJSONObject(JsonKeys.JSON_FIXTURES_LINK).getString(JsonKeys.JSON_HREF);
        playersLink = links.getJSONObject(JsonKeys.JSON_PLAYERS_LINK).getString(JsonKeys.JSON_HREF);
        players = new ArrayList<>();
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

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Bitmap getCrestImage() {
        return crestImage;
    }

    public void setCrestImage(Bitmap crestImage) {
        this.crestImage = crestImage;
    }
}
