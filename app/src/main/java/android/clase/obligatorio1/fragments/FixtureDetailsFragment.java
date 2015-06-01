package android.clase.obligatorio1.fragments;


import android.clase.obligatorio1.R;
import android.clase.obligatorio1.entities.Fixture;
import android.clase.obligatorio1.entities.Match;
import android.clase.obligatorio1.entities.Team;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    private ListView mHeadToHeadListView;
    private LinearLayout mMatchDetailsLinearLayout;
    private ImageView mHomeTeamLogo;
    private ImageView mAwayTeamLogo;

    /**
     * League name obtained from the HomeActivity
     */
    private String mLeagueName;

    /**
     * Fixture received from intent.
     */
    private Fixture mFixture;


    private Team mHomeTeam;
    private Team mAwayTeam;

//    private FetchFixtureTask mFetchFixtureTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent homeScreenIntent = getActivity().getIntent();
        Bundle extras = homeScreenIntent.getExtras();
        mFixture = (Fixture) extras.getSerializable(HomeFragment.EXTRA_MATCH);
        mHomeTeam = (Team) extras.getSerializable(HomeFragment.EXTRA_HOME_TEAM);
        mAwayTeam = (Team) extras.getSerializable(HomeFragment.EXTRA_AWAY_TEAM);
        mLeagueName = extras.getString(HomeFragment.EXTRA_LEAGUE_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fixutre_details, container, false);
        //To add a header to a ListView, you must create a separated layout and inflate it too.
        View headToHeadListViewHeader = inflater.inflate(R.layout.fragment_fixture_details_header, mHeadToHeadListView, false);
        mMatchDayTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.matchDayTextView);
        mMatchDateTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.matchDateTextView);
        mMatchStatusTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.matchStatusTextView);
        mMatchStartTimeTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.matchStartTimeTextView);
        mHomeTeamLogo = (ImageView) headToHeadListViewHeader.findViewById(R.id.homeTeamImageView);
        mHomeTeamLogo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mHomeTeamTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.homeTeamTextView);
        mHomeTeamScoreTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.homeTeamScoreTextView);
        mAwayTeamTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.awayTeamTextView);
        mAwayTeamLogo = (ImageView) headToHeadListViewHeader.findViewById(R.id.awayTeamImageView);
        mAwayTeamLogo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mAwayTeamScoreTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.awayTeamScoreTextView);
        mHomeTeamNameH2H = (TextView) headToHeadListViewHeader.findViewById(R.id.home_team_name_h2h);
        mAwayTeamNameH2H = (TextView) headToHeadListViewHeader.findViewById(R.id.away_team_name_h2h);
        mHomeTeamWins = (TextView) headToHeadListViewHeader.findViewById(R.id.home_team_wins);
        mAwayTeamWins = (TextView) headToHeadListViewHeader.findViewById(R.id.away_team_wins);
        mDraws = (TextView) headToHeadListViewHeader.findViewById(R.id.draws);
        mHeadToHeadListView = (ListView) v.findViewById(R.id.head2headListView);
        //Add header to ListView, without data and making it no selectable.
        mHeadToHeadListView.addHeaderView(headToHeadListViewHeader, null, false);
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
        File homeLogo = getActivity().getFileStreamPath(HomeFragment.HOME_CREST_FILE);
        try {
            SVG homeLogoSVG = SVGParser.getSVGFromInputStream(new FileInputStream(homeLogo));
            Drawable homeLogoDrawable = homeLogoSVG.createPictureDrawable();
            mAwayTeamLogo.setImageDrawable(homeLogoDrawable != null ? homeLogoDrawable
                    : getResources().getDrawable(R.mipmap.ic_launcher));
        } catch (Exception e) {
            mAwayTeamLogo.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        }
        Integer homeTeamScore = mFixture.getGoalsHomeTeam();
        //If match status isn't finished, the API returns -1 goals for both teams.
        mHomeTeamScoreTextView.setText(homeTeamScore != -1 ? homeTeamScore.toString() : " - ");
        mAwayTeamTextView.setText(mFixture.getAwayTeam().getName());
        Integer awayTeamScore = mFixture.getGoalsAwayTeam();
        File awayLogo = getActivity().getFileStreamPath(HomeFragment.AWAY_CREST_FILE);
        try {
            SVG awayLogoSVG = SVGParser.getSVGFromInputStream(new FileInputStream(awayLogo));
            Drawable awayLogoDrawable = awayLogoSVG.createPictureDrawable();
            mAwayTeamLogo.setImageDrawable(awayLogoDrawable != null ? awayLogoDrawable
                    : getResources().getDrawable(R.mipmap.ic_launcher));
        } catch (Exception e) {
            mAwayTeamLogo.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        }
        //If match status isn't finished, the API returns -1 goals for both teams.
        mAwayTeamScoreTextView.setText(awayTeamScore != -1 ? awayTeamScore.toString() : " - ");
        mHomeTeamNameH2H.setText(mFixture.getHomeTeam().getName());
        mAwayTeamNameH2H.setText(mFixture.getAwayTeam().getName());
        mHomeTeamWins.setText(mFixture.getHead2Head().getHomeTeamWins().toString());
        mAwayTeamWins.setText(mFixture.getHead2Head().getAwayTeamWins().toString());
        mDraws.setText(mFixture.getHead2Head().getDraws().toString());
        mHeadToHeadListView.setAdapter(new MatchAdapter(mFixture.getHead2Head().getFixtures()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class MatchAdapter extends ArrayAdapter<Match> {

        private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        public MatchAdapter(List<Match> matches) {
            super(getActivity(), 0, matches);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.head_to_head_list_item, null);
            }
            Match match = getItem(position);
            TextView matchDate = (TextView) convertView.findViewById(R.id.h2h_match_date);
            matchDate.setText(dateFormat.format(match.getDate()));
            TextView homeTeamName = (TextView) convertView.findViewById(R.id.h2h_home_team);
            homeTeamName.setText(match.getHomeTeamName());
            TextView homeTeamScore = (TextView) convertView.findViewById(R.id.h2h_home_team_goals);
            homeTeamScore.setText(match.getGoalsHomeTeam().toString());
            TextView awayTeamName = (TextView) convertView.findViewById(R.id.h2h_away_team);
            awayTeamName.setText(match.getAwayTeamName());
            TextView awayTeamScore = (TextView) convertView.findViewById(R.id.h2h_away_team_goals);
            awayTeamScore.setText(match.getGoalsAwayTeam().toString());
            return convertView;
        }

    }
}
