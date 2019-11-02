package org.fieldsight.naxa.generalforms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.BaseFormListFragment;
import org.fieldsight.naxa.common.OnFormItemClickListener;
import org.fieldsight.naxa.common.RecyclerViewEmptySupport;
import org.fieldsight.naxa.common.SharedPreferenceUtils;
import org.fieldsight.naxa.common.ViewModelFactory;
import org.fieldsight.naxa.common.event.DataSyncEvent;
import org.fieldsight.naxa.common.utilities.SnackBarUtils;
import org.fieldsight.naxa.educational.EducationalMaterialActivity;
import org.fieldsight.naxa.generalforms.data.GeneralForm;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.previoussubmission.model.GeneralFormAndSubmission;
import org.fieldsight.naxa.site.FragmentHostActivity;
import org.fieldsight.naxa.submissions.PreviousSubmissionListActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.application.Collect;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;
import static org.fieldsight.naxa.common.SharedPreferenceUtils.isFormSaveCacheSafe;
import static org.fieldsight.naxa.generalforms.data.FormType.TABLE_GENERAL_FORM;

public class GeneralFormsFragment extends BaseFormListFragment implements OnFormItemClickListener<GeneralForm> {

    @Inject
    ViewModelFactory viewModelFactory;

    private GeneralFormViewModel viewModel;

    @BindView(R.id.android_list)
    RecyclerViewEmptySupport recyclerView;

    @BindView(R.id.root_layout_general_form_frag)
    LinearLayout rootLayout;

    @BindView(R.id.root_layout_empty_layout)
    LinearLayout emptyLayout;


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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.general_forms_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        viewModel = FragmentHostActivity.obtainViewModel(requireActivity());

        setToolbarText();
        return rootView;
    }

    private void setToolbarText() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_general_forms);
        toolbar.setSubtitle(loadedSite.getName());
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

        viewModel.getFormsAndSubmission(loadedSite.getId(), loadedSite.getProject())
                .observe(this, new Observer<List<GeneralFormAndSubmission>>() {
                    @Override
                    public void onChanged(@Nullable List<GeneralFormAndSubmission> generalFormAndSubmissions) {

                        if (generalFormAndSubmissions == null) {
                            return;
                        }


                        generalFormsAdapter.updateList(generalFormAndSubmissions);

                    }
                });
    }


    private void setupListAdapter() {

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        generalFormsAdapter = new GeneralFormsAdapter(new ArrayList<>(0), this);

        recyclerView.setEmptyView(emptyLayout,
                getString(R.string.empty_message, "general FORMS"),
                () -> {

                });
        recyclerView.setAdapter(generalFormsAdapter);

    }

    @Override
    public void onGuideBookButtonClicked(GeneralForm generalForm, int position) {
        EducationalMaterialActivity.startFromGeneral(getActivity(), generalFormsAdapter.getAll(), position);
    }

    @Override
    public void onFormItemClicked(GeneralForm generalForm, int position) {

        String formDeployedFrom = generalForm.getSiteId() != null ? Constant.FormDeploymentFrom.SITE : Constant.FormDeploymentFrom.PROJECT;

        String submissionUrl = generateSubmissionUrl(formDeployedFrom, loadedSite.getId(), generalForm.getFsFormId());
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_URL, submissionUrl);
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_SITE_ID, loadedSite.getId());

        if (isFormSaveCacheSafe(submissionUrl, loadedSite.getId())) {
            fillODKForm(generalForm.getIdString());
        }
    }

    @Override
    public void onFormItemLongClicked(GeneralForm generalForm) {

    }

    @Override
    public void onFormHistoryButtonClicked(GeneralForm generalForm) {
        PreviousSubmissionListActivity.start(getActivity(),
                generalForm.getFsFormId(),
                generalForm.getName(),
                generalForm.getName(),
                null,
                loadedSite.getId(),
                null,
                TABLE_GENERAL_FORM
        );
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
                SnackBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_start_message), true);
                break;
            case DataSyncEvent.EventStatus.EVENT_END:
                SnackBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_end_message), false);
                break;
            case DataSyncEvent.EventStatus.EVENT_ERROR:
                SnackBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_error_message), false);
                break;
        }
    }

}
