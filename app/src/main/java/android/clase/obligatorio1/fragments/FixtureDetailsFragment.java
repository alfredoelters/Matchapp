package android.clase.obligatorio1.fragments;


import android.clase.obligatorio1.R;
import android.clase.obligatorio1.entities.Fixture;
import android.clase.obligatorio1.utils.WebServiceUtils;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alfredo on 17/05/15.
 */
public class FixtureDetailsFragment extends Fragment {
    private static final DateFormat MATCH_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat MATCH_TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    //UI components
    private TextView mMatchDayTextView;
    private TextView mMatchDateTextView;
    private TextView mMatchStatusTextView;
    private TextView mMatchStartTimeTextView;
    private TextView mHomeTeamTextView;
    private TextView mHomeTeamScoreTextView;
    private TextView mAwayTeamTextView;
    private TextView mAwayTeamScoreTextView;
    private TextView mHomeTeamNameH2H;
    private TextView mAwayTeamNameH2H;
    private TextView mHomeTeamWins;
    private TextView mAwayTeamWins;
    private TextView mDraws;
    private Toolbar mToolbar;

    /**
     * League name obtained from the HomeActivity
     */
    private String mLeagueName;

    /**
     * Fixture received from intent.
     */
    private Fixture mFixture;

//    private FetchFixtureTask mFetchFixtureTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent homeScreenIntent = getActivity().getIntent();
        mFixture = (Fixture) homeScreenIntent.getExtras().getSerializable(HomeFragment.EXTRA_MATCH);
        mLeagueName = homeScreenIntent.getExtras().getString(HomeFragment.EXTRA_LEAGUE_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fixutre_details, container, false);
        mMatchDayTextView = (TextView) v.findViewById(R.id.matchDayTextView);
        mMatchDateTextView = (TextView) v.findViewById(R.id.matchDateTextView);
        mMatchStatusTextView = (TextView) v.findViewById(R.id.matchStatusTextView);
        mMatchStartTimeTextView = (TextView) v.findViewById(R.id.matchStartTimeTextView);
        mHomeTeamTextView = (TextView) v.findViewById(R.id.homeTeamTextView);
        mHomeTeamScoreTextView = (TextView) v.findViewById(R.id.homeTeamScoreTextView);
        mAwayTeamTextView = (TextView) v.findViewById(R.id.awayTeamTextView);
        mAwayTeamScoreTextView = (TextView) v.findViewById(R.id.awayTeamScoreTextView);
        mHomeTeamNameH2H = (TextView) v.findViewById(R.id.home_team_name_h2h);
        mAwayTeamNameH2H = (TextView) v.findViewById(R.id.away_team_name_h2h);
        mHomeTeamWins = (TextView) v.findViewById(R.id.home_team_wins);
        mAwayTeamWins = (TextView) v.findViewById(R.id.away_team_wins);
        mDraws = (TextView) v.findViewById(R.id.draws);
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.setTitle(mLeagueName);
        updateUI();
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return v;
    }

    /**
     * Method to update the UI based on the fetches Fixture
     */
    private void updateUI() {
        mMatchDayTextView.setText(getString(R.string.matchDay) + mFixture.getMatchDay());
        Date matchDate = mFixture.getDate();
        mMatchDateTextView.setText(MATCH_DATE_FORMAT.format(matchDate));
        mMatchStartTimeTextView.setText(getString(R.string.startTime) + MATCH_TIME_FORMAT.format(matchDate));
        mHomeTeamTextView.setText(mFixture.getHomeTeam().getName());
        //Need to transform the value to a string
        Integer homeTeamScore = mFixture.getGoalsHomeTeam();
        mHomeTeamScoreTextView.setText(homeTeamScore != -1 ? homeTeamScore.toString() : " - ");
        mAwayTeamTextView.setText(mFixture.getAwayTeam().getName());
        //Need to transform the value to a string
        Integer awayTeamScore = mFixture.getGoalsAwayTeam();
        mAwayTeamScoreTextView.setText(awayTeamScore != -1 ? awayTeamScore.toString() : " - ");
        mHomeTeamNameH2H.setText(mFixture.getHomeTeam().getName());
        mAwayTeamNameH2H.setText(mFixture.getAwayTeam().getName());
        mHomeTeamWins.setText(mFixture.getHead2Head().getHomeTeamWins().toString());
        mAwayTeamWins.setText(mFixture.getHead2Head().getAwayTeamWins().toString());
        mDraws.setText(mFixture.getHead2Head().getDraws().toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
