package android.clase.obligatorio1.fragments;


import android.app.ProgressDialog;
import android.clase.obligatorio1.R;
import android.clase.obligatorio1.activities.TeamDetailsActivity;
import android.clase.obligatorio1.constants.JsonKeys;
import android.clase.obligatorio1.constants.WebServiceURLs;
import android.clase.obligatorio1.entities.Fixture;
import android.clase.obligatorio1.entities.LeagueTableStanding;
import android.clase.obligatorio1.entities.Match;
import android.clase.obligatorio1.entities.Player;
import android.clase.obligatorio1.entities.Team;
import android.clase.obligatorio1.utils.WebServiceUtils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alfredo on 17/05/15.
 */
public class FixtureDetailsFragment extends Fragment {
    private static final DateFormat MATCH_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat MATCH_TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    public static final String HOME_CREST_FILE = "home_crest.svg";
    public static final String AWAY_CREST_FILE = "away_crest.svg";

    private DownloadCrestImageTask mFetchHomeTeamCrest;
    private DownloadCrestImageTask mFetchAwayTeamCrest;

    //UI components
    private TextView mMatchDayTextView;
    private TextView mMatchDateTextView;
    private TextView mMatchStatusTextView;
    private TextView mMatchStartTimeTextView;
    private TextView mHomeTeamTextView;
    private TextView mHomeTeamScoreTextView;
    private TextView mAwayTeamTextView;
    private TextView mAwayTeamScoreTextView;
    private TextView mHomeTeamNameH2H;
    private TextView mAwayTeamNameH2H;
    private TextView mHomeTeamWins;
    private TextView mAwayTeamWins;
    private TextView mDraws;
    private Toolbar mToolbar;
    private ListView mHeadToHeadListView;
    private ImageView mHomeTeamLogo;
    private ImageView mAwayTeamLogo;
    private ProgressDialog mProgressDialog;

    private FetchLeagueTableStandingTask mFetchLeagueTableStandingTask;

    private FetchTeamPlayersTask mFetchPlayersTask;

    private LeagueTableStanding mLeagueTableStanding;

    private boolean mFetchedLeagueTableStanding;
    private boolean mFetchedTeamPlayers;

    /**
     * League name obtained from the HomeActivity
     */
    private String mLeagueName;

    /**
     * Fixture received from intent.
     */
    private Fixture mFixture;


    private Team mHomeTeam;
    private Team mAwayTeam;
    private Bitmap mHomeTeamLogoBitmap;
    private Bitmap mAwayTeamLogoBitmap;

//    private FetchFixtureTask mFetchFixtureTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent homeScreenIntent = getActivity().getIntent();
        Bundle extras = homeScreenIntent.getExtras();
        if (extras != null) {
            mFixture = (Fixture) extras.getSerializable(HomeFragment.EXTRA_MATCH);
            mHomeTeam = (Team) extras.getSerializable(HomeFragment.EXTRA_HOME_TEAM);
            mAwayTeam = (Team) extras.getSerializable(HomeFragment.EXTRA_AWAY_TEAM);
            mLeagueName = extras.getString(HomeFragment.EXTRA_LEAGUE_NAME);
        }
        setHasOptionsMenu(true);
        mFetchHomeTeamCrest = new DownloadCrestImageTask(getActivity(), true);
        mFetchHomeTeamCrest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mFetchAwayTeamCrest = new DownloadCrestImageTask(getActivity(), false);
        mFetchAwayTeamCrest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fixutre_details, container, false);
        //To add a header to a ListView, you must create a separated layout and inflate it too.
        final View headToHeadListViewHeader = inflater.inflate(R.layout.fragment_fixture_details_header, mHeadToHeadListView, false);
        mMatchDayTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.matchDayTextView);
        mMatchDateTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.matchDateTextView);
        mMatchStatusTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.matchStatusTextView);
        mMatchStartTimeTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.matchStartTimeTextView);
        mHomeTeamLogo = (ImageView) headToHeadListViewHeader.findViewById(R.id.homeTeamImageView);
        mHomeTeamLogo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mHomeTeamLogo.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        mHomeTeamLogo.setClickable(false);
        mHomeTeamLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFetchedLeagueTableStanding = false;
                mFetchedTeamPlayers = false;
                mFetchLeagueTableStandingTask = new FetchLeagueTableStandingTask(headToHeadListViewHeader);
                mFetchLeagueTableStandingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
                mFetchPlayersTask = new FetchTeamPlayersTask(headToHeadListViewHeader);
                mFetchPlayersTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);

            }
        });
        mHomeTeamTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.homeTeamTextView);
        mHomeTeamScoreTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.homeTeamScoreTextView);
        mAwayTeamTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.awayTeamTextView);
        mAwayTeamLogo = (ImageView) headToHeadListViewHeader.findViewById(R.id.awayTeamImageView);
        mAwayTeamLogo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mAwayTeamLogo.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        mAwayTeamLogo.setClickable(false);
        mAwayTeamLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFetchedLeagueTableStanding = false;
                mFetchedTeamPlayers = false;
                mFetchLeagueTableStandingTask = new FetchLeagueTableStandingTask(headToHeadListViewHeader);
                mFetchLeagueTableStandingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, false);
                mFetchPlayersTask = new FetchTeamPlayersTask(headToHeadListViewHeader);
                mFetchPlayersTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, false);
            }
        });
        mAwayTeamScoreTextView = (TextView) headToHeadListViewHeader.findViewById(R.id.awayTeamScoreTextView);
        mHomeTeamNameH2H = (TextView) headToHeadListViewHeader.findViewById(R.id.home_team_name_h2h);
        mAwayTeamNameH2H = (TextView) headToHeadListViewHeader.findViewById(R.id.away_team_name_h2h);
        mHomeTeamWins = (TextView) headToHeadListViewHeader.findViewById(R.id.home_team_wins);
        mAwayTeamWins = (TextView) headToHeadListViewHeader.findViewById(R.id.away_team_wins);
        mDraws = (TextView) headToHeadListViewHeader.findViewById(R.id.draws);
        mHeadToHeadListView = (ListView) v.findViewById(R.id.head2headListView);
        //Add header to ListView, without data and making it no selectable.
        mHeadToHeadListView.addHeaderView(headToHeadListViewHeader, null, false);
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar);
        mToolbar.setTitle(mLeagueName);
        updateUI();
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return v;
    }

    /**
     * Method to update the UI based on the fetches Fixture
     */
    private void updateUI() {
        mMatchDayTextView.setText(getString(R.string.matchDay) + mFixture.getMatchDay());
        Date matchDate = mFixture.getDate();
        mMatchDateTextView.setText(MATCH_DATE_FORMAT.format(matchDate));
        mMatchStatusTextView.setText(mFixture.getStatus());
        mMatchStartTimeTextView.setText(getString(R.string.startTime) + MATCH_TIME_FORMAT.format(matchDate));
        mHomeTeamTextView.setText(mFixture.getHomeTeam().getName());
//        File homeLogo = getActivity().getFileStreamPath(HOME_CREST_FILE);
//        try {
//            SVG homeLogoSVG = SVGParser.getSVGFromInputStream(new FileInputStream(homeLogo));
//            Drawable homeLogoDrawable = homeLogoSVG.createPictureDrawable();
//            mHomeTeamLogo.setImageDrawable(homeLogoDrawable != null ? homeLogoDrawable
//                    : getResources().getDrawable(R.mipmap.ic_launcher));
//        } catch (Exception e) {
//            mHomeTeamLogo.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
//        }
        Integer homeTeamScore = mFixture.getGoalsHomeTeam();
        //If match status isn't finished, the API returns -1 goals for both teams.
        if (homeTeamScore != -1) {
            mHomeTeamScoreTextView.setText(homeTeamScore.toString());
        } else {
            mHomeTeamScoreTextView.setVisibility(View.INVISIBLE);
        }
        mAwayTeamTextView.setText(mFixture.getAwayTeam().getName());
        Integer awayTeamScore = mFixture.getGoalsAwayTeam();
        //If match status isn't finished, the API returns -1 goals for both teams.
        if (awayTeamScore != -1) {
            mAwayTeamScoreTextView.setText(awayTeamScore.toString());
        } else {
            mAwayTeamScoreTextView.setVisibility(View.INVISIBLE);
        }
        mHomeTeamNameH2H.setText(mFixture.getHomeTeam().getName());
        mAwayTeamNameH2H.setText(mFixture.getAwayTeam().getName());
        mHomeTeamWins.setText(mFixture.getHead2Head().getHomeTeamWins().toString());
        mAwayTeamWins.setText(mFixture.getHead2Head().getAwayTeamWins().toString());
        mDraws.setText(mFixture.getHead2Head().getDraws().toString());
        mHeadToHeadListView.setAdapter(new MatchAdapter(mFixture.getHead2Head().getFixtures()));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fixture_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                boolean matchFinished = mFixture.getStatus().equals("FINISHED");
                StringBuilder textToSend = new StringBuilder(MATCH_DATE_FORMAT
                        .format(mFixture.getDate())).append(" ")
                        .append(MATCH_TIME_FORMAT.format(mFixture.getDate())).append(" \n")
                        .append(mFixture.getHomeTeam().getName()).append(" ").append(matchFinished ?
                                mFixture.getGoalsHomeTeam() : "").append(" - ").append(matchFinished ?
                                mFixture.getGoalsAwayTeam() : "").append(" ")
                        .append(mFixture.getAwayTeam().getName())
                        .append("\n").append(mFixture.getStatus())
                        .append("\n").append(getString(R.string.share_message)).append(" ")
                        .append("https://play.google.com/store/apps?hl=es");
                sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFetchLeagueTableStandingTask != null && mFetchLeagueTableStandingTask.getStatus()
                != AsyncTask.Status.FINISHED) {
            mFetchLeagueTableStandingTask.cancel(true);
        }
        if (mFetchPlayersTask != null && mFetchPlayersTask.getStatus()
                != AsyncTask.Status.FINISHED) {
            mFetchPlayersTask.cancel(true);
        }
    }

    private void tryCallTeamDetailsActivity(View view, boolean isHome) {
        if (mFetchedLeagueTableStanding && mFetchedTeamPlayers) {
            mProgressDialog.dismiss();
            Intent callTeamDetailsActivity = new Intent(getActivity(),
                    TeamDetailsActivity.class);
            callTeamDetailsActivity.putExtra(TeamDetailsFragment.EXTRA_LEAGUE_STANDING, mLeagueTableStanding);
            callTeamDetailsActivity.putExtra(TeamDetailsFragment.EXTRA_TEAM, isHome ?
                    mHomeTeam : mAwayTeam);
            callTeamDetailsActivity.putExtra(TeamDetailsFragment.EXTRA_TEAM_LOGO_BITMAP, isHome ?
                    mHomeTeamLogoBitmap : mAwayTeamLogoBitmap);
            startActivity(callTeamDetailsActivity);
//            String transitionName = getString(R.string.team_logo_transition);
//            Pair<View, String> pair = Pair.create(isHome ? view.findViewById(R.id.homeTeamImageView)
//                    : view.findViewById(R.id.awayTeamImageView), transitionName);
//            ActivityOptionsCompat options =
//                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pair);
//            ActivityCompat.startActivity(getActivity(), callTeamDetailsActivity, options.toBundle());
        }
    }

    private class MatchAdapter extends ArrayAdapter<Match> {

        private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        public MatchAdapter(List<Match> matches) {
            super(getActivity(), 0, matches);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.head_to_head_list_item, null);
            }
            Match match = getItem(position);
            TextView matchDate = (TextView) convertView.findViewById(R.id.h2h_match_date);
            matchDate.setText(dateFormat.format(match.getDate()));
            TextView homeTeamName = (TextView) convertView.findViewById(R.id.h2h_home_team);
            homeTeamName.setText(match.getHomeTeamName());
            TextView homeTeamScore = (TextView) convertView.findViewById(R.id.h2h_home_team_goals);
            homeTeamScore.setText(match.getGoalsHomeTeam().toString());
            TextView awayTeamName = (TextView) convertView.findViewById(R.id.h2h_away_team);
            awayTeamName.setText(match.getAwayTeamName());
            TextView awayTeamScore = (TextView) convertView.findViewById(R.id.h2h_away_team_goals);
            awayTeamScore.setText(match.getGoalsAwayTeam().toString());
            return convertView;
        }
    }

    private class FetchLeagueTableStandingTask extends AsyncTask<Boolean, Void, LeagueTableStanding> {
        private Boolean isHome;
        private View mView;

        public FetchLeagueTableStandingTask(View view) {
            mView = view;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(getActivity(), getString(R.string.pleaseWait),
                    getString(R.string.gettingTeam));
        }

        @Override
        protected LeagueTableStanding doInBackground(Boolean... params) {
            LeagueTableStanding result = null;
            isHome = params[0];
            try {
                JSONObject leagueTable = WebServiceUtils.getJSONObjectFromUrl(
                        mFixture.getSoccerSeasonLink() +
                                WebServiceURLs.INCOMPLETE_GETE_LEAGUETABLE);
                JSONArray standingsJson = leagueTable.getJSONArray(JsonKeys.JSON_STANDING);
                JSONObject standing;
                if (leagueTable != null) {
                    for (int i = 0; i < standingsJson.length(); i++) {
                        standing = standingsJson.getJSONObject(i);
                        if (standing.getJSONObject(JsonKeys.JSON_LINKS)
                                .getJSONObject(JsonKeys.JSON_TEAM_LINK).getString(JsonKeys.JSON_HREF)
                                .equals(isHome ? mHomeTeam.getSelfLink() : mAwayTeam.getSelfLink())) {
                            result = new LeagueTableStanding(standing);
                            if (result.getPosition() <= 4) {
                                result.setBackgroundColor(getResources()
                                        .getColor(R.color.light_green));
                            } else {
                                if (result.getPosition() > standingsJson.length() - 3) {
                                    result.setBackgroundColor(getResources()
                                            .getColor(R.color.light_red));
                                } else {
                                    result.setBackgroundColor(Color.TRANSPARENT);
                                }
                            }
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(LeagueTableStanding standing) {
            mLeagueTableStanding = standing;
            mFetchedLeagueTableStanding = true;
            tryCallTeamDetailsActivity(mView, isHome);
        }
    }

    private class FetchTeamPlayersTask extends AsyncTask<Boolean, Void, List<Player>> {
        private Boolean isHome;
        private View mView;

        public FetchTeamPlayersTask(View view) {
            mView = view;
        }

        @Override
        protected List<Player> doInBackground(Boolean... params) {
            isHome = params[0];
            List<Player> results = new ArrayList<>();
            try {
                JSONArray players = WebServiceUtils.getJSONObjectFromUrl(
                        (isHome ? mHomeTeam.getSelfLink() : mAwayTeam.getSelfLink())
                                + WebServiceURLs.INCOMPLETE_GET_TEAM_PLAYERS)
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
            mFetchedTeamPlayers = true;
            if (isHome) {
                mHomeTeam.setPlayers(players);
            } else {
                mAwayTeam.setPlayers(players);
            }
            tryCallTeamDetailsActivity(mView, isHome);
        }
    }

    /**
     * AsyncTask that downloads an image for the given URL, and sets the Bitmap in the UI thread
     */
    public class DownloadCrestImageTask extends AsyncTask<String, Void, Drawable> {
        private Context mContext;
        private boolean mIsHomeTeam;

        public DownloadCrestImageTask(Context context, boolean home) {
            mContext = context;
            mIsHomeTeam = home;
        }

        @Override
        protected Drawable doInBackground(String... strings) {
            return WebServiceUtils.downloadSVGFromUrlAndConvertToDrawable(
                    mIsHomeTeam ? mHomeTeam.getCrestURL() : mAwayTeam.getCrestURL(),
                    mContext,
                    mIsHomeTeam ? HOME_CREST_FILE : AWAY_CREST_FILE);
        }

        @Override
        public void onPostExecute(Drawable drawable) {
            // show downloaded bitmap in the imageView
            if (drawable != null) {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                drawable.draw(canvas);
                if (mIsHomeTeam) {
                    mHomeTeamLogo.setImageDrawable(drawable);
                    mHomeTeamLogoBitmap = scaleDownBitmap(bitmap, 100, getActivity());
                    mHomeTeamLogo.setClickable(true);

                } else {
                    mAwayTeamLogo.setImageDrawable(drawable);
                    mAwayTeamLogoBitmap = scaleDownBitmap(bitmap, 100, getActivity());
                    mAwayTeamLogo.setClickable(true);
                }
            }
        }
    }


    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));
        if (w > h) {
            w = h;
        }

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }


}
