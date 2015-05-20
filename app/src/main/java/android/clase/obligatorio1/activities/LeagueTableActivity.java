package android.clase.obligatorio1.activities;

import android.clase.obligatorio1.fragments.LeagueTableFragment;
import android.clase.obligatorio1.utils.SingleFragmentActivity;
import android.support.v4.app.Fragment;

/**
 * Created by alfredo on 20/05/15.
 */
public class LeagueTableActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new LeagueTableFragment();
    }
}
