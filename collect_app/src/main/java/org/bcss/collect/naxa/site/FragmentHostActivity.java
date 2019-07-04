package org.bcss.collect.naxa.site;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.common.InternetUtils;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.common.utilities.FlashBarUtils;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.generalforms.GeneralFormViewModel;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.notificationslist.NotificationListActivity;
import org.bcss.collect.naxa.preferences.SettingsActivity;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.sync.ContentDownloadActivity;
import org.bcss.collect.naxa.v3.network.SyncActivity;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.ALL_FORMS;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class FragmentHostActivity extends CollectAbstractActivity {

    Site loadedSite = null;
    Toolbar toolbar;

    public static void start(Context context, Site site) {
        Intent intent = new Intent(context, FragmentHostActivity.class);
        intent.putExtra(EXTRA_OBJECT, site);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_dashboard);

        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            ToastUtils.showShortToast(getString(R.string.dialog_unexpected_error_title));
            finish();
            return;
        }

        loadedSite = extras.getParcelable(EXTRA_OBJECT);
        bindUI();
        setupToolbar();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, SiteDashboardFragment.newInstance(loadedSite), "frag0")
                .commit();


        FieldSightNotificationLocalSource.getInstance()
                .isSiteNotSynced(loadedSite.getId(), loadedSite.getProject())
                .observe(this, integer -> {
                    if (integer != null && integer > 0) {

                        FlashBarUtils.showOutOfSyncMsg(ALL_FORMS, FragmentHostActivity.this, "Form(s) information is out of sync");
                    }
                });


    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void bindUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;

            case R.id.action_notificaiton:
                startActivity(new Intent(this, NotificationListActivity.class));

                break;
            case R.id.action_app_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_logout:
//                showProgress();
                InternetUtils.checkInterConnectivity(new InternetUtils.OnConnectivityListener() {
                    @Override
                    public void onConnectionSuccess() {
                        FieldSightUserSession.showLogoutDialog(FragmentHostActivity.this);
                    }

                    @Override
                    public void onConnectionFailure() {
                        FieldSightUserSession.stopLogoutDialog(FragmentHostActivity.this);
                    }

                    @Override
                    public void onCheckComplete() {
                        hideProgress();
                    }
                });
                break;
            case R.id.action_refresh:
                ProjectLocalSource.getInstance().getProjectById(loadedSite.getProject()).observe(this, new Observer<Project>() {
                    @Override
                    public void onChanged(@Nullable Project project) {
                        if (project != null) {
                            Bundle bundle = new Bundle();
                            ArrayList<Project> projectArrayList = new ArrayList<>();
                            projectArrayList.add(project);
                            bundle.putParcelableArrayList("projects", projectArrayList);
                            bundle.getBoolean("auto", true);
                            startActivity(new Intent(FragmentHostActivity.this, SyncActivity.class)
                                    .putExtra("params", bundle));
                        }
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static GeneralFormViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(GeneralFormViewModel.class);
    }


}
