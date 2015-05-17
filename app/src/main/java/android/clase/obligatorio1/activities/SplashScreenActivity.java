package android.clase.obligatorio1.activities;

import android.clase.obligatorio1.fragments.SplashScreenFragment;
import android.clase.obligatorio1.utils.SingleFragmentActivity;
import android.support.v4.app.Fragment;

/**
 * created by Alfredo El Ters and Mathias Cabano on 02/05/15.
 */
public class SplashScreenActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new SplashScreenFragment();
    }
}
