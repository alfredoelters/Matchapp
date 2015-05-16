package android.clase.obligatorio1.activities;

import android.app.Fragment;
import android.clase.obligatorio1.R;
import android.clase.obligatorio1.fragments.HomeFragment;
import android.clase.obligatorio1.utils.SingleFragmentActionBarActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class HomeActivity extends SingleFragmentActionBarActivity {

    @Override
    protected Fragment createFragment() {
        return new HomeFragment();
    }
}
