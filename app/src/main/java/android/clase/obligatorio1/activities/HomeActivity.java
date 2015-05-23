package android.clase.obligatorio1.activities;


import android.clase.obligatorio1.fragments.HomeFragment;
import android.clase.obligatorio1.utils.SingleFragmentActivity;
import android.support.v4.app.Fragment;


public class HomeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new HomeFragment();
    }

}
