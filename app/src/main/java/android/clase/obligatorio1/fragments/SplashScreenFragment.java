package android.clase.obligatorio1.fragments;

import android.app.Fragment;
import android.clase.obligatorio1.R;
import android.clase.obligatorio1.activities.HomeActivity;
import android.clase.obligatorio1.entities.Fixture;
import android.clase.obligatorio1.entities.LeagueTable;
import android.clase.obligatorio1.entities.Match;
import android.clase.obligatorio1.entities.Team;
import android.clase.obligatorio1.utils.SingleFragmentActivity;
import android.clase.obligatorio1.utils.WebServiceInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alfredo El Ters and Mathias Cabano on 15/05/15.
 */
public class SplashScreenFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SingleFragmentActivity) getActivity()).lockScreenOrientation();


        new FetchFixturesTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_screen, container, false);
    }


    private class FetchFixturesTask extends AsyncTask<Void, Void, ArrayList<Fixture>> {

        @Override
        protected void onPostExecute(ArrayList<Fixture> registers) {
            // Start the next activity
            Intent mainIntent = new Intent().setClass(
                    getActivity(), HomeActivity.class);
            startActivity(mainIntent);

            // Close the activity so the user won't able to go back this
            // activity pressing Back button
            getActivity().finish();
        }

        @Override
        protected ArrayList<Fixture> doInBackground(Void... params) {
            Fixture test = WebServiceInterface.getInstance().getFixtureById(133566);
            Team testTeam = WebServiceInterface.getInstance().getTeamById(109);
            LeagueTable testLeagueTable = WebServiceInterface.getInstance().getLeagueTableById(357);
            List<Match> fixtures = WebServiceInterface.getInstance().getFixturesForDate(new Date(), null);
            return new ArrayList<>();
        }

    }
}
