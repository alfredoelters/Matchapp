package android.clase.obligatorio1.fragments;

import android.app.AlertDialog;
import android.clase.obligatorio1.R;
import android.clase.obligatorio1.activities.HomeActivity;
import android.clase.obligatorio1.constants.PreferencesKeys;
import android.clase.obligatorio1.utils.SingleFragmentActivity;
import android.clase.obligatorio1.utils.WebServiceInterface;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private Timer mConnectionTimeoutTimer;

    private SharedPreferences mPreferences;

    /**
     * Dialog to alert the user that some GET request where unsuccessful
     */
    private AlertDialog mAlertDialog;


    /**
     * boolean to notify that the GET to fetch leagues was successful
     */
    private boolean mLoadedLeagues;

    /**
     * boolean to notify that the GET to fetch today's fixtures was successful
     */
    private boolean mLoadedMatches;

    /**
     * Task used to fetch leagues data
     */
    private FetchLeaguesTask mFetchLeaguesTask;

    /**
     * Task used to fetch today's matches data
     */
    private FetchMatchesTask mFetchMatchesTask;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SingleFragmentActivity) getActivity()).lockScreenOrientation();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

//        mConnectionTimeoutTimer = new Timer();
//        mConnectionTimeoutTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                alertConnectionTimeout();
//            }
//
//        }, CONNECTION_TIMEOUT);
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
//        mConnectionTimeoutTimer.cancel();
        mLoadedMatches = false;
        mLoadedLeagues = false;
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
            return WebServiceInterface.getInstance().getSoccerSeasonsJSON();
        }

        @Override
        protected void onPostExecute(String leaguesJson) {
            if (leaguesJson != null) {
                mLoadedLeagues = true;
                mPreferences.edit().putString(PreferencesKeys.PREFS_LEAGUES, leaguesJson).commit();
                if (mLoadedMatches)
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
            calendar.set(Calendar.DAY_OF_MONTH,17);
            calendar.set(Calendar.MONTH,4);
            calendar.set(Calendar.YEAR, 2015);

            return WebServiceInterface.getInstance().getFixturesJSONForDate(calendar.getTime(), null);
        }

        @Override
        protected void onPostExecute(String fixturesJson) {
            if (fixturesJson != null) {
                mLoadedMatches = true;
                mPreferences.edit().putString(PreferencesKeys.PREFS_HOME_MATCHES, fixturesJson).commit();
                if (mLoadedLeagues)
                    startHomeActivity();
            } else {
                alertLoadError();
            }
        }

    }
}
