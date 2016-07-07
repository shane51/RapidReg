package org.unicef.rapidreg.sync;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;

public class SyncActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setNavMenuItem(R.id.nav_sync);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, new SyncFragment(), null).commit();
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void navSyncAction() {
    }

    @Override
    protected void navCaseAction() {
        intentSender.showCasesActivity(this, null, false);
    }

    @Override
    protected void navTracingRequestAction() {
        intentSender.showTracingRequestActivity(this);
    }

    @Override
    protected void processBackButton() {
        logOut(this);
    }

    @Override
    public boolean isInDetailMode() {
        return false;
    }

    @Override
    public boolean isInEditMode() {
        return false;
    }
}
