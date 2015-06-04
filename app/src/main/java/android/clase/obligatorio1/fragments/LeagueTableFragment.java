package android.clase.obligatorio1.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.clase.obligatorio1.R;
import android.clase.obligatorio1.activities.TeamDetailsActivity;
import android.clase.obligatorio1.constants.JsonKeys;
import android.clase.obligatorio1.constants.PreferencesKeys;
import android.clase.obligatorio1.constants.WebServiceURLs;
import android.clase.obligatorio1.entities.LeagueTable;
import android.clase.obligatorio1.entities.LeagueTableStanding;
import android.clase.obligatorio1.entities.Player;
import android.clase.obligatorio1.entities.Team;
import android.clase.obligatorio1.utils.WebServiceUtils;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfredo on 20/05/15.
 */
public class LeagueTableFragment extends Fragment {

    //UI Components
    private Toolbar mToolbar;
    private ObservableListView mStandingsListView;
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;


    /**
     * LeagueTable fetched by the async task
     */
    private LeagueTable mLeagueTable;

    /**
     * Auxiliary list to save the total standings to filter.
     */
    private List<LeagueTableStanding> mTotalStandings;

    /**
     * Preferences to get favorite LeagueTable
     */
    private SharedPreferences mPreferences;


    /**
     * Auxiliary boolean to determine if the league is favorite or not
     */
    private boolean mIsFavorite;

    /**
     * AsyncTask to fetch team details of the team selected
     */
    private FetchTeamDetailsTask mFetchTeamDetailsTask;

    /**
     * AsyncTask to fetch team players of the team selected
     */
    private FetchTeamPlayersTask mFetchTeamPlayersTask;

    /**
     * Auxiliary list to store the team fetch by the FetchTeamDetailsTask
     */
    private Team mTeam;

    /**
     * Auxiliary list to store all players fetch by the FetchTeamPlayersTask
     */
    private List<Player> mTeamPlayers;

    /**
     * Boolean to notify the end of the FetchTeamDetailsTask
     * (for concurrency purposes)
     */
    private boolean mFetchTeam;

    /**
     * Boolean to notify the end of the FetchTeamPlayersTask
     * (for concurrency purposes)
     */
    private boolean mFetchPlayers;

    private LeagueTableStanding mLeagueTableStanding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLeagueTable = (LeagueTable) getActivity().getIntent()
                .getSerializableExtra(HomeFragment.EXTRA_LEAGUE_TABLE);
        if (mLeagueTable != null) {
            mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mIsFavorite = mPreferences.getString(PreferencesKeys.PREFS_FAVORITE_LEAGUE, "")
                    .equals(mLeagueTable.getLeagueCaption());
            mTotalStandings = new ArrayList<>();
        } else {
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_league_table, container, false);
        // ------ Setup standings listView  -----
        mStandingsListView = (ObservableListView) v.findViewById(R.id.standingsListView);
        mStandingsListView.setDivider(null);
        mStandingsListView.setAdapter(new LeagueStandingsAdapter(mLeagueTable.getStandings()));
        mTotalStandings.addAll(mLeagueTable.getStandings());
        // ------ Setup toolbar  -----
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.setTitle(mLeagueTable.getLeagueCaption());
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        mAlertDialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.alertErrorTittle)
                .setMessage(R.string.alertError)
                .setIcon(android.R.drawable.ic_dialog_alert).setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAlertDialog.dismiss();
                            }
                        }).create();
        mAlertDialog.dismiss();
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_league_table, menu);
        MenuItem favoriteItem = menu.findItem(R.id.favoriteAction);
        if (mIsFavorite)
            favoriteItem.setIcon(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
        else
            favoriteItem.setIcon(R.drawable.abc_btn_rating_star_off_mtrl_alpha);

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
                    ((LeagueStandingsAdapter) mStandingsListView.getAdapter())
                            .getFilter().filter(newText);
                    return false;
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favoriteAction:
                if (mIsFavorite) {
                    mPreferences.edit().remove(PreferencesKeys.PREFS_FAVORITE_LEAGUE).apply();
                    item.setIcon(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                    mIsFavorite = false;
                } else {
                    //Save the league as favorite
                    mPreferences.edit().putString(PreferencesKeys.PREFS_FAVORITE_LEAGUE,
                            mLeagueTable.getLeagueCaption()).apply();
                    item.setIcon(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                    mIsFavorite = true;
                }
                return true;
            case R.id.searchTeamAction:
                getActivity().onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to cancel all running asyncTasks
     */
    private void cancelAllAsyncTasks(){
        if (mFetchTeamDetailsTask != null && mFetchTeamDetailsTask.getStatus() != AsyncTask.Status.FINISHED) {
            mFetchTeamDetailsTask.cancel(true);
        }
        if (mFetchTeamPlayersTask != null && mFetchTeamPlayersTask.getStatus() != AsyncTask.Status.FINISHED) {
            mFetchTeamPlayersTask.cancel(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAllAsyncTasks();
    }

    private class LeagueStandingsAdapter extends ArrayAdapter<LeagueTableStanding> implements Filterable {
        public LeagueStandingsAdapter(List<LeagueTableStanding> standings) {
            super(getActivity(), 0, standings);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.league_table_standing_list_item, null);
            }
            if (position == 0)
                convertView.findViewById(R.id.separator).setVisibility(View.GONE);
            final LeagueTableStanding standing = getItem(position);
            ((TextView) convertView.findViewById(R.id.positionTextView))
                    .setText(standing.getPosition().toString());
            ((TextView) convertView.findViewById(R.id.teamTextView))
                    .setText(standing.getTeamName());
            ((TextView) convertView.findViewById(R.id.playedGamesTextView))
                    .setText(standing.getPlayedGames().toString());
            ((TextView) convertView.findViewById(R.id.goalsInFavorTextView))
                    .setText(standing.getGoals().toString());
            ((TextView) convertView.findViewById(R.id.goalsAgainstTextView))
                    .setText(standing.getGoalsAgainst().toString());
            ((TextView) convertView.findViewById(R.id.goalsDifferenceTextView))
                    .setText(standing.getGoalDifference().toString());
            ((TextView) convertView.findViewById(R.id.pointsTextView))
                    .setText(standing.getPoints().toString());
            //Paint standings based on their position in the LeagueTable and save the color in the
            //standing in case it is needed later. (E.g.: in the team's details screen)
            if (standing.getPosition() <= 4) {
                convertView.setBackgroundColor(getResources().getColor(R.color.light_green));
                standing.setBackgroundColor(getResources().getColor(R.color.light_green));
            } else {
                if (standing.getPosition() > mTotalStandings.size() - 3) {
                    convertView.setBackgroundColor(getResources().getColor(R.color.light_red));
                    standing.setBackgroundColor(getResources().getColor(R.color.light_red));
                } else {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Set booleans to false to indicate that we will start fetching both entities
                    mFetchTeam = false;
                    mFetchPlayers = false;
                    //Start the fetchTeamsDetailsTask to fetch team's data
                    mFetchTeamDetailsTask = new FetchTeamDetailsTask();
                    mFetchTeamDetailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, standing);
                    //Start the fetchTeamsDetailsTask to fetch team's players
                    mFetchTeamPlayersTask = new FetchTeamPlayersTask();
                    mFetchTeamPlayersTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, standing);
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
                    List<LeagueTableStanding> values = new ArrayList<>();
                    // We implement here the filter logic
                    if (constraint == null || constraint.length() == 0) {
                        values.addAll(mTotalStandings);
                    } else {
                        // Filter total leagues by the constraint
                        for (LeagueTableStanding standing : mTotalStandings) {
                            if (searchStandingByTeam(standing, constraint))
                                values.add(standing);

                        }
                    }
                    results.values = values;
                    results.count = values.size();
                    return results;
                }

                private boolean searchStandingByTeam(LeagueTableStanding standing, CharSequence constraint) {
                    String[] upperCaseTeamName = standing.getTeamName().toUpperCase().split(" ");
                    String upperCaseConstraint = constraint.toString().toUpperCase();
                    for (String s : upperCaseTeamName) {
                        if (s.startsWith(upperCaseConstraint)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mLeagueTable.getStandings().clear();
                    mLeagueTable.getStandings().addAll((List<LeagueTableStanding>) results.values);
                    notifyDataSetChanged();
                }
            };
        }
    }

    /**
     * Method to notify the user of errors when trying to fetch data from the WS
     */
    private void errorOccurredInAsyncTasks(){
        cancelAllAsyncTasks();
        mProgressDialog.dismiss();
        mAlertDialog.show();
    }

    private void callTeamDetailsActivity(){
        mProgressDialog.dismiss();
        mTeam.getPlayers().addAll(mTeamPlayers);
        Intent callTeamDetailsActivity = new Intent(getActivity(),
                TeamDetailsActivity.class);
        callTeamDetailsActivity.putExtra(TeamDetailsFragment.EXTRA_LEAGUE_STANDING, mLeagueTableStanding);
        callTeamDetailsActivity.putExtra(TeamDetailsFragment.EXTRA_TEAM, mTeam);
        startActivity(callTeamDetailsActivity);
    }

    private class FetchTeamDetailsTask extends AsyncTask<LeagueTableStanding, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), getString(R.string.pleaseWait),
                    getString(R.string.gettingTeamInfo));
        }

        @Override
        protected Void doInBackground(LeagueTableStanding... params) {
            mLeagueTableStanding = params[0];
            JSONObject team = WebServiceUtils.getJSONObjectFromUrl(
                    mLeagueTableStanding.getTeamLink());
            try {
                mTeam = new Team(team);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mTeam != null) {
                mFetchTeam = true;
                if (mFetchPlayers) {
                    callTeamDetailsActivity();
                }
            } else {
                errorOccurredInAsyncTasks();
            }
        }
    }

    private class FetchTeamPlayersTask extends AsyncTask<LeagueTableStanding, Void, List<Player>> {

        @Override
        protected List<Player> doInBackground(LeagueTableStanding... params) {
            mLeagueTableStanding = params[0];
            List<Player> results = new ArrayList<>();
            try {
                JSONArray players = WebServiceUtils.getJSONObjectFromUrl(
                        mLeagueTableStanding.getTeamLink() + WebServiceURLs.INCOMPLETE_GET_TEAM_PLAYERS)
                        .getJSONArray(JsonKeys.JSON_PLAYERS);
                JSONObject player;
                for (int i = 0; i < players.length(); i++) {
                    player = players.getJSONObject(i);
                    results.add(new Player(player));
                }
            } catch (JSONException | NullPointerException | ParseException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<Player> players) {
            mTeamPlayers = players;
            mFetchPlayers = true;
            if (mFetchTeam) {
                callTeamDetailsActivity();
            }
        }
    }
}
