package android.clase.obligatorio1.fragments;

import android.app.ProgressDialog;
import android.clase.obligatorio1.R;
import android.clase.obligatorio1.activities.FixtureDetailsActivity;
import android.clase.obligatorio1.activities.LeagueTableActivity;
import android.clase.obligatorio1.constants.JsonKeys;
import android.clase.obligatorio1.constants.PreferencesKeys;
import android.clase.obligatorio1.entities.Fixture;
import android.clase.obligatorio1.entities.LeagueTable;
import android.clase.obligatorio1.entities.Match;
import android.clase.obligatorio1.entities.SoccerSeason;
import android.clase.obligatorio1.entities.Team;
import android.clase.obligatorio1.utils.WebServiceUtils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HeaderViewListAdapter;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alfredo El Ters and Mathias Cabano on 15/05/15.
 */
public class HomeFragment extends Fragment implements ObservableScrollViewCallbacks {
    //Extra keys
    public static final String EXTRA_MATCH = "match";
    public static final String EXTRA_LEAGUE_NAME = "leagueName";
    public static final String EXTRA_HOME_TEAM_CREST = "homeTeamCrest";
    public static final String EXTRA_AWAY_TEAM_CREST = "awayTeamCrest";
    public static final String EXTRA_HOME_TEAM = "homeTeam";
    public static final String EXTRA_AWAY_TEAM = "awayTeam";

    public static final String EXTRA_LEAGUE_TABLE = "leagueTable";

    //UI components
    private Spinner mLeaguesSpinner;
    private ObservableListView mHomeListView;
    private View mHeaderView;
    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;

    /**
     * int to specify the translation of the toolbar based on the scrolling
     */
    private int mBaseTranslationY;

    private SharedPreferences mPreferences;


    /**
     * List of all the leagues
     */
    private List<SoccerSeason> mLeagues;

    /**
     * List of filtered matches
     */
    private List<Match> mMatches;

    /**
     * List of today's matches
     */
    private List<Match> mAllMatches;

    /**
     * HashMap to set match's league caption
     */
    private HashMap<String, String> mLeagueCaptions;

    /**
     * Variable to store the LoadMatchesTask in case it needs to be canceled
     */
//    private List<FetchMatchesTask> mFetchMatchesTasks;

    /**
     * Variable to store async tasks in case they needs to be canceled
     */
    private LoadLeaguesTask mLoadLeaguesTask;

    private LoadMatchesTask mLoadMatchesTask;

    private FetchFixtureTask mFetchFixtureTask;

    private FetchLeagueTableTask mFetchLeagueTableTask;

    private DownloadCrestImageTask mHomeTeamCrestTask;
    private DownloadCrestImageTask mAwayTeamCrestTask;

    private FetchTeamDetailsTask mFetchHomeTeamTask;
    private FetchTeamDetailsTask mFetchAwayTeamTask;

    /**
     * Selected match
     */
    private Match mMatch;
    /**
     * Fetched fixture information
     */
    private Fixture mFetchedFixture;
    private Team mHomeTeam;
    private Team mAwayTeam;
    private Bitmap mHomeTeamCrest;
    private Bitmap mAwayTeamCrest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mLeagues = new ArrayList<>();
        mMatches = new ArrayList<>();
        mAllMatches = new ArrayList<>();
        mLeagueCaptions = new HashMap<>();
//        mFetchMatchesTasks = new ArrayList<>();
        mLoadLeaguesTask = new LoadLeaguesTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);


        // ------ Setup matches listView  -----
        mHomeListView = (ObservableListView) v.findViewById(R.id.homeListView);
        mHomeListView.setDivider(null);
        mHomeListView.setDividerHeight(1);
        // add toolbar padding
        mHomeListView.addHeaderView(inflater.inflate(R.layout.padding, mHomeListView, false));
        // add toolbar padding
        mHomeListView.addHeaderView(inflater.inflate(R.layout.padding, mHomeListView, false));
        mHomeListView.setScrollViewCallbacks(this);

        // ------ Setup leagues Spinner  -----
        mLeaguesSpinner = (Spinner) v.findViewById(R.id.leaguesSpinner);

        mLeaguesSpinner.setAdapter(new LeaguesAdapter(mLeagues));

        //Set listener to filter leagues results
        mLeaguesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mHomeListView.getAdapter() != null) {
                    MatchesAdapter adapter = (MatchesAdapter)
                            ((HeaderViewListAdapter) mHomeListView.getAdapter()).getWrappedAdapter();
                    //Filter without a query to only take into account the spinner filter.
                    adapter.getFilter().filter(null);
                    //Redraw options menu to hide the viewLeagueTableAction
                    // in case "All leagues" is selected
                    getActivity().invalidateOptionsMenu();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // ------ Start async tasks to load data into memory  -----
        //By using the executeOnExecutor method the asyncTasks run concurrently
        mLoadLeaguesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // ------ Setup  Toolbar  -----
        mHeaderView = v.findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);
        //Prepare searchView to query TeamNames
        SearchView searchView = (SearchView) menu.findItem(R.id.searchTeamAction).getActionView();
        if (searchView != null) {
            final Menu menu_block = menu;
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    menu_block.findItem(R.id.searchTeamAction).collapseActionView();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ((MatchesAdapter) ((HeaderViewListAdapter) mHomeListView.getAdapter())
                            .getWrappedAdapter()).getFilter().filter(newText);
                    return false;
                }
            });
        }
        menu.getItem(1).setVisible(false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //Hide the viewLeagueAction in case "All leagues" is selected
        if (mLeaguesSpinner.getSelectedItemPosition() != 0)
            menu.getItem(1).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewLeagueTableAction:
                //Start async task to fetch the league
                mFetchLeagueTableTask = new FetchLeagueTableTask();
                mFetchLeagueTableTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        ((SoccerSeason) mLeaguesSpinner.getSelectedItem()).getLeagueTableLink());
                return true;
            case R.id.searchTeamAction:
                getActivity().onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //    public void updateMatchesFilter() {
//        String query = mPreferences.getString(PreferencesKeys.PREFS_SEARCH_QUERY, null);
//        if (query != null) {
//            //Take into account both the league filter and the search filter
//            int selectedLeaguePosition = mLeaguesSpinner.getSelectedItemPosition();
//            for (Match match : mAllMatches) {
//                if (selectedLeaguePosition == 0 || match.getLeagueCaption().
//                        equals(((SoccerSeason) mLeaguesSpinner.getSelectedItem()).getCaption())) {
//                    if (match.getHomeTeamName().contains(query) || match.getAwayTeamName().contains(query)) {
//                        mMatches.add(match);
//                    }
//                }
//            }
//            ((MatchesAdapter) ((HeaderViewListAdapter) mHomeListView.getAdapter())
//                    .getWrappedAdapter()).notifyDataSetChanged();
//        }
//    }

    public void selectFavoriteLeague() {
        String favoriteLeagueCaption = mPreferences.getString(PreferencesKeys.PREFS_FAVORITE_LEAGUE,
                null);
        if (favoriteLeagueCaption != null) {
            for (int i = 0; i < mLeagues.size(); i++) {
                if (favoriteLeagueCaption.equals(mLeagues.get(i).getCaption())) {
                    mLeaguesSpinner.setSelection(i);
                    break;
                }
            }
        } else {
            mLeaguesSpinner.setSelection(0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Finish all running async tasks
        if (mLoadLeaguesTask != null && mLoadLeaguesTask.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadLeaguesTask.cancel(true);
        }
        if (mLoadMatchesTask != null && mLoadMatchesTask.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadMatchesTask.cancel(true);
        }
        if (mFetchFixtureTask != null && mFetchFixtureTask.getStatus() != AsyncTask.Status.FINISHED) {
            mFetchFixtureTask.cancel(true);
        }
        if (mFetchLeagueTableTask != null && mFetchLeagueTableTask.getStatus() != AsyncTask.Status.FINISHED) {
            mFetchLeagueTableTask.cancel(true);
        }
        if (mHomeTeamCrestTask != null && mHomeTeamCrestTask.getStatus() != AsyncTask.Status.FINISHED) {
            mHomeTeamCrestTask.cancel(true);
        }
        if (mAwayTeamCrestTask != null && mAwayTeamCrestTask.getStatus() != AsyncTask.Status.FINISHED) {
            mAwayTeamCrestTask.cancel(true);
        }
        if (mFetchHomeTeamTask != null && mFetchHomeTeamTask.getStatus() != AsyncTask.Status.FINISHED) {
            mFetchHomeTeamTask.cancel(true);
        }
        if (mFetchAwayTeamTask != null && mFetchAwayTeamTask.getStatus() != AsyncTask.Status.FINISHED) {
            mFetchAwayTeamTask.cancel(true);
        }
//        for (FetchMatchesTask task : mFetchMatchesTasks) {
//            if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
//                task.cancel(true);
//            }
//        }
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = mToolbar.getHeight();
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
            int toolbarHeight = mToolbar.getHeight();
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
        return ViewHelper.getTranslationY(mHeaderView) == -mToolbar.getHeight();
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
        int toolbarHeight = mToolbar.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            com.nineoldandroids.view.ViewPropertyAnimator.animate(mHeaderView).cancel();
            com.nineoldandroids.view.ViewPropertyAnimator.animate(mHeaderView)
                    .translationY(-toolbarHeight).setDuration(200).start();
        }
    }
//
//    private void startFetchMatchesTasks() {
//        FetchMatchesTask task;
//        SoccerSeason league;
//        for (int i = 1; i < mLeagues.size(); i++) {
//            task = new FetchMatchesTask();
//            league =mLeagues.get(i);
//            mFetchMatchesTasks.add(task);
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
//                   league.getSelfLink(),league.getCaption());
//        }
//    }

    /**
     * Method to call FixtureDetailsActivity in case all required data has been fetched
     */
    public void tryCallFixtureDetailsActivity() {
        if (mFetchedFixture != null && mHomeTeamCrest != null && mAwayTeamCrest != null
                && mHomeTeam != null && mAwayTeam != null) {
        mProgressDialog.dismiss();
        //When finished fetching fixture info, start fixture detail activity.
        Intent callFixtureDetailsActivity = new Intent(getActivity(),
                FixtureDetailsActivity.class);
        callFixtureDetailsActivity.putExtra(EXTRA_MATCH, mFetchedFixture);
        callFixtureDetailsActivity.putExtra(EXTRA_LEAGUE_NAME, mMatch.getLeagueCaption());
        callFixtureDetailsActivity.putExtra(EXTRA_HOME_TEAM_CREST, mHomeTeamCrest);
        callFixtureDetailsActivity.putExtra(EXTRA_AWAY_TEAM_CREST, mAwayTeamCrest);
        callFixtureDetailsActivity.putExtra(EXTRA_HOME_TEAM, mHomeTeam);
        callFixtureDetailsActivity.putExtra(EXTRA_AWAY_TEAM, mAwayTeam);
        startActivity(callFixtureDetailsActivity);
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
            Collections.sort(mLeagues);
            ((ArrayAdapter<SoccerSeason>) mLeaguesSpinner.getAdapter()).notifyDataSetChanged();
            for (SoccerSeason league : mLeagues) {
                mLeagueCaptions.put(league.getSelfLink(), league.getCaption());
            }
            mLoadMatchesTask = new LoadMatchesTask();
            mLoadMatchesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //Dummy league to show all leagues in the filter
            SoccerSeason dummyLeague = new SoccerSeason();
            dummyLeague.setCaption(getString(R.string.all_leagues));
            mLeagues.add(0, dummyLeague);
        }
    }

//    /**
//     * Async task to load matches fetches by the splash screen into memory
//     */
//    private class FetchMatchesTask extends AsyncTask<String, Void, List<Match>> {
//        private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        @Override
//        protected List<Match> doInBackground(String... params) {
//            String soccerSeasonLink = params[0];
//            String soccerSeasonCaption = params[1];
//            //Fixed date for debug purposes
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.DAY_OF_MONTH, 17);
//            calendar.set(Calendar.MONTH, 4);
//            calendar.set(Calendar.YEAR, 2015);
//            String dateFormatted = dateFormat.format(calendar.getTime());
//            String matchesJson = WebServiceUtils.getJSONStringFromUrl(soccerSeasonLink
//                    + String.format(WebServiceURLs.INCOMPLETE_GET_FIXTURES_OF_DATE_FOR_LEAGUE,
//                    dateFormatted, dateFormatted));
//            List<Match> result = new ArrayList<>();
//            if (matchesJson != null) {
//                try {
//                    JSONArray matches = new JSONObject(matchesJson)
//                            .getJSONArray(JsonKeys.JSON_FIXTURES);
//                    JSONObject matchJson;
//                    Match match;
//                    for (int i = 0; i < matches.length(); i++) {
//                        matchJson = matches.getJSONObject(i);
//                        match = new Match(matchJson);
//                        //Set the league caption to show in the header if necessary
//                        match.setLeagueCaption(soccerSeasonCaption);
//                        result.add(match);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(List<Match> matches) {
//            super.onPostExecute(matches);
//            mMatches.addAll(matches);
//            ((MatchesAdapter) ((HeaderViewListAdapter) mHomeListView.getAdapter())
//                    .getWrappedAdapter()).notifyDataSetChanged();
//        }
//    }

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
                    JSONArray matchesJSON = new JSONObject(matchesJson).getJSONArray(JsonKeys.JSON_FIXTURES);
                    JSONObject matchJSON;
                    Match match;
                    for (int i = 0; i < matchesJSON.length(); i++) {
                        matchJSON = matchesJSON.getJSONObject(i);
                        match = new Match(matchJSON);
                        match.setLeagueCaption(mLeagueCaptions.get(match.getSoccerSeasonLink()));
                        result.add(match);
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<Match> matches) {
            super.onPostExecute(matches);
            mAllMatches = matches;
            Collections.sort(mAllMatches);
            mMatches.addAll(mAllMatches);
            mHomeListView.setAdapter(new MatchesAdapter(mMatches));
            //If there is a favorite league, set the selection as this one.
            selectFavoriteLeague();
        }
    }

    private class LeaguesAdapter extends ArrayAdapter<SoccerSeason> {

        public LeaguesAdapter(List<SoccerSeason> leagues) {
            super(getActivity(), 0, leagues);
        }

        private View getCustomView(int position, View convertView) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.league_spinner_item, null);
            }
            TextView leagueNameTextView = (TextView) convertView.findViewById(R.id.leagueName);
            SoccerSeason league = getItem(position);
            leagueNameTextView.setText(league.getCaption());
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView);
        }
    }

    /**
     * Array adapter to show today's matches
     */
    private class MatchesAdapter extends ArrayAdapter<Match> implements Filterable {

        // State of the row that needs to show separator
        private static final int SECTIONED_STATE = 1;
        // State of the row that doesn't need to separator
        private static final int REGULAR_STATE = 2;
        private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        private final DateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        // Cache row states based on positions (Section or regular)
        private int[] rowStates;

        //Auxiliary string to determine when to render the header.
        private String lastLeagueCaption;

        public MatchesAdapter(List<Match> matches) {
            super(getActivity(), 0, matches);
            lastLeagueCaption = "";
            rowStates = new int[matches.size()];
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.home_match_list_item, null);
            }
            final Match match = getItem(position);
            TextView leagueTittle = (TextView) convertView.findViewById(R.id.leagueTittle);
            View separatorView = convertView.findViewById(R.id.separator);
            String currentLeagueCaption = match.getLeagueCaption();
            //Determine if it is necessary to show the league title or the separator
            boolean showLeagueTittle;
            switch (rowStates[position]) {
                case SECTIONED_STATE:
                    showLeagueTittle = true;
                    break;
                case REGULAR_STATE:
                    showLeagueTittle = false;
                    break;
                default:
                    if (position == 0) {
                        showLeagueTittle = true;
                    } else {
                        showLeagueTittle = !lastLeagueCaption.equals(currentLeagueCaption);
                    }
                    // Cache it
                    rowStates[position] = showLeagueTittle ? SECTIONED_STATE : REGULAR_STATE;
                    //Update last leagueCaption
                    lastLeagueCaption = currentLeagueCaption;
                    break;
            }
            if (showLeagueTittle) {
                separatorView.setVisibility(View.GONE);
                leagueTittle.setVisibility(View.VISIBLE);
            } else {
                separatorView.setVisibility(View.VISIBLE);
                leagueTittle.setVisibility(View.GONE);
            }
            lastLeagueCaption = match.getLeagueCaption();
            leagueTittle.setText(match.getLeagueCaption());
            TextView matchDateTextView = (TextView) convertView
                    .findViewById(R.id.matchDateTextView);
            TextView matchTimeTextView = (TextView) convertView
                    .findViewById(R.id.matchTimeTextView);
            TextView matchAwayTeamTextView = (TextView) convertView
                    .findViewById(R.id.matchAwayTeamTextView);
            TextView matchHomeTeamTextView = (TextView) convertView
                    .findViewById(R.id.matchHomeTeamTextView);
            Date date = match.getDate();
            matchDateTextView.setText(dateFormat.format(date));
            matchTimeTextView.setText(timeFormat.format(date));
            matchAwayTeamTextView.setText(match.getAwayTeamName());
            matchHomeTeamTextView.setText(match.getHomeTeamName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Set required information to null and start all async tasks necessary
                    // to fetch it
                    mFetchedFixture = null;
                    mHomeTeam = null;
                    mAwayTeam = null;
                    mAwayTeamCrest = null;
                    mHomeTeamCrest = null;
                    mFetchFixtureTask = new FetchFixtureTask(match);
                    mFetchFixtureTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    mFetchHomeTeamTask = new FetchTeamDetailsTask(true);
                    mFetchHomeTeamTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            match.getHomeTeamLink());
                    mFetchAwayTeamTask = new FetchTeamDetailsTask(false);
                    mFetchAwayTeamTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            match.getAwayTeamLink());
                }
            });
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<Match> filteredList = new ArrayList<>();
                    int selectedLeaguePosition = mLeaguesSpinner.getSelectedItemPosition();
                    // We implement here the filter logic
                    if (constraint == null || constraint.length() == 0) {
                        // Only take into account the spinnerFilter
                        for (Match m : mAllMatches) {
                            if (selectedLeaguePosition == 0 || matchBelongToSelectedLeague(m)) {
                                filteredList.add(m);
                            }
                        }
                        results.values = filteredList;
                        results.count = filteredList.size();
                    } else {
                        // Take into account both the league filter and the team filter
                        for (Match m : mAllMatches) {
                            if (selectedLeaguePosition == 0 || matchBelongToSelectedLeague(m)) {
                                if (searchMatchAnyTeam(m, constraint))
                                    filteredList.add(m);
                            }
                        }
                        results.values = filteredList;
                        results.count = filteredList.size();
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mMatches.clear();
                    mMatches.addAll((List<Match>) results.values);
                    rowStates = new int[mMatches.size()];
                    notifyDataSetChanged();
                }

                private boolean searchMatchAnyTeam(Match match, CharSequence constraint) {
                    String[] upperCaseHomeTeamName = match.getHomeTeamName().toUpperCase().split(" ");
                    String[] upperCaseAwayTeamName = match.getAwayTeamName().toUpperCase().split(" ");
                    String upperCaseConstraint = constraint.toString().toUpperCase();
                    for (String s : upperCaseHomeTeamName) {
                        if (s.startsWith(upperCaseConstraint)) {
                            return true;
                        }
                    }
                    for (String s : upperCaseAwayTeamName) {
                        if (s.startsWith(upperCaseConstraint)) {
                            return true;
                        }
                    }
                    return false;
                }

                private boolean matchBelongToSelectedLeague(Match match) {
                    return match.getLeagueCaption().
                            equals(((SoccerSeason) mLeaguesSpinner.getSelectedItem()).getCaption());
                }
            };
        }
    }

    private class FetchFixtureTask extends AsyncTask<Void, Void, Fixture> {

        private FetchFixtureTask(Match match) {
            mMatch = match;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(getActivity(), getString(R.string.pleaseWait),
                    getString(R.string.gettingMatch));
        }

        @Override
        protected Fixture doInBackground(Void... params) {
            JSONObject matchJSON = WebServiceUtils.getJSONObjectFromUrl(mMatch.getSelfLink());
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
                mFetchedFixture = fixture;
                tryCallFixtureDetailsActivity();
            } else {
                //TODO handle error
            }
        }
    }

    /**
     * Async task to fetch Team's details
     */
    private class FetchTeamDetailsTask extends AsyncTask<String, Void, Void> {
        private boolean mIsHomeTeam;

        public FetchTeamDetailsTask(boolean home) {
            mIsHomeTeam = home;
        }

        @Override
        protected Void doInBackground(String... params) {
            String teamUrl = params[0];
            JSONObject team = WebServiceUtils.getJSONObjectFromUrl(
                    teamUrl);
            try {
                if (mIsHomeTeam) {
                    mHomeTeam = new Team(team);
                } else {
                    mAwayTeam = new Team(team);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mIsHomeTeam && mHomeTeam != null) {
                mHomeTeamCrestTask = new DownloadCrestImageTask(getActivity(), true);
                mHomeTeamCrestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                if (!mIsHomeTeam && mAwayTeam != null) {
                    mAwayTeamCrestTask = new DownloadCrestImageTask(getActivity(), false);
                    mAwayTeamCrestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    //TODO handle error
                }
            }
        }
    }

    /**
     * AsyncTask that downloads an image for the given URL, and sets the Bitmap in the UI thread
     */
    public class DownloadCrestImageTask extends AsyncTask<String, Integer, Bitmap> {
        private Context mContext;
        private boolean mIsHomeTeam;

        public DownloadCrestImageTask(Context context, boolean home) {
            mContext = context;
            mIsHomeTeam = home;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return WebServiceUtils.downloadBitmapFromUrl(
                    mIsHomeTeam ? mHomeTeam.getCrestURL() : mAwayTeam.getCrestURL(),
                    mContext,
                    mIsHomeTeam ? "home_crest" : "away_crest");
        }

        @Override
        public void onPostExecute(Bitmap image) {
            // show downloaded bitmap in the imageView
            if (image != null) {
                if (mIsHomeTeam) {
                    mHomeTeamCrest = image;
                } else {
                    mAwayTeamCrest = image;
                }
                tryCallFixtureDetailsActivity();
            } else {
                //TODO handle error
            }
        }
    }

    private class FetchLeagueTableTask extends AsyncTask<String, Void, LeagueTable> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(getActivity(), getString(R.string.pleaseWait),
                    getString(R.string.gettingLeagueTable));
        }

        @Override
        protected LeagueTable doInBackground(String... params) {
            String leagueTableUrl = params[0];
            JSONObject leagueTable = WebServiceUtils.getJSONObjectFromUrl(leagueTableUrl);
            LeagueTable result = null;
            try {
                result = new LeagueTable(leagueTable);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(LeagueTable leagueTable) {
            if (leagueTable != null) {
                mProgressDialog.dismiss();
                //When finished fetching leagueTable, start league table activity.
                Intent callLeagueTableActivity = new Intent(getActivity(),
                        LeagueTableActivity.class);
                callLeagueTableActivity.putExtra(EXTRA_LEAGUE_TABLE, leagueTable);
                startActivity(callLeagueTableActivity);
            } else {
                //TODO handle error
            }
        }
    }

}
