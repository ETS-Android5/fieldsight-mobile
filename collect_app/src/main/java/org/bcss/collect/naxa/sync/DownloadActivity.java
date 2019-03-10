package org.bcss.collect.naxa.sync;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.bcss.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.naxa.OnItemClickListener;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.ViewModelFactory;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.DownloadStatus.PENDING;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.PROJECT_SITES;

@Deprecated
public class DownloadActivity extends CollectAbstractActivity implements OnItemClickListener<DownloadableItem> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_download_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toggle_button)
    Button toggleButton;
    @BindView(R.id.download_button)
    Button downloadButton;

    private DownloadListAdapterNew adapter;
    private DownloadViewModel viewModel;



    public static void start(Context context){
        Intent intent = new Intent(context,DownloadActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);

        setupToolbar();
        setupRecyclerView();
        setupViewModel();


        DownloadableItemLocalSource.getINSTANCE()
                .save(getData())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Timber.i("Insert complete on sync table");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Insert failed on sync table reason: %s", e.getMessage());
                        e.printStackTrace();
                    }
                });


        DownloadableItemLocalSource.getINSTANCE().getAll()
                .observe(this, syncs -> adapter.updateList(syncs));


        DownloadableItemLocalSource.getINSTANCE()
                .selectedItemCountLive()
                .observe(this, integer -> {
                    if (integer == null) return;
                    if (integer > 0) {
                        toggleButton.setText(getString(R.string.clear_all));
                        downloadButton.setEnabled(true);
                    } else {
                        downloadButton.setEnabled(false);
                        toggleButton.setText(getString(R.string.select_all));
                    }
                });

        DownloadableItemLocalSource.getINSTANCE()
                .runningItemCountLive()
                .observe(this, integer -> {
                    if (integer == null) return;
                    if (integer > 0) {
                        downloadButton.setText(R.string.stop_download);
                    } else {
                        downloadButton.setText(R.string.download);
                    }
                });
    }

    private void setupViewModel() {
        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(DownloadViewModel.class);
    }

    @OnClick({R.id.toggle_button, R.id.download_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toggle_button:
                DownloadableItemLocalSource.getINSTANCE()
                        .toggleAllChecked()
                        .subscribeOn(Schedulers.io())
                        .subscribe(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                Timber.i("Update completed on sync table");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e("Update failed on sync table reason: %s", e.getMessage());
                                e.printStackTrace();
                            }
                        });
                break;
            case R.id.download_button:

                if (getString(R.string.download).contentEquals(downloadButton.getText())) {
                    runDownload();
                } else if (getString(R.string.stop_download).contentEquals(downloadButton.getText())) {
                    stopDownload();
                }


                break;
        }
    }

    private void stopDownload() {
        viewModel.cancelAllTask();




    }

    private void runDownload() {
        DownloadableItemLocalSource.getINSTANCE().getAllChecked()
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<List<DownloadableItem>>() {
                    @Override
                    public void onSuccess(List<DownloadableItem> downloadableItems) {
                        viewModel.queueSyncTask(downloadableItems);
                        Timber.i("Select completed on sync table");


                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Select failed on sync table reason: %s", e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setOnItemClickListener(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.setOnItemClickListener(null);
    }

    private DownloadableItem[] getData() {

        return new DownloadableItem[]{
                new DownloadableItem(PROJECT_SITES, PENDING, "Project and sites", "Downloads your assigned project and sites"),
                new DownloadableItem(Constant.DownloadUID.ALL_FORMS, PENDING, "Forms", "Downloads all forms for assigned sites"),
                new DownloadableItem(Constant.DownloadUID.SITE_TYPES, PENDING, "Site type(s)", "Download site types to filter staged forms"),
                new DownloadableItem(Constant.DownloadUID.EDU_MATERIALS, PENDING, "Educational Materials", "Download educational attached for form(s)"),
                new DownloadableItem(Constant.DownloadUID.PROJECT_CONTACTS, PENDING, "Project Contact(s)", "Download contact information for people associated with your project"),
                new DownloadableItem(Constant.DownloadUID.PREV_SUBMISSION, PENDING, "Previous Submissions", "Download previous submission(s) for forms"),
        };
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_downloads));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new DownloadListAdapterNew(new ArrayList<>(0));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
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
    public void onClickPrimaryAction(DownloadableItem downloadableItem) {
        DownloadableItemLocalSource.getINSTANCE()
                .toggleSingleItem(downloadableItem)
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Timber.i("Update completed on downloadableItem table");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Update failed on downloadableItem table reason: %s", e.getMessage());
                        e.printStackTrace();
                    }
                })
        ;
    }

    @Override
    public void onClickSecondaryAction(DownloadableItem downloadableItem) {
//        DownloadableItemLocalSource.getINSTANCE().toggleSingleItem(downloadableItem);
        DownloadableItemLocalSource.getINSTANCE().markAsPending(downloadableItem.getUid());
    }
}
