package android.clase.obligatorio1.fragments;

import android.clase.obligatorio1.R;
import android.clase.obligatorio1.activities.FixtureDetailsActivity;
import android.clase.obligatorio1.activities.LeagueTableActivity;
import android.clase.obligatorio1.activities.TeamDetailsActivity;
import android.clase.obligatorio1.constants.JsonKeys;
import android.clase.obligatorio1.constants.PreferencesKeys;
import android.clase.obligatorio1.constants.WebServiceURLs;
import android.clase.obligatorio1.entities.Match;
import android.clase.obligatorio1.entities.SoccerSeason;
import android.clase.obligatorio1.utils.WebServiceUtils;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Alfredo El Ters and Mathias Cabano on 15/05/15.
 */
public class HomeFragment extends Fragment implements ObservableScrollViewCallbacks {
    //Extra keys
    public static final String EXTRA_MATCH_URL = "matchUrl";
    public static final String EXTRA_LEAGUE_TABLE_URL = "leagueTableUrl";
    public static final String EXTRA_LEAGUE_NAME = "leagueName";

    //UI components
    private Spinner mLeaguesSpinner;
    private ObservableListView mHomeListView;
    private View mHeaderView;
    private Toolbar mToolbar;

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
     * Variable to store the LoadMatchesTask in case it needs to be canceled
     */
    private List<FetchMatchesTask> mFetchMatchesTasks;

    /**
     * Variable to store the LoadLeaguesTask in case it needs to be canceled
     */
    private LoadLeaguesTask mLoadLeaguesTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mLeagues = new ArrayList<>();
        mFetchMatchesTasks = new ArrayList<>();
        mLoadLeaguesTask = new LoadLeaguesTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);


        // ------ Setup matches listView  -----
        mHomeListView = (ObservableListView) v.findViewById(R.id.homeListView);
        mHomeListView.setDivider(null);
        mHomeListView.setDividerHeight(15);
        // add toolbar padding
        mHomeListView.addHeaderView(inflater.inflate(R.layout.padding, mHomeListView, false));
        // add toolbar padding
        mHomeListView.addHeaderView(inflater.inflate(R.layout.padding, mHomeListView, false));
        mHomeListView.setAdapter(new SoccerSeasonMatchesAdapter(mLeagues));
        mHomeListView.setScrollViewCallbacks(this);

        // ------ Setup leagues Spinner  -----
        mLeaguesSpinner = (Spinner) v.findViewById(R.id.leaguesSpinner);

        mLeaguesSpinner.setAdapter(new LeaguesAdapter(mLeagues));

        //Set listener to filter leagues results
        mLeaguesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO make filter
//                View matchView;
//                SoccerSeasonMatchesAdapter adapter = (SoccerSeasonMatchesAdapter)
//                        ((HeaderViewListAdapter) mHomeListView.getAdapter()).getWrappedAdapter();
//                for (int i = 0;i<adapter.getCount();i++) {
//                    matchView = adapter.getView(i,mHomeListView,null);
//                    //Show or hide league depending on the position selected
//                    if(position == i)
//                        matchView.setVisibility(View.VISIBLE);
//                    else
//                        matchView.setVisibility(View.GONE);
//                }
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // ------ Start async tasks to load data into memory  -----
        //By using the executeOnExecutor method the asyncTasks run concurrently
        mLoadLeaguesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        inflater.inflate(R.menu.menu_home,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.viewLeagueTableAction:
                //Call leagueTableActivity with the selected league url and name
                Intent intent = new Intent(getActivity(), LeagueTableActivity.class);
                SoccerSeason selectedLeague = ((SoccerSeason)mLeaguesSpinner
                        .getSelectedItem());
                intent.putExtra(EXTRA_LEAGUE_TABLE_URL, selectedLeague.getLeagueTableLink());
                intent.putExtra(EXTRA_LEAGUE_NAME, selectedLeague.getCaption());
                startActivity(intent);
                return true;
            case R.id.searchLeagueAction:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Finish all running async tasks
        if (mLoadLeaguesTask != null && mLoadLeaguesTask.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadLeaguesTask.cancel(true);
        }
        for (FetchMatchesTask task : mFetchMatchesTasks) {
            if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
                task.cancel(true);
            }
        }
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

    private void startFetchMatchesTasks() {
        FetchMatchesTask task;
        for (int i = 1; i < mLeagues.size(); i++) {
            task = new FetchMatchesTask();
            mFetchMatchesTasks.add(task);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    mLeagues.get(i).getSelfLink(), i + "");
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
            //Dummy league to show all leagues in the filter
            mLeagues.clear();
            mLeagues.addAll(soccerSeasons);
            ((ArrayAdapter<SoccerSeason>) mLeaguesSpinner.getAdapter()).notifyDataSetChanged();
            startFetchMatchesTasks();
        }
    }

    /**
     * Async task to load matches fetches by the splash screen into memory
     */
    private class FetchMatchesTask extends AsyncTask<String, Void, List<Match>> {
        private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private int soccerSeasonPositionInList;

        @Override
        protected List<Match> doInBackground(String... params) {
            String soccerSeasonLink = params[0];
            soccerSeasonPositionInList = Integer.parseInt(params[1]);
            //Fixed date for debug purposes
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 17);
            calendar.set(Calendar.MONTH, 4);
            calendar.set(Calendar.YEAR, 2015);
            String dateFormatted = dateFormat.format(calendar.getTime());
            String matchesJson = WebServiceUtils.getJSONStringFromUrl(soccerSeasonLink
                    + String.format(WebServiceURLs.INCOMPLETE_GET_FIXTURES_OF_DATE_FOR_LEAGUE,
                    dateFormatted, dateFormatted));
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
            mLeagues.get(soccerSeasonPositionInList).getMatches().clear();
            mLeagues.get(soccerSeasonPositionInList).getMatches().addAll(matches);
            ((SoccerSeasonMatchesAdapter) ((HeaderViewListAdapter) mHomeListView.getAdapter())
                    .getWrappedAdapter()).notifyDataSetChanged();
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
    private class SoccerSeasonMatchesAdapter extends ArrayAdapter<SoccerSeason> {

        private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        private final DateFormat timeFormat = new SimpleDateFormat("hh:mm a");


        public SoccerSeasonMatchesAdapter(List<SoccerSeason> leagues) {
            super(getActivity(), 0, leagues);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            if (position != mSelectedLeaguePosition)
//                return getActivity().getLayoutInflater().inflate(R.layout.null_item, null);

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.soccer_season_match_list, null);
            }
            LinearLayout matchesList = (LinearLayout) convertView
                    .findViewById(R.id.matchesLinearLayout);
            //TODO ask the professor why this is called for each child
            matchesList.removeAllViews();
            TextView leagueTittle = (TextView) convertView.findViewById(R.id.leagueTittle);
            final SoccerSeason league = getItem(position);
            leagueTittle.setText(league.getCaption());
            LayoutInflater li = getActivity().getLayoutInflater();
            for (int i = 0; i < league.getMatches().size(); i++) {
                final Match match = league.getMatches().get(i);
                View matchView = li.inflate(R.layout.home_match_list_item, null, false);
                View separatorView = matchView.findViewById(R.id.separator);
                //Hide first separator
                if (i == 0)
                    separatorView.setVisibility(View.GONE);
                TextView matchDateTextView = (TextView) matchView
                        .findViewById(R.id.matchDateTextView);
                TextView matchTimeTextView = (TextView) matchView
                        .findViewById(R.id.matchTimeTextView);
                TextView matchAwayTeamTextView = (TextView) matchView
                        .findViewById(R.id.matchAwayTeamTextView);
                TextView matchHomeTeamTextView = (TextView) matchView
                        .findViewById(R.id.matchHomeTeamTextView);
                Date date = match.getDate();
                matchDateTextView.setText(dateFormat.format(date));
                matchTimeTextView.setText(timeFormat.format(date));
                matchAwayTeamTextView.setText(match.getAwayTeamName());
                matchHomeTeamTextView.setText(match.getHomeTeamName());
                matchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callTeamDetailActivity = new Intent(getActivity(),
                                FixtureDetailsActivity.class);
                        callTeamDetailActivity.putExtra(EXTRA_MATCH_URL, match.getSelfLink());
                        callTeamDetailActivity.putExtra(EXTRA_LEAGUE_NAME, league.getCaption());
                        startActivity(callTeamDetailActivity);
                    }
                });
                matchesList.addView(matchView);
            }
            return convertView;
        }
    }

}
