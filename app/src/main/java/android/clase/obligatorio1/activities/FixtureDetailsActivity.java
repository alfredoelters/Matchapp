package android.clase.obligatorio1.activities;


import android.clase.obligatorio1.fragments.FixtureDetailsFragment;
import android.clase.obligatorio1.utils.SingleFragmentActivity;
import android.support.v4.app.Fragment;

/**
 * Created by alfredo on 17/05/15.
 */
public class FixtureDetailsActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new FixtureDetailsFragment();
    }
}
