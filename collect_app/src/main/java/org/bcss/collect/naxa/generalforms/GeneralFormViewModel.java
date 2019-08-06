package org.bcss.collect.naxa.generalforms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.generalforms.data.GeneralFormRepository;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.previoussubmission.model.GeneralFormAndSubmission;

import java.util.List;

import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.SITE;

public class GeneralFormViewModel extends ViewModel {
    private final GeneralFormRepository repository;


    public GeneralFormViewModel(GeneralFormRepository repository) {
        this.repository = repository;
    }




    @Deprecated
    public LiveData<List<GeneralForm>> getForms(boolean forcedUpdate, Site loadedSite) {
        switch (loadedSite.getGeneralFormDeployedFrom()) {
            case SITE:
                return repository.getBySiteId(forcedUpdate, loadedSite.getId(), loadedSite.getProject());
            case PROJECT:
            default:
                return repository.getByProjectId(forcedUpdate, loadedSite.getProject());

        }
    }

    public LiveData<List<GeneralFormAndSubmission>> getFormsAndSubmission(Site loadedSite) {
        switch (loadedSite.getGeneralFormDeployedFrom()) {
            case SITE:
                return repository.getFormsBySiteId(loadedSite.getId(),loadedSite.getProject());
            case PROJECT:
            default:
                return repository.getFormsByProjectIdId(loadedSite.getProject());

        }
    }
}
