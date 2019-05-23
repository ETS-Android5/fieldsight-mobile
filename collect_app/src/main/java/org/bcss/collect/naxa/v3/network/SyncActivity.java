package org.bcss.collect.naxa.v3.network;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.InternetUtils;
import org.bcss.collect.naxa.common.SharedPreferenceUtils;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.sync.ContentDownloadAdapter;
import org.bcss.collect.naxa.sync.DownloadViewModel;
import org.bcss.collect.naxa.v3.adapter.SyncAdapterv3;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.EXTRA_MESSAGE;

public class SyncActivity extends CollectAbstractActivity implements SyncAdapterCallback {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_download_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.download_button)
    Button downloadButton;

    @BindView(R.id.toolbar_message)
    TextView toolbar_message;


    private DisposableObserver<Boolean> connectivityDisposable;
    SyncAdapterv3 adapterv3;
    boolean auto = true;
    HashMap<String, List<Syncable>> syncableMap = null;

    LiveData<List<SyncStat>> syncdata;
    Observer<List<SyncStat>> syncObserver = null;
    boolean syncing = false;
    ArrayList<Project> projectList;
    LiveData<Integer> runningLiveData;
    Observer<Integer> runningLiveDataObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /// getting the selected project list from the projectlist activity
        Timber.i("SyncActivity, alreadySyncing:: " + (Collect.selectedProjectList != null && Collect.selectedProjectList.size() > 0));
        if (Collect.selectedProjectList != null && Collect.selectedProjectList.size() > 0) {
            syncing = true;
            projectList = Collect.selectedProjectList;
            syncableMap = Collect.syncableMap;
        } else {
            Bundle bundle = getIntent().getBundleExtra("params");
            projectList = bundle.getParcelableArrayList("projects");
            auto = bundle.getBoolean("auto", true);
        }

        if (projectList == null || projectList.size() == 0) {
            return;
        }
        setTitle(String.format(Locale.getDefault(), "Projects (%d)", projectList.size()));
        /// create the map of the syncing
        if (syncableMap == null)
            createSyncableList(projectList);

        adapterv3 = new SyncAdapterv3(auto, projectList, syncableMap);
        adapterv3.setAdapterCallback(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterv3);

        findViewById(R.id.download_button).setOnClickListener(v -> {
            ToastUtils.showShortToast("Download starts");
            Intent syncIntent = new Intent(getApplicationContext(), SyncServiceV3.class);
            syncIntent.putParcelableArrayListExtra("projects", projectList);
            syncIntent.putExtra("selection", syncableMap);
            startService(syncIntent);

            Collect.selectedProjectList = projectList;
            Collect.syncableMap = syncableMap;
            enableDisableAdapter(true);
        });

        syncObserver = syncStats -> {
            Timber.i("sync stats size = %d", syncStats.size());
            adapterv3.notifyBySyncStat(syncStats);
        };

        syncdata = SyncLocalSourcev3.getInstance().getAll();
        syncdata.observe(this, syncObserver);

        runningLiveDataObserver = count -> {
            Timber.i("SyncActivity, syncing = " + syncing + " count = %d", count);
            if (count == 0) {
                Timber.i("SyncActivity, enable called");
                enableDisableAdapter(false);
            }
        };
        runningLiveData = SyncLocalSourcev3.getInstance().getCountByStatus(Constant.DownloadStatus.RUNNING);
        runningLiveData.observe(this, runningLiveDataObserver);

        connectivityDisposable = InternetUtils.observeInternetConnectivity(new InternetUtils.OnConnectivityListener() {
            @Override
            public void onConnectionSuccess() {
                toolbar_message.setVisibility(View.GONE);
            }

            @Override
            public void onConnectionFailure() {
                toolbar_message.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCheckComplete() {

            }
        });

        if(syncing) {
            enableDisableAdapter(syncing);
        }
    }

    // this class will manage the sync list to determine which should be synced
    ArrayList<Syncable> createList() {
//        -1 refers here as never started
        ArrayList<Syncable> list = new ArrayList<Syncable>() {{
            add(0, new Syncable("Regions and sites", auto, -1));
            add(1, new Syncable("Forms", auto, -1));
            add(2, new Syncable("Materials", auto, -1));
        }};
        return list;
    }

    void createSyncableList(List<Project> selectedProjectList) {
        syncableMap = new HashMap<>();
        for (Project project : selectedProjectList) {
            syncableMap.put(project.getId(), createList());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestInterrupt(int pos, Project project) {
        DialogFactory.createActionDialog(this, getString(R.string.app_name), "Are you sure you want to remove " + project.getName() + "from download queue ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    syncableMap.remove(project.getId());
                    adapterv3.removeAndNotify(pos);
                    if (adapterv3.getItemCount() > 0)
                        setTitle("Projects (" + adapterv3.getItemCount() + ")");
                    else
                        setTitle("Projects");
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void childDownloadListSelectionChange(Project project, List<Syncable> list) {
//    add this request in download queue
        Timber.i("SyncActivity data = " + readaableSyncParams(project.getName(), list));
        syncableMap.put(project.getId(), list);
    }

    private void enableDisableAdapter(boolean isSyncing) {
        if (isSyncing) {
            adapterv3.disableItemClick();
        } else {
            adapterv3.enableItemClick();
        }
        downloadButton.setEnabled(!isSyncing);
        downloadButton.setBackgroundColor(isSyncing ? getResources().getColor(R.color.disabled_gray) :
                getResources().getColor(R.color.primaryColor));
        downloadButton.setTextColor(getResources().getColor(R.color.white));
        this.syncing = isSyncing;
    }

    private String readaableSyncParams(String projectName, List<Syncable> list) {
        String logString = "";
        for (Syncable syncable : list) {
            logString += "\n title = " + syncable.getTitle() + ", sync = " + syncable.getSync();
        }
        return String.format("%s \n params = %s", projectName, logString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (syncdata != null && syncdata.hasObservers()) {
            syncdata.removeObserver(syncObserver);
        }
        if (connectivityDisposable != null) {
            connectivityDisposable.dispose();
        }

        if (runningLiveData != null && runningLiveData.hasObservers()) {
            runningLiveData.removeObserver(runningLiveDataObserver);
        }
        Timber.i("OnDestroy, isSyncing : " + syncing);
        if (syncing) {
            Collect.syncableMap = syncableMap;
            Collect.selectedProjectList = projectList;
        } else {
            Collect.syncableMap = null;
            Collect.selectedProjectList = null;
        }
    }

}
