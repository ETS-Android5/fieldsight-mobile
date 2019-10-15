package org.fieldsight.naxa.site;

import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.anim.FlipAnimator;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.login.model.SiteBuilder;

import java.util.ArrayList;
import java.util.List;

public class SiteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SURVEY_FORM = 0, VIEW_TYPE_SITE = 1;

    private final List<Site> siteList;

    private final SparseBooleanArray selectedItems;
    private final SparseBooleanArray animationItemsIndex;
    private final SiteListAdapter.SiteListAdapterListener listener;
    private static int currentSelectedIndex = -1;
    private boolean reverseAllAnimations;

    SiteListAdapter(List<Site> sitelist, SiteListAdapter.SiteListAdapterListener listener) {

        this.listener = listener;
        this.selectedItems = new SparseBooleanArray();
        this.animationItemsIndex = new SparseBooleanArray();

        ArrayList<Site> surveyFormAndSites = new ArrayList<>();
        surveyFormAndSites.add(new SiteBuilder()
                .setName("project_survey")
                .setId("0")
                .createSite());
        surveyFormAndSites.addAll(sitelist);

        this.siteList = surveyFormAndSites;



    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View itemView = null;

        switch (viewType) {
            case VIEW_TYPE_SURVEY_FORM:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.survey_list_item, parent, false);
                holder = new SurveyViewHolder(itemView);
                break;
            case VIEW_TYPE_SITE:
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.site_list_item, parent, false);
                holder = new SiteViewHolder(itemView);
                break;
        }
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SURVEY_FORM:
                break;
            default:
                SiteViewHolder siteViewHolder = (SiteViewHolder) holder;
                Site site = siteList.get(holder.getAdapterPosition());
                siteViewHolder.siteName.setText(site.getName());
                siteViewHolder.iconText.setText(site.getName().substring(0, 1));
                siteViewHolder.identifier.setText(site.getIdentifier());
                siteViewHolder.message.setText(site.getAddress());
                siteViewHolder.imgProfile.setImageResource(R.drawable.circle_blue);

                applyIconAnimation(siteViewHolder, position);
                applyOffilineSiteTag(siteViewHolder, site);
                break;
        }


    }


    private void applyIconAnimation(SiteViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(holder.iconBack.getContext(), holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }

            holder.rootLayout.setActivated(true);
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(holder.iconBack.getContext(), holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }


            holder.rootLayout.setActivated(false);
        }
    }

    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }


    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return VIEW_TYPE_SURVEY_FORM;
            default:
                return VIEW_TYPE_SITE;
        }
    }

    void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public List<Site> getAll() {
        return siteList;
    }

    public class SurveyViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout rootLayout;

        SurveyViewHolder(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.root_layout_survey_form_list_item);
            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSurveyFormClicked();
                }
            });

        }
    }

    public class SiteViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private final TextView siteName, identifier, message, iconText, offlinetag;
        private final ImageView imgProfile;
        private final RelativeLayout iconContainer, iconBack, iconFront;
        private final View rootLayout;

        SiteViewHolder(View view) {
            super(view);
            siteName = view.findViewById(R.id.tv_site_name);
            identifier = view.findViewById(R.id.tv_identifier);
            message = view.findViewById(R.id.txt_secondary);
            iconText = view.findViewById(R.id.icon_text);
            offlinetag = view.findViewById(R.id.timestamp);
            iconBack = view.findViewById(R.id.icon_back);
            iconFront = view.findViewById(R.id.icon_front);
            imgProfile = view.findViewById(R.id.icon_profile);
            iconContainer = view.findViewById(R.id.icon_container);
            rootLayout = view.findViewById(R.id.root_layout_message_list_row);

            rootLayout.setOnLongClickListener(this);
            rootLayout.setOnClickListener(this);

        }

        @Override
        public boolean onLongClick(View view) {

            if (Constant.SiteStatus.IS_ONLINE != siteList.get(getAdapterPosition()).getIsSiteVerified()
                    && Constant.SiteStatus.IS_EDITED != siteList.get(getAdapterPosition()).getIsSiteVerified()) {

                listener.onRowLongClicked(getAdapterPosition());
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                return true;
            }

            return false;
        }

        @Override
        public void onClick(View v) {
            listener.onUselessLayoutClicked(siteList.get(getAdapterPosition()));
        }
    }


    private void applyOffilineSiteTag(SiteViewHolder holder, Site siteLocationPojo) {
        boolean isChecked = selectedItems.get(holder.getAdapterPosition(), false);
        boolean isUnVerifiedSite = siteLocationPojo.getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE;
        boolean isEditedSite = siteLocationPojo.getIsSiteVerified() == Constant.SiteStatus.IS_EDITED;
        holder.offlinetag.setVisibility(View.GONE);

        if (isChecked) {
//            holder.siteName.setTypeface(null, Typeface.BOLD);
//            holder.identifier.setTypeface(null, Typeface.BOLD);
            holder.siteName.setTextColor(ContextCompat.getColor(holder.siteName.getContext(), R.color.from));
            holder.identifier.setTextColor(ContextCompat.getColor(holder.siteName.getContext(), R.color.subject));
        }


        if (isEditedSite) {
            holder.offlinetag.setVisibility(View.VISIBLE);
            holder.offlinetag.setText("Edited Site");
        }

        if (isUnVerifiedSite) {
            holder.offlinetag.setVisibility(View.VISIBLE);
            holder.offlinetag.setText("Offline Site");
//            holder.siteName.setTypeface(null, Typeface.NORMAL);
//            holder.identifier.setTypeface(null, Typeface.NORMAL);
            holder.siteName.setTextColor(ContextCompat.getColor(holder.siteName.getContext(), R.color.subject));
            holder.identifier.setTextColor(ContextCompat.getColor(holder.siteName.getContext(), R.color.message));
        }


    }


    public void updateList(List<Site> newList) {

        ArrayList<Site> surveyFormAndSites = new ArrayList<>();
        surveyFormAndSites.add(new SiteBuilder()
                .setName("project_survey")
                .setId("0")
                .createSite());
        surveyFormAndSites.addAll(newList);

        this.siteList.clear();
        this.siteList.addAll(surveyFormAndSites);


        notifyDataSetChanged();


    }

    public ArrayList<Site> getSelected() {
        ArrayList<Site> items = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(siteList.get(selectedItems.keyAt(i)));
        }

        return items;
    }


    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {

            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public interface SiteListAdapterListener {
        void onIconClicked(int position);

        void onRowLongClicked(int position);

        void onUselessLayoutClicked(Site site);

        void onSurveyFormClicked();
    }
}
