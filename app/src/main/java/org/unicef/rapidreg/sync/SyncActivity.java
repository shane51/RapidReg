package org.unicef.rapidreg.sync;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseActivity;
import org.unicef.rapidreg.childcase.CaseFeature;

public class SyncActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, new SyncFragment(), null).commit();
        drawer.closeDrawer(GravityCompat.START);

        toolbar.setTitle(R.string.sync);
        navigationView.setCheckedItem(R.id.nav_sync);
        navigationView.setItemTextColor(syncColor);
    }

    @Override
    protected void navSyncAction() {

    }

    @Override
    protected void navCaseAction() {
        intentSender.showCasesActivity(this, false);
    }

    @Override
    protected void navTracingAction() {
        intentSender.showTracingActivity(this);
    }

    @Override
    protected void processBackButton() {
        intentSender.showCasesActivity(this, true);
    }
}
