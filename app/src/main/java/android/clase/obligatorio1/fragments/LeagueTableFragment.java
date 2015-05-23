package android.clase.obligatorio1.fragments;

import android.clase.obligatorio1.R;
import android.clase.obligatorio1.constants.PreferencesKeys;
import android.clase.obligatorio1.entities.LeagueTable;
import android.clase.obligatorio1.entities.LeagueTableStanding;
import android.clase.obligatorio1.utils.WebServiceUtils;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfredo on 20/05/15.
 */
public class LeagueTableFragment extends Fragment {

    //UI Components
    private Toolbar mToolbar;
    private ObservableListView mStandingsListView;

    /**
     * League table url obtained from the homeActivity
     */
    private String mLeagueTableUrl;

    /**
     * League name obtained from the homeActivity
     */
    private String mLeagueName;

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
     * AsyncTask to fetch the LeagueTable based on its url
     */
    private FetchLeagueTableTask mFetchLeagueTableTask;


    /**
     * Auxiliary boolean to determine if the league is favorite or not
     */
    private boolean mIsFavorite;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent homeIntent = getActivity().getIntent();
        mLeagueTableUrl = homeIntent.getExtras().getString(HomeFragment.EXTRA_LEAGUE_TABLE_URL);
        mLeagueName = homeIntent.getExtras().getString(HomeFragment.EXTRA_LEAGUE_NAME);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mFetchLeagueTableTask = new FetchLeagueTableTask();
        mIsFavorite = mPreferences.getString(PreferencesKeys.PREFS_FAVORITE_LEAGUE, "").equals(mLeagueName);
        mTotalStandings = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_league_table, container, false);
        // ------ Setup standings listView  -----
        mStandingsListView = (ObservableListView) v.findViewById(R.id.standingsListView);
        mStandingsListView.setDivider(null);
        // ------ Start async task to fetch league table  -----
        mFetchLeagueTableTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // ------ Setup toolbar  -----
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.setTitle(mLeagueName);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.league_table_menu, menu);
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

    private class FetchLeagueTableTask extends AsyncTask<Void, Void, LeagueTable> {

        @Override
        protected LeagueTable doInBackground(Void... params) {
            JSONObject leagueTable = WebServiceUtils.getJSONObjectFromUrl(mLeagueTableUrl);
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
            mLeagueTable = leagueTable;
            mStandingsListView.setAdapter(new LeagueStandingsAdapter(mLeagueTable.getStandings()));
            mTotalStandings.addAll(mLeagueTable.getStandings());
        }
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
            if(position == 0)
                convertView.findViewById(R.id.separator).setVisibility(View.GONE);
            LeagueTableStanding standing = getItem(position);
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
            //Paint standings based on their position in the LeagueTable
            if (standing.getPosition() <= 4) {
                convertView.setBackgroundColor(getResources().getColor(R.color.light_green));
            } else {
                if (standing.getPosition() >= mTotalStandings.size() - 3) {
                    convertView.setBackgroundColor(getResources().getColor(R.color.light_red));
                } else {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<LeagueTableStanding> value = new ArrayList<>();
                    // We implement here the filter logic
                    if (constraint == null || constraint.length() == 0) {
                        value.addAll(mTotalStandings);
                    } else {
                        // Filter total leagues by the constraint
                        for (LeagueTableStanding standing : mTotalStandings) {
                            if (standing.getTeamName().toUpperCase().startsWith(constraint.toString()
                                    .toUpperCase()))
                                value.add(standing);

                        }
                    }
                    results.values = value;
                    results.count = value.size();
                    return results;
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
}
