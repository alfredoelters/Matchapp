package android.clase.obligatorio1.entities;

import android.clase.obligatorio1.utils.JsonKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * created by Alfredo El Ters and Mathias Cabano on 08/05/15.
 * Class to represent a player of a team returned by the Web Service.
 */
public class Player implements Serializable{
    private String name;
    private String position;
    private Integer jerseyNumber;
    private String nationality;
    private String marketValue;
    private Date dateOfBirth;
    private Date contractUntil;

    private DateFormat responseDateFormat;

    public Player(JSONObject json) throws JSONException, ParseException {
        name = json.getString(JsonKeys.JSON_NAME);
        position = json.getString(JsonKeys.JSON_POSITION);
        jerseyNumber = json.getInt(JsonKeys.JSON_JERSEY_NUMBER);
        nationality = json.getString(JsonKeys.JSON_NATIONALITY);
        marketValue = json.getString(JsonKeys.JSON_MARKET_VALUE);
        responseDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        dateOfBirth = responseDateFormat.parse(json.getString(JsonKeys.JSON_DATE_OF_BIRTH));
        contractUntil = responseDateFormat.parse(json.getString(JsonKeys.JSON_CONTRACT_UNTIL));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(Integer jerseryNumber) {
        this.jerseyNumber = jerseryNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getContractUntil() {
        return contractUntil;
    }

    public void setContractUntil(Date contractUntil) {
        this.contractUntil = contractUntil;
    }
}
