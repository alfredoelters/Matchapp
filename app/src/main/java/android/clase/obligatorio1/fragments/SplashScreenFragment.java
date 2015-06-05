package android.clase.obligatorio1.fragments;

import android.app.AlertDialog;
import android.clase.obligatorio1.R;
import android.clase.obligatorio1.activities.HomeActivity;
import android.clase.obligatorio1.constants.WebServiceURLs;
import android.clase.obligatorio1.database.MatchesDAO;
import android.clase.obligatorio1.utils.SingleFragmentActivity;
import android.clase.obligatorio1.utils.WebServiceUtils;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Alfredo El Ters and Mathias Cabano on 15/05/15.
 */
public class SplashScreenFragment extends Fragment {
    /**
     * Milliseconds after which the user will receive a connection timeout error
     */
    private static final int CONNECTION_TIMEOUT = 5000;

    public static final String EXTRA_MATCHES = "mMatches";
    public static final String EXTRA_LEAGUES = "mLeagues";

    private static final DateFormat RESPONSE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Timer mConnectionTimeoutTimer;


    /**
     * Dialog to alert the user that some GET request where unsuccessful
     */
    private AlertDialog mAlertDialog;

    /**
     * Task used to fetch leagues data
     */
    private FetchLeaguesTask mFetchLeaguesTask;

    /**
     * Task used to fetch today's matches data
     */
    private FetchMatchesTask mFetchMatchesTask;

    /**
     * Matches fetched by the async task
     */
    private String mMatches;

    /**
     * Matches leagues by the async task
     */
    private String mLeagues;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SingleFragmentActivity) getActivity()).lockScreenRotation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.connectionTimeout)
                .setTitle(R.string.connectionTimeoutTittle).setNeutralButton(R.string.retry,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Retry data fetch
                        mFetchMatchesTask = new FetchMatchesTask();
                        mFetchMatchesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        mFetchLeaguesTask = new FetchLeaguesTask();
                        mFetchLeaguesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        //Reset connection timeout
                        mConnectionTimeoutTimer = new Timer();
                        mConnectionTimeoutTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                alertConnectionTimeout();
                            }
                        }, CONNECTION_TIMEOUT);
                        mAlertDialog.hide();
                    }
                });
        mAlertDialog = builder.create();
        //By using the executeOnExecutor method the asyncTasks run concurrently
        mFetchLeaguesTask = new FetchLeaguesTask();
        mFetchLeaguesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mFetchMatchesTask = new FetchMatchesTask();
        mFetchMatchesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAlertDialog.hide();
        stopTasks();
    }

    /**
     * Method used to stop all running tasks
     */
    private void stopTasks() {
        mMatches = null;
        mLeagues = null;
        if (mFetchMatchesTask != null && mFetchMatchesTask.getStatus() != AsyncTask.Status.FINISHED) {
            mFetchMatchesTask.cancel(true);
        }
        if (mFetchLeaguesTask != null && mFetchLeaguesTask.getStatus() != AsyncTask.Status.FINISHED) {
            mFetchLeaguesTask.cancel(true);
        }
    }

    /**
     * Method to start the homeActivity and finish the splashScreenActivity
     */
    private void startHomeActivity() {
//        mConnectionTimeoutTimer.cancel();
        // Start the next activity
        Intent mainIntent = new Intent().setClass(
                getActivity(), HomeActivity.class);
        mainIntent.putExtra(EXTRA_MATCHES, mMatches);
        mainIntent.putExtra(EXTRA_LEAGUES,mLeagues);
        startActivity(mainIntent);

        // Close the activity so the user won't able to go back this
        // activity pressing Back button
        getActivity().finish();
    }

    /**
     * Method used to alert the user of errors loading the data
     */
    private void alertLoadError() {
        stopTasks();
        if (isAdded()) {
            mAlertDialog.setTitle(R.string.alertErrorTittle);
            mAlertDialog.setMessage(getString(R.string.alertError));
            mAlertDialog.show();
        }
    }

    /**
     * Method used to alert the user of connection timeout
     */
    private void alertConnectionTimeout() {
        stopTasks();
        if (isAdded()) {
            mAlertDialog.setTitle(R.string.connectionTimeoutTittle);
            mAlertDialog.setMessage(getString(R.string.connectionTimeout));
            mAlertDialog.show();
        }
    }

    /**
     * AsyncTask to fetch all leagues.
     */
    private class FetchLeaguesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return WebServiceUtils.getJSONStringFromUrl(WebServiceURLs.GET_ALL_SOCCER_SEASONS);
        }

        @Override
        protected void onPostExecute(String leaguesJson) {
            if (leaguesJson != null) {
                mLeagues = leaguesJson;
                if (mMatches != null)
                    startHomeActivity();
            } else {
                alertLoadError();
            }
        }
    }

    /**
     * AsyncTask to fetch today's matches.
     */
    private class FetchMatchesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            //Fixed date for debug purposes
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 17);
            calendar.set(Calendar.MONTH, 4);
            calendar.set(Calendar.YEAR, 2015);
            String dateString = RESPONSE_DATE_FORMAT.format(calendar.getTime());
            String request = String.format(WebServiceURLs.GET_FIXTURES_OF_DATE_FOR_ALL_LEAGUES,
                    dateString, dateString);
            MatchesDAO dao = new MatchesDAO(getActivity());
            String cachedJson = dao.getMatches(request);
            if (cachedJson == null) {
                cachedJson = WebServiceUtils.getJSONStringFromUrl(request);
                if (cachedJson != null)
                    dao.insertTodaysMatches(request, cachedJson);
            }
            return cachedJson;
        }

        @Override
        protected void onPostExecute(String fixturesJson) {
            if (fixturesJson != null) {
                mMatches = fixturesJson;
                if (mLeagues != null)
                    startHomeActivity();
            } else {
                alertLoadError();
            }
        }

    }
}
