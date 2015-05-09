package android.clase.obligatorio1.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alfredo on 08/05/15.
 */
public class Player implements Serializable{
    public static final String JSON_NAME = "name";
    public static final String JSON_POSITION = "position";
    public static final String JSON_JERSEY_NUMBER = "jerseyNumber";
    public static final String JSON_NATIONALITY = "nationality";
    public static final String JSON_MARKET_VALUE = "marketValue";
    public static final String JSON_DATE_OF_BIRTH = "dateOfBirth";
    public static final String JSON_CONTRACT_UNTIL= "contractUntil";

    private String name;
    private String position;
    private Integer jerseyNumber;
    private String nationality;
    private String marketValue;
    private Date dateOfBirth;
    private Date contractUntil;

    private DateFormat responseDateFormat;

    public Player(JSONObject json) throws JSONException, ParseException {
        name = json.getString(JSON_NAME);
        position = json.getString(JSON_POSITION);
        jerseyNumber = json.getInt(JSON_JERSEY_NUMBER);
        nationality = json.getString(JSON_NATIONALITY);
        marketValue = json.getString(JSON_MARKET_VALUE);
        responseDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        dateOfBirth = responseDateFormat.parse(json.getString(JSON_DATE_OF_BIRTH));
        contractUntil = responseDateFormat.parse(json.getString(JSON_CONTRACT_UNTIL));
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
