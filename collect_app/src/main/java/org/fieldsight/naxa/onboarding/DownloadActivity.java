package org.fieldsight.naxa.onboarding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.DownloadUID.ALL_FORMS;
import static org.fieldsight.naxa.common.Constant.DownloadUID.EDU_MATERIALS;
import static org.fieldsight.naxa.common.Constant.DownloadUID.PREV_SUBMISSION;
import static org.fieldsight.naxa.common.Constant.DownloadUID.PROJECT_CONTACTS;
import static org.fieldsight.naxa.common.Constant.DownloadUID.PROJECT_SITES;
import static org.fieldsight.naxa.common.Constant.DownloadUID.SITE_TYPES;
import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

@Deprecated
public class DownloadActivity extends CollectAbstractActivity implements DownloadView {


    @BindView(R.id.toggle_button)
    Button toggleButton;

    @BindView(R.id.download_button)
    Button downloadButton;

    @BindView(R.id.activity_download_recycler_view)
    public RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    private DownloadListAdapter downloadListAdapter;
    private DownloadPresenter downloadPresenter;

    public static void start(Activity context, int outOfSyncUid) {
        Intent intent = new Intent(context, DownloadActivity.class);
        intent.putExtra(EXTRA_OBJECT, outOfSyncUid);
        context.startActivity(intent);
    }

    public static void runAll(Context context) {
        Intent intent = new Intent(context, DownloadActivity.class);
        intent.putExtra("run_all", true);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        downloadPresenter = new DownloadPresenterImpl(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_OBJECT)) {
                int uid = bundle.getInt(EXTRA_OBJECT);
                downloadPresenter.startDownload(uid);
            } else if (bundle.containsKey("run_all")) {
                downloadPresenter.startDownload(ALL_FORMS);
                downloadPresenter.startDownload(PROJECT_SITES);
                downloadPresenter.startDownload(SITE_TYPES);
                downloadPresenter.startDownload(EDU_MATERIALS);
                downloadPresenter.startDownload(PROJECT_CONTACTS);
                downloadPresenter.startDownload(PREV_SUBMISSION);
            }
        }

        setupToolbar();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        downloadListAdapter = new DownloadListAdapter(new ArrayList<>(0));
        recyclerView.setAdapter(downloadListAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_downloads));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy() %s",isFinishing());
    }


    @Override
    protected void onPause() {
        super.onPause();
        Timber.i("onPause() ");
    }

    @Override
    public void addAdapter(List<SyncableItem> syncableItems) {

        Observable<Integer> notificaitonCountForm = FieldSightNotificationLocalSource.getInstance().anyFormsOutOfSync().toObservable();
        Observable<Integer> notificaitonCountSites = FieldSightNotificationLocalSource.getInstance().anyProjectSitesOutOfSync().toObservable();
        Observable<Integer> notificaitonCountPreviousSubmission = FieldSightNotificationLocalSource.getInstance().anyFormStatusChangeOutOfSync().toObservable();


        Observable.just(syncableItems)
                .flatMapIterable((Function<List<SyncableItem>, Iterable<SyncableItem>>) syncableItems1 -> syncableItems1)
                .flatMap(new Function<SyncableItem, ObservableSource<SyncableItem>>() {
                    @Override
                    public ObservableSource<SyncableItem> apply(SyncableItem syncableItem) {
                        return notificaitonCountForm
                                .map(new Function<Integer, SyncableItem>() {
                                    @Override
                                    public SyncableItem apply(Integer integer) {
                                        switch (syncableItem.getUid()) {
                                            case Constant.DownloadUID.ALL_FORMS:
                                                syncableItem.setOutOfSync(integer > 0);
                                                break;
                                        }

                                        return syncableItem;
                                    }
                                });
                    }
                })
                .flatMap(new Function<SyncableItem, ObservableSource<SyncableItem>>() {
                    @Override
                    public ObservableSource<SyncableItem> apply(SyncableItem syncableItem) {
                        return notificaitonCountSites
                                .map(new Function<Integer, SyncableItem>() {
                                    @Override
                                    public SyncableItem apply(Integer integer) {
                                        switch (syncableItem.getUid()) {
                                            case Constant.DownloadUID.PROJECT_SITES:
                                                syncableItem.setOutOfSync(integer > 0);
                                                break;
                                        }

                                        return syncableItem;
                                    }
                                });
                    }
                })
                .flatMap(new Function<SyncableItem, ObservableSource<SyncableItem>>() {
                    @Override
                    public ObservableSource<SyncableItem> apply(SyncableItem syncableItem) {
                        return notificaitonCountPreviousSubmission
                                .map(new Function<Integer, SyncableItem>() {
                                    @Override
                                    public SyncableItem apply(Integer integer) {
                                        switch (syncableItem.getUid()) {
                                            case Constant.DownloadUID.PREV_SUBMISSION:
                                                syncableItem.setOutOfSync(integer > 0);
                                                break;
                                        }

                                        return syncableItem;
                                    }
                                });
                    }
                })
//                .flatMap(new Function<SyncableItem, ObservableSource<SyncableItem>>() {
//                    @Override
//                    public ObservableSource<SyncableItem> apply(SyncableItem syncableItem) throws Exception {
//                        boolean hasAPIRunning = ServiceGenerator.getRunningAPICount() > 0;
//                        if(!hasAPIRunning){
//                            SyncRepository.getInstance().setError(syncableItem.getUid());
//                        }
//                        return Observable.just(syncableItem);
//                    }
//                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<SyncableItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<SyncableItem> syncableItems) {
                        downloadListAdapter.updateList(syncableItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });

    }

    @Override
    public LifecycleOwner getLifeCycleOwner() {
        return this;
    }


    @OnClick({R.id.toggle_button, R.id.download_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toggle_button:
                downloadPresenter.onToggleButtonClick(downloadListAdapter.getList());
                break;
            case R.id.download_button:
                downloadPresenter.onDownloadButtonClick(downloadListAdapter.getList());
                break;
        }
    }


    protected int getCheckedCount() {
        return downloadListAdapter.getSelectedItemsCount();
    }


}
