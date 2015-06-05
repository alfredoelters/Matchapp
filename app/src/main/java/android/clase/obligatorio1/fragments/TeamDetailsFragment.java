package android.clase.obligatorio1.fragments;


import android.clase.obligatorio1.R;
import android.clase.obligatorio1.entities.LeagueTableStanding;
import android.clase.obligatorio1.entities.Player;
import android.clase.obligatorio1.entities.Team;
import android.clase.obligatorio1.utils.WebServiceUtils;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by alfredo on 17/05/15.
 */
public class TeamDetailsFragment extends Fragment {

    /**
     * Extra keys to send data to the TeamDetailsActivity
     */
    public static final String EXTRA_LEAGUE_STANDING = "leagueTableStanding";
    public static final String EXTRA_TEAM = "team";
    public static final String EXTRA_TEAM_LOGO_BITMAP = "teamLogo";

    //UI Components
    private Toolbar mToolbar;
    private TextView mMarketValueTextView;
    private ObservableListView mPlayersListView;
    private ImageView mTeamCrestImageView;


    /**
     * Current team to show
     */
    private Team mTeam;
    private Bitmap mTeamLogoBitmap;

    /**
     * Current's team standing in the league
     */
    private LeagueTableStanding mLeagueTableStanding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        mLeagueTableStanding = (LeagueTableStanding) intent
                .getSerializableExtra(EXTRA_LEAGUE_STANDING);
        mTeam = (Team) intent.getSerializableExtra(EXTRA_TEAM);
        mTeamLogoBitmap = intent.getParcelableExtra(EXTRA_TEAM_LOGO_BITMAP);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_details, container, false);
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.setTitle(mTeam.getName());
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPlayersListView = (ObservableListView) v.findViewById(R.id.playersListView);
        View playersViewHeader = inflater.inflate(R.layout.team_details_fragment_header,
                mPlayersListView, false);
        mTeamCrestImageView = (ImageView) playersViewHeader.findViewById(R.id.teamCrestImageView);
        if (mTeamLogoBitmap != null) {
            mTeamCrestImageView.setImageBitmap(mTeamLogoBitmap);
        } else {
            mTeamCrestImageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        }
        mMarketValueTextView = (TextView) playersViewHeader.findViewById(R.id.marketValueTextView);
        String marketValue = mTeam.getSquadMarketValue();
        if (marketValue == null || marketValue.equals("null")) {
            //Hide market value linear layout in case there is no information about it.
            playersViewHeader.findViewById(R.id.marketValueLinearLayout).setVisibility(View.GONE);
        } else {
            mMarketValueTextView.setText(mTeam.getSquadMarketValue());
        }
        View standingView = playersViewHeader.findViewById(R.id.standingLayout);
        ((TextView) standingView.findViewById(R.id.positionTextView)).setText(
                mLeagueTableStanding.getPosition().toString());
        standingView.findViewById(R.id.teamTextView).setVisibility(View.GONE);
        ((TextView) standingView.findViewById(R.id.playedGamesTextView)).setText(
                mLeagueTableStanding.getPlayedGames().toString());
        ((TextView) standingView.findViewById(R.id.goalsInFavorTextView)).setText(
                mLeagueTableStanding.getGoals().toString());
        ((TextView) standingView.findViewById(R.id.goalsAgainstTextView)).setText(
                mLeagueTableStanding.getGoalsAgainst().toString());
        ((TextView) standingView.findViewById(R.id.goalsDifferenceTextView)).setText(
                mLeagueTableStanding.getGoalDifference().toString());
        ((TextView) standingView.findViewById(R.id.pointsTextView)).setText(
                mLeagueTableStanding.getPoints().toString());
        standingView.setBackgroundColor(mLeagueTableStanding.getBackgroundColor());
        //Add the header to the listView so that it is also scrolled.
        mPlayersListView.addHeaderView(playersViewHeader, null, false);
        mPlayersListView.setAdapter(new PlayersAdapter(mTeam.getPlayers()));
        if (mTeam.getPlayers().isEmpty()) {
            //Hide players table is there is no information of them.
            playersViewHeader.findViewById(R.id.playersSectionTittle).setVisibility(View.GONE);
            playersViewHeader.findViewById(R.id.playersTableTittle).setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Since this activity can have 2 different parents, we cant rely on the parentActivity
        //attribute in the Manifest. In this case we just finish the current activity to show the
        //activity that is on top of the back stack (the parent of this one)
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class PlayersAdapter extends ArrayAdapter<Player> {

        public PlayersAdapter(List<Player> players) {
            super(getActivity(), 0, players);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.players_list_item, null);
            }
            Player player = getItem(position);
            TextView playerNumber = (TextView) convertView.findViewById(R.id.playerNumberTextView);
            TextView playerName = (TextView) convertView.findViewById(R.id.playerNameTextView);
            TextView playerPosition = (TextView) convertView.findViewById(R.id.playerPositionTextView);
            TextView playerNationality = (TextView) convertView.findViewById(R.id.playerNationalityTextView);
            playerNumber.setText(player.getJerseyNumber()!=null? player.getJerseyNumber().toString():"-");
            playerName.setText(player.getName());
            playerPosition.setText(player.getPosition());
            playerNationality.setText(player.getNationality());
            return convertView;
        }
    }

}
