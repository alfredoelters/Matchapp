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
    private Toolbar mToolbar;

    /**
     * Match Url obtained from the HomeActivity
     */
    private String mMatchUrl;
    /**
     * League name obtained from the HomeActivity
     */
    private String mLeagueName;

    /**
     * Fixture fetched  by the async task
     */
    private Fixture mFixture;

    private FetchFixtureTask mFetchFixtureTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent homeScreenIntent = getActivity().getIntent();
        mMatchUrl = homeScreenIntent.getExtras().getString(HomeFragment.EXTRA_MATCH_URL);
        mLeagueName = homeScreenIntent.getExtras().getString(HomeFragment.EXTRA_LEAGUE_NAME);
        mFetchFixtureTask = new FetchFixtureTask();
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
        mFetchFixtureTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.setTitle(mLeagueName);
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
        mHomeTeamScoreTextView.setText(mFixture.getGoalsHomeTeam().toString());
        mAwayTeamTextView.setText(mFixture.getAwayTeam().getName());
        //Need to transform the value to a string
        mAwayTeamScoreTextView.setText(mFixture.getGoalsAwayTeam().toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Finish all running async tasks
        if (mFetchFixtureTask != null && mFetchFixtureTask.getStatus() != AsyncTask.Status.FINISHED) {
            mFetchFixtureTask.cancel(true);
        }
    }

    private class FetchFixtureTask extends AsyncTask<Void, Void, Fixture> {

        @Override
        protected Fixture doInBackground(Void... params) {
            JSONObject matchJSON = WebServiceUtils.getJSONObjectFromUrl(mMatchUrl);
            Fixture result = null;
            try {
                result = new Fixture(matchJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Fixture fixture) {
            if (fixture != null) {
                mFixture = fixture;
                updateUI();
            } else {
                //TODO handle error
            }
        }
    }
}
