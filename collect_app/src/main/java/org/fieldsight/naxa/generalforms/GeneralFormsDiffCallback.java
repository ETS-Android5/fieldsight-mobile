package org.fieldsight.naxa.generalforms;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import org.fieldsight.naxa.previoussubmission.model.GeneralFormAndSubmission;

import java.util.List;


public class GeneralFormsDiffCallback extends DiffUtil.Callback {

    private List<GeneralFormAndSubmission> oldGeneralForms;
    private List<GeneralFormAndSubmission> newGeneralForms;

    public GeneralFormsDiffCallback(List<GeneralFormAndSubmission> newGeneralForms, List<GeneralFormAndSubmission> oldGeneralForms) {
        this.newGeneralForms = newGeneralForms;
        this.oldGeneralForms = oldGeneralForms;
    }

    @Override
    public int getOldListSize() {

        return oldGeneralForms.size();
    }

    @Override
    public int getNewListSize() {

        return newGeneralForms.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldGeneralForms.get(oldItemPosition).getGeneralForm().getFsFormId()
                .equals(newGeneralForms.get(newItemPosition).getGeneralForm().getFsFormId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldGeneralForms.get(oldItemPosition).equals(newGeneralForms.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
