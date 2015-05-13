package android.clase.obligatorio1.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfredo on 12/05/15.
 */
public class LeagueTable implements Serializable{
    public static final String JSON_CAPTION = "leagueCaption";
    public static final String JSON_STANDING = "standing";

    public static final String JSON_LINK = "_links";
    public static final String JSON_SELF_LINK = "self";
    public static final String JSON_SOCCER_SEASON_LINK = "soccerseason";

    private String leagueCaption;
    private List<LeagueTableStanding> standings;

    public LeagueTable(JSONObject json) throws JSONException {
        if(json != null) {
            leagueCaption = json.getString(JSON_CAPTION);
            standings = new ArrayList<>();
            JSONArray standingsJson = json.getJSONArray(JSON_STANDING);
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
