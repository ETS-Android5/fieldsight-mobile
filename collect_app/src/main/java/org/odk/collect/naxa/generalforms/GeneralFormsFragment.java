package org.odk.collect.naxa.generalforms;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.FieldSightFormListFragment;
import org.odk.collect.naxa.common.OnFormItemClickListener;
import org.odk.collect.naxa.common.RecyclerViewEmptySupport;
import org.odk.collect.naxa.common.SharedPreferenceUtils;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.common.utilities.FlashBarUtils;
import org.odk.collect.naxa.generalforms.data.GeneralForm;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.site.FragmentHostActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.naxa.common.Constant.FormDeploymentFrom.PROJECT;

public class GeneralFormsFragment extends FieldSightFormListFragment implements OnFormItemClickListener<GeneralForm> {

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SyncCommentLifecycleObserver syncCommentLifecycleObserver;

    private GeneralFormViewModel viewModel;

    @BindView(R.id.android_list)
    RecyclerViewEmptySupport recyclerView;

    @BindView(R.id.root_layout_general_form_frag)
    LinearLayout rootLayout;

    @BindView(R.id.root_layout_empty_layout)
    View emptyLayout;

    Unbinder unbinder;
    private GeneralFormsAdapter generalFormsAdapter;

    private Site loadedSite;

    public static GeneralFormsFragment newInstance(Site loadedSite) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        GeneralFormsFragment generalFormsFragment = new GeneralFormsFragment();
        generalFormsFragment.setArguments(bundle);
        return generalFormsFragment;

    }

    public GeneralFormsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.general_forms_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        viewModel = FragmentHostActivity.obtainViewModel(getActivity());

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();


        viewModel.loadGeneralForms(true, loadedSite.getId())
                .observe(this, generalForms -> {
                    Timber.i("General forms data has been changed");
                    generalFormsAdapter.updateList(generalForms);


                });
    }

    private void setupListAdapter() {

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        generalFormsAdapter = new GeneralFormsAdapter(new ArrayList<>(0), this);
        recyclerView.setEmptyView(emptyLayout,
                getString(R.string.empty_message, "general forms"),
                new RecyclerViewEmptySupport.OnEmptyLayoutClickListener() {
                    @Override
                    public void onRetryButtonClick() {
                        viewModel.loadGeneralForms(true, loadedSite.getId());
                    }
                });
        recyclerView.setAdapter(generalFormsAdapter);
    }

    @Override
    public void onGuideBookButtonClicked(GeneralForm generalForm, int position) {

    }

    @Override
    public void onFormItemClicked(GeneralForm generalForm) {
        String submissionUrl = generateSubmissionUrl(PROJECT, loadedSite.getProject(), generalForm.getFsFormId());
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_URL, submissionUrl);

        fillODKForm(generalForm.getIdString());
    }

    @Override
    public void onFormItemLongClicked(GeneralForm generalForm) {

    }

    @Override
    public void onFormHistoryButtonClicked(GeneralForm generalForm) {

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DataSyncEvent event) {

        if (!isAdded() || getActivity() == null) {
            //Fragment is not added
            return;
        }


        Timber.i(event.toString());
        switch (event.getEvent()) {
            case DataSyncEvent.EventStatus.EVENT_START:
                FlashBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_start_message), true);
                break;
            case DataSyncEvent.EventStatus.EVENT_END:
                FlashBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_end_message), false);
                break;
            case DataSyncEvent.EventStatus.EVENT_ERROR:
                FlashBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_error_message), false);
                break;
        }
    }

}
