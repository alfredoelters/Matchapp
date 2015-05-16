package android.clase.obligatorio1.activities;

import android.app.Fragment;
import android.clase.obligatorio1.fragments.HomeFragment;
import android.clase.obligatorio1.utils.SingleFragmentActivity;


public class HomeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new HomeFragment();
    }
}
