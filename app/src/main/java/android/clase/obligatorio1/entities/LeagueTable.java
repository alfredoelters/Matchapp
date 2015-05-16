package android.clase.obligatorio1.entities;

import android.clase.obligatorio1.constants.JsonKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * created by Alfredo El Ters and Mathias Cabano on 12/05/15.
 * Class to represent the positions table of a league returned by the Web Service.
 */
public class LeagueTable implements Serializable {
    private String leagueCaption;
    private List<LeagueTableStanding> standings;

    public LeagueTable(JSONObject json) throws JSONException {
        if (json != null) {
            leagueCaption = json.getString(JsonKeys.JSON_CAPTION);
            standings = new ArrayList<>();
            JSONArray standingsJson = json.getJSONArray(JsonKeys.JSON_STANDING);
            JSONObject standing;
            for (int i = 0; i < standingsJson.length(); i++) {
                standing = standingsJson.getJSONObject(i);
                standings.add(new LeagueTableStanding(standing));
            }
        }
    }

    public String getLeagueCaption() {
        return leagueCaption;
    }

    public void setLeagueCaption(String leagueCaption) {
        this.leagueCaption = leagueCaption;
    }

    public List<LeagueTableStanding> getStandings() {
        return standings;
    }

    public void setStandings(List<LeagueTableStanding> standings) {
        this.standings = standings;
    }
}
