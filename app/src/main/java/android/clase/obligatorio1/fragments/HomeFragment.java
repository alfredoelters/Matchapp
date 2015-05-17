package android.clase.obligatorio1.fragments;

import android.clase.obligatorio1.R;
import android.clase.obligatorio1.constants.JsonKeys;
import android.clase.obligatorio1.constants.PreferencesKeys;
import android.clase.obligatorio1.entities.Match;
import android.clase.obligatorio1.entities.SoccerSeason;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alfredo El Ters and Mathias Cabano on 15/05/15.
 */
public class HomeFragment extends Fragment implements ObservableScrollViewCallbacks {
    private SharedPreferences mPreferences;
    private View mHeaderView;
    private View mToolbarView;

    private int mBaseTranslationY;

    private Spinner mLeaguesSpinner;
    private ObservableListView mHomeListView;

    /**
     * List of all the leagues
     */
    private List<SoccerSeason> mLeagues;

    /**
     * List of all today's matches
     */
    private List<Match> mMatches;

    /**
     * Variable to store the LoadMatchesTask in case it needs to be canceled
     */
    private LoadMatchesTask mLoadMatchesTask;

    /**
     * Variable to store the LoadLeaguesTask in case it needs to be canceled
     */
    private LoadLeaguesTask mLoadLeaguesTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mLeagues = new ArrayList<>();
        mMatches = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // ------ Setup leagues Spinner  -----
        mLeaguesSpinner = (Spinner) v.findViewById(R.id.leaguesSpinner);

        mLeaguesSpinner.setAdapter(new LeaguesAdapter(mLeagues));

        // ------ Setup matches listView  -----
        mHomeListView = (ObservableListView) v.findViewById(R.id.homeListView);
        mHomeListView.setDivider(null);
        mHomeListView.setDividerHeight(15);
        mHomeListView.setAdapter(new MatchesAdapter(mMatches));
        mHomeListView.setScrollViewCallbacks(this);

        // ------ Start async tasks to load data into memory  -----
        //By using the executeOnExecutor method the asyncTasks run concurrently
        mLoadLeaguesTask = new LoadLeaguesTask();
        mLoadLeaguesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mLoadMatchesTask = new LoadMatchesTask();
        mLoadMatchesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mHeaderView = v.findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) v.findViewById(R.id.toolbar));
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadLeaguesTask != null && mLoadLeaguesTask.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadLeaguesTask.cancel(true);
        }
        if (mLoadMatchesTask != null && mLoadMatchesTask.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadMatchesTask.cancel(true);
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            if (firstScroll) {
                float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
            com.nineoldandroids.view.ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            int toolbarHeight = mToolbarView.getHeight();
            int scrollY = mHomeListView.getCurrentScrollY();
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else {
            // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
            if (!toolbarIsShown() && !toolbarIsHidden()) {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideToolbar()
                showToolbar();
            }
        }
    }
    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            com.nineoldandroids.view.ViewPropertyAnimator.animate(mHeaderView).cancel();
            com.nineoldandroids.view.ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            com.nineoldandroids.view.ViewPropertyAnimator.animate(mHeaderView).cancel();
            com.nineoldandroids.view.ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
        }
    }

    /**
     * Async task to load leagues fetches by the splash screen into memory
     */
    private class LoadLeaguesTask extends AsyncTask<Void, Void, List<SoccerSeason>> {
        @Override
        protected List<SoccerSeason> doInBackground(Void... params) {
            String leaguesJson = mPreferences.getString(PreferencesKeys.PREFS_LEAGUES, null);
            List<SoccerSeason> result = new ArrayList<>();
            if (leaguesJson != null) {
                try {
                    JSONArray leagues = new JSONArray(leaguesJson);
                    JSONObject league;
                    for (int i = 0; i < leagues.length(); i++) {
                        league = leagues.getJSONObject(i);
                        result.add(new SoccerSeason(league));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<SoccerSeason> soccerSeasons) {
            super.onPostExecute(soccerSeasons);
            mLeagues.addAll(soccerSeasons);
            ((ArrayAdapter<SoccerSeason>) mLeaguesSpinner.getAdapter()).notifyDataSetChanged();
        }
    }

    /**
     * Async task to load matches fetches by the splash screen into memory
     */
    private class LoadMatchesTask extends AsyncTask<Void, Void, List<Match>> {

        @Override
        protected List<Match> doInBackground(Void... params) {
            String matchesJson = mPreferences.getString(PreferencesKeys.PREFS_HOME_MATCHES, null);
            List<Match> result = new ArrayList<>();
            if (matchesJson != null) {
                try {
                    JSONArray matches = new JSONObject(matchesJson)
                            .getJSONArray(JsonKeys.JSON_FIXTURES);
                    JSONObject match;
                    for (int i = 0; i < matches.length(); i++) {
                        match = matches.getJSONObject(i);
                        result.add(new Match(match));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<Match> matches) {
            super.onPostExecute(matches);
            mMatches.addAll(matches);
            ((MatchesAdapter) mHomeListView.getAdapter()).notifyDataSetChanged();
        }
    }

    private class LeaguesAdapter extends ArrayAdapter<SoccerSeason>{

        public LeaguesAdapter( List<SoccerSeason> leagues) {
            super(getActivity(),0,leagues);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.league_spinner_item, null);
            }
            TextView leagueNameTextView = (TextView) convertView.findViewById(R.id.leagueName);
            SoccerSeason league = getItem(position);
            leagueNameTextView.setText(league.getCaption());
            return convertView;
        }
    }

    /**
     * Array adapter to show today's matches
     */
    private class MatchesAdapter extends ArrayAdapter<Match> {

        private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        private final DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        private String lastSeasonName;

        public MatchesAdapter(List<Match> matches) {
            super(getActivity(), 0, matches);
            lastSeasonName = "";
        }


        private View getCustomView(int position, View convertView){
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.home_match_list_item, null);
            }
            TextView separatorTextView = (TextView) convertView.findViewById(R.id.separator);
            TextView dateTextView = (TextView) convertView.findViewById(R.id.matchDateTextView);
            TextView timeTextView = (TextView) convertView.findViewById(R.id.matchTimeTextView);
            TextView awayTeamTextView = (TextView) convertView.findViewById(R.id.matchAwayTeamTextView);
            TextView homeTeamTextView = (TextView) convertView.findViewById(R.id.matchHomeTeamTextView);

            Match match = getItem(position);
            Date date = match.getDate();
            dateTextView.setText(dateFormat.format(date));
            timeTextView.setText(timeFormat.format(date));
            awayTeamTextView.setText(match.getAwayTeamName());
            homeTeamTextView.setText(match.getHomeTeamName());
            return convertView;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position,convertView);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position,convertView);
        }
    }

}
