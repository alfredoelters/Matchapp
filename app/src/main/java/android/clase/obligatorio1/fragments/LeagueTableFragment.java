package android.clase.obligatorio1.fragments;

import android.clase.obligatorio1.R;
import android.clase.obligatorio1.entities.LeagueTable;
import android.clase.obligatorio1.utils.WebServiceUtils;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alfredo on 20/05/15.
 */
public class LeagueTableFragment extends Fragment {

    //UI Components
    private TextView mTestTextView;
    private Toolbar mToolbar;

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
     * AsyncTask to fetch the LeagueTable based on its url
     */
    private FetchLeagueTableTask mFetchLeagueTableTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent homeIntent = getActivity().getIntent();
        mLeagueTableUrl = homeIntent.getExtras().getString(HomeFragment.EXTRA_LEAGUE_TABLE_URL);
        mLeagueName = homeIntent.getExtras().getString(HomeFragment.EXTRA_LEAGUE_NAME);
        mFetchLeagueTableTask = new FetchLeagueTableTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_league_table, container, false);
        mTestTextView = (TextView) v.findViewById(R.id.testUrl);
        mTestTextView.setText(mLeagueTableUrl);
        mFetchLeagueTableTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.setTitle(mLeagueName);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return v;
    }

    /**
     * Method to update the UI based on the leagueTable fetch by the async task
     */
    private void updateUI() {

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
            updateUI();
        }
    }
}
