package org.odk.collect.naxa.common;

import org.odk.collect.android.R;

public class Constant {

    public static int selectedFragmentId;
    public static String EXTRA_RECEIVER = "extra_receiver";
    public static final String KEY_ANIM_TYPE = "anim_type";
    public static final String KEY_TITLE = "anim_title";

    public enum TransitionType {
        ExplodeJava, ExplodeXML
    }

    public static class Notification {
        public static final int FOREGROUND_SERVICE = 123;
    }

    public static class PrefKey {
        public static final String token = "token";
    }

    public static class Key {
        public static final int RC_CAMERA = 1234;
        public static final int RC_STORAGE = 1235;
        public static final int RC_LOCATION = 1236;
        public static final int GEOPOINT_RESULT_CODE = 1994;
    }

    public final static class ANIM {
        public final static int fragmentEnterAnimation = R.anim.pop_enter;
        public final static int fragmentExitAnimation = R.anim.pop_exit;
    }

    public static final class NotificationEvent {
        public static final String SINGLE_STAGE_DEPLOYED = "deploy_ms";
        public static final String SINGLE_STAGED_FORM_DEPLOYED = "deploy_ss";
        public static final String ALL_STAGE_DEPLOYED = "deploy_all";
    }

    public static final class FormType {
        public static final String SCHEDULE = "schedule_form";
        public static final String STAGED = "staged_form";
        public static final String GENERAl = "general_form";
        public static final String SURVEY = "survey_form";
    }

    public final static class FormDeploymentFrom {
        public final static String PROJECT = "project";
        public final static String SITE = "site";
    }

    public final static class SiteStatus {
        public static final int IS_UNVERIFIED_SITE = 0;
        public static final int IS_OFFLINE_SITE_SYNCED = 1;
        public static final int IS_VERIFIED_BUT_UNSYNCED = 2;
        public static final int IS_FINALIZED = 3;

        public static final String NEEDS_SYNC = "1";
        public static final String SYNCNED = "1";

    }

    public final static class BundleKey {
        public static final String IS_DEPLOYED_FROM_PROJECT = "is_project";
    }


    public static final String EXTRA_OBJECT = "extra_object";
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_MESSAGE = "extra_msg";
    public static final String EXTRA_PROJECT_ID = "extra_msg";

    public final static class DownloadUID {
        public static final int PROJECT_SITES = 1;
        public static final int ODK_FORMS = 2;
        public static final int GENERAL_FORMS = 3;
        public static final int SCHEDULED_FORMS = 4;
        public static final int STAGED_FORMS = 5;
        public static final int PROJECT_CONTACTS = 6;
    }

    public final static class DownloadStatus {
        public static final int PENDING = 1;
        public static final int FAILED = 2;
        public static final int RUNNING = 3;
        public static final int COMPLETED = 4;
    }
}
