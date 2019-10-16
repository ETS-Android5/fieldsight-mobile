package org.fieldsight.naxa.generalforms.data;

import com.google.gson.annotations.SerializedName;

public class FormType {

    public static final String TABLE_SCHEDULE_FORM = "schedule_forms";
    public static final String TABLE_GENERAL_FORM = "general_forms";
    public final static String TABLE_STAGE_PARENT = "stage";
    public static final String TABLE_SUBSTAGE = "sub_stage_child";
    public static final String TABLE_SURVEY_FORM = "survey_form";


    @SerializedName("is_staged")
    private final boolean isStaged;

    @SerializedName("is_survey")
    private final boolean isSurvey;

    @SerializedName("is_scheduled")
    private final boolean isScheduled;

    public boolean isStaged() {
        return isStaged;
    }

    public boolean isSurvey() {
        return isSurvey;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public FormType(boolean isStaged, boolean isSurvey, boolean isScheduled) {
        this.isStaged = isStaged;
        this.isSurvey = isSurvey;
        this.isScheduled = isScheduled;
    }

    public String getFormTableName() {
        String name;

        if (isScheduled) {
            name = TABLE_SCHEDULE_FORM;
        } else if (isStaged) {
            name = TABLE_SUBSTAGE;
        } else if (isSurvey) {
            name = TABLE_SURVEY_FORM;
        } else {
            name = TABLE_GENERAL_FORM;
        }

        return name;
    }
}
