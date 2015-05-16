package android.clase.obligatorio1.entities;

import android.clase.obligatorio1.constants.JsonKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * created by Alfredo El Ters and Mathias Cabano on 12/05/15.
 * Class to represent the historic matches between two teams present in the head2head
 * section returned by the Web Service
 */
public class Head2Head implements Serializable {
    private Integer awayTeamWins;
    private Integer homeTeamWins;
    private Integer draws;
    private List<Match> fixtures;

    public Head2Head(JSONObject json) throws JSONException, ParseException {
        awayTeamWins = json.getInt(JsonKeys.JSON_AWAY_WINS);
        homeTeamWins = json.getInt(JsonKeys.JSON_HOME_WINS);
        draws = json.getInt(JsonKeys.JSON_DRAWS);
        fixtures = new ArrayList<>();
        JSONArray fixturesJson = json.getJSONArray(JsonKeys.JSON_FIXTURES);
        JSONObject match;
        for (int i = 0; i < fixturesJson.length(); i++) {
            match = fixturesJson.getJSONObject(i);
            fixtures.add(new Match(match));
        }
    }

    public Integer getAwayTeamWins() {
        return awayTeamWins;
    }

    public void setAwayTeamWins(Integer awayTeamWins) {
        this.awayTeamWins = awayTeamWins;
    }

    public Integer getHomeTeamWins() {
        return homeTeamWins;
    }

    public void setHomeTeamWins(Integer homeTeamWins) {
        this.homeTeamWins = homeTeamWins;
    }

    public Integer getDraws() {
        return draws;
    }

    public void setDraws(Integer draws) {
        this.draws = draws;
    }

    public List<Match> getFixtures() {
        return fixtures;
    }

    public void setFixtures(List<Match> fixtures) {
        this.fixtures = fixtures;
    }
}
