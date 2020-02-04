package org.fieldsight.naxa.v3.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.bcss.collect.android.R;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.onboarding.SyncableItem;
import org.fieldsight.naxa.v3.network.Syncable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static org.fieldsight.naxa.common.ViewUtils.loadImageWithFallback;


public class ProjectSyncViewholder extends RecyclerView.ViewHolder {
    @BindView(R.id.primary_text)
    TextView primaryText;

    @BindView(R.id.sub_text)
    TextView textView;

    @BindView(R.id.tv_synced_date)
    TextView tvSyncedDate;

    @BindView(R.id.iv_card_status)
    AppCompatImageView imageView;

    @BindView(R.id.iv_project_thumbnail)
    ImageView ivThumbnail;

    @BindView(R.id.tv_regions)
    TextView tvRegions;

    @BindView(R.id.tv_submissions)
    TextView tvSubmissions;

    @BindView(R.id.tv_sites)
    TextView tvSites;

    @BindView(R.id.tv_users)
    TextView tvUsers;

    @BindView(R.id.tv_label_regions)
    TextView regionLabel;

    @BindView(R.id.tv_label_submission)
    TextView submissionLabel;

    @BindView(R.id.tv_label_sites)
    TextView sitesLabel;

    @BindView(R.id.tv_label_users)
    TextView userLabel;

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;

    @BindView(R.id.tv_downloading)
    TextView tvDownloading;

    @BindView(R.id.ll_downloading_section)
    LinearLayout downloadingSection;

    public ProjectSyncViewholder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindView(Project project, boolean allTrue, List<Syncable> syncableList) {
        primaryText.setText(project.getName());
        textView.setText(String.format("%s", project.getOrganizationName()));
        tvSyncedDate.setText(project.getStatusMessage());
        Timber.i("project image = %s", project.getUrl());
        Glide.with(itemView.getContext()).load(project.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivThumbnail);
        tvRegions.setText(String.format(Locale.ENGLISH, "%d", project.getTotalRegions()));
        tvUsers.setText(String.format(Locale.ENGLISH, "%d", project.getTotalUsers()));
        tvSubmissions.setText(String.format(Locale.ENGLISH, "%d", project.getTotalSubmissions()));
        tvSites.setText(String.format(Locale.ENGLISH, "%d", project.getTotalSites()));
//        ivCancel.setTag("syncing");

        if (!TextUtils.isEmpty(project.getUrl())) {
            loadImageWithFallback(itemView.getContext(), project.getUrl()).into(ivThumbnail);
        } else {
            ivThumbnail.setImageResource(R.drawable.fieldsight_logo);
        }

        Timber.i("projectsyncviewholder, project name = %s and hasSyncablelist isnotnull = " + (syncableList != null), project.getName());
        if (syncableList == null) {
            downloadingSection.setVisibility(View.GONE);
        } else {
            if (syncableList != null && syncableList.size() > 0) {
                Timber.i("projectsync, notifying sync for project = " + project.getName());
                updateBySyncStat(syncableList);
            }
        }

    }

    private void updateBySyncStat(List<Syncable> syncableList) {
//        for (String key : syncableMap.keySet()) {
        // key -> projectId
        // 0 -> sites and regions
        // 1 -> forms
        // 2 - education materials
//            List<Syncable> syncableList = syncableMap.get(key);
        Syncable sitesAndRegionsSyncStat = syncableList.get(0);
        Syncable formSyncStat = syncableList.get(1);
        Syncable educationAndMaterialSyncStat = syncableList.get(2);
        if (sitesAndRegionsSyncStat.status == Constant.DownloadStatus.COMPLETED && formSyncStat.status == Constant.DownloadStatus.COMPLETED && educationAndMaterialSyncStat.status == Constant.DownloadStatus.COMPLETED) {
            Timber.i("upddate sync by status, complete");
            ivCancel.setVisibility(View.GONE);
            ivCancel.setTag("synced");
            tvDownloading.setText("Sync complete");
            downloadingSection.setVisibility(View.GONE);
            hasSyncComplete(getLayoutPosition());
        } else if (sitesAndRegionsSyncStat.status == Constant.DownloadStatus.RUNNING || formSyncStat.status == Constant.DownloadStatus.RUNNING || educationAndMaterialSyncStat.status == Constant.DownloadStatus.RUNNING) {
            Timber.i("upddate sync by status, syncing");
            ivCancel.setImageResource(R.drawable.ic_circle_cancel_major_monotone);
            tvDownloading.setText("Syncing project");
            ivCancel.setTag("syncing");
            downloadingSection.setVisibility(View.VISIBLE);
        } else {
            downloadingSection.setVisibility(View.VISIBLE);
            StringBuilder failedSync = new StringBuilder();
            if (sitesAndRegionsSyncStat.status == Constant.DownloadStatus.FAILED) {
                failedSync.append(sitesAndRegionsSyncStat.getTitle() + ", ");
            }
            if (formSyncStat.status == Constant.DownloadStatus.FAILED) {
                failedSync.append(formSyncStat.getTitle() + ", ");
            }

            if (educationAndMaterialSyncStat.status == Constant.DownloadStatus.FAILED) {
                failedSync.append(educationAndMaterialSyncStat.getTitle());
            }

            if (failedSync.length() > 0) {
                Timber.i("upddate sync by status, failed");
                tvDownloading.setText(failedSync.toString() + " failed to sync");
                tvDownloading.setTextColor(Color.parseColor("#FF0000"));
                ivCancel.setImageResource(R.drawable.ic_refresh);
                ivCancel.setTag("retry");
                hasSyncComplete(getLayoutPosition());
            }
        }
    }

//    }

    public void hasSyncComplete(int index) {

    }
}
