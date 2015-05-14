package android.clase.obligatorio1.activities;

import android.app.Activity;
import android.clase.obligatorio1.R;
import android.clase.obligatorio1.entities.Fixture;
import android.clase.obligatorio1.entities.LeagueTable;
import android.clase.obligatorio1.entities.Team;
import android.clase.obligatorio1.utils.WebServiceInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

import java.util.ArrayList;

/**
 * created by Alfredo El Ters and Mathias Cabano on 02/05/15.
 */
public class SplashScreenActivity extends Activity {

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.splash_screen);


        new FetchFixturesTask().execute();
    }


    private class FetchFixturesTask extends AsyncTask<Void, Void, ArrayList<Fixture>> {

        @Override
        protected void onPostExecute(ArrayList<Fixture> registers) {
            // Start the next activity
            Intent mainIntent = new Intent().setClass(
                    SplashScreenActivity.this, HomeActivity.class);
            startActivity(mainIntent);

            // Close the activity so the user won't able to go back this
            // activity pressing Back button
            finish();
        }

        @Override
        protected ArrayList<Fixture> doInBackground(Void... params) {
            Fixture test = WebServiceInterface.getInstance().getFixtureById(133566);
            Team testTeam = WebServiceInterface.getInstance().getTeamById(109);
            LeagueTable testLeagueTable = WebServiceInterface.getInstance().getLeagueTableById(357);
            return new ArrayList<>();
        }

    }

}
