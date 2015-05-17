package android.clase.obligatorio1.fragments;

import android.app.Fragment;
import android.clase.obligatorio1.R;
import android.clase.obligatorio1.constants.JsonKeys;
import android.clase.obligatorio1.constants.PreferencesKeys;
import android.clase.obligatorio1.entities.Match;
import android.clase.obligatorio1.entities.SoccerSeason;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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
public class HomeFragment extends Fragment {
    private SharedPreferences mPreferences;

    private Spinner mLeaguesSpinner;
    private ListView mHomeListView;

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

        mLeaguesSpinner.setAdapter(new ArrayAdapter<SoccerSeason>(getActivity(),
                android.R.layout.simple_spinner_item, mLeagues) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                //Makes texts in the spinner centered
                ((TextView) v).setGravity(Gravity.CENTER);
                ((TextView) v).setTextColor(getResources().getColor(R.color.white));
                return v;
            }
        });

        // ------ Setup matches listView  -----
        mHomeListView = (ListView) v.findViewById(R.id.homeListView);
        mHomeListView.setDivider(null);
        mHomeListView.setDividerHeight(15);
        mHomeListView.setAdapter(new MatchesAdapter(mMatches));

        // ------ Start async tasks to load data into memory  -----
        //By using the executeOnExecutor method the asyncTasks run concurrently
        mLoadLeaguesTask = new LoadLeaguesTask();
        mLoadLeaguesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mLoadMatchesTask = new LoadMatchesTask();
        mLoadMatchesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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
    }

}
