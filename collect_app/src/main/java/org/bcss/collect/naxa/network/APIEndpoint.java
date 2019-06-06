package org.bcss.collect.naxa.network;

import java.util.HashMap;

public class APIEndpoint {

    public final static int NEW_RECORD_CREATED = 201;

//    public static final String BASE_URL = "https://app.fieldsight.org";
  public static final String BASE_URL = "https://fieldsight.naxa.com.np";

//  public static final String BASE_URL = "http://192.168.1.107:8001";
//  public static final String BASE_URL = "http://192.168.1.251:8001";

    public static final String PASSWORD_RESET = "/accounts/password/reset/";
    public static final String USER_LOGIN = "/users/api/get-auth-token/";

    public static final String FORM_SUBMISSION_PAGE = "/forms/submission/";
    public static final String GET_SITE_TYPES = "/fieldsight/api/site-types/";
    public static final String GET_PROJECT_STAGES = "/forms/api/stage/1/{project_id}";
    public static final String GET_PROJECT_STAGES_NEW = "/forms/api/stage/{is_project}/{id}";
    public static final String GET_ALL_CONTACTS = "/users/contacts/";

    public static final String GET_SITE_STAGES = "/forms/api/stage/0/{site_id}";
    public static final String GET_STAGE_SUB_STAGE = "/forms/api/stage/{is_project}/{id}";
    public static final String GET_GENERAL_EM = "/forms/api/general/0/{site_id}";
    public static final String GET_SCHEDULE_EM = "/forms/api/schedules/0/{site_id}";

    public static final String GET_LOCATION_URL = "/fieldsight/api/project-sites/";
    public static final String GET_FORM_SCHEDULE = "/forms/api/schedules/{is_project}/{id}";
    public static final String GET_GENERAL_FORM = "/forms/api/general/{is_project}/{id}";
    public static final String GET_PROJECT_SITES = "/users/metwo/";


    public static final String GET_FS_FORM_DETAIL = "/forms/api/form-detail/{fs_form_id}";
    public static final String ASSIGNED_FORM_LIST_PROJECT = "/forms/assignedFormList/project/";
    public static final String ASSIGNED_FORM_LIST_SITE = "/forms/assignedFormList/siteLevel/";

    public static final String ADD_FCM = "/fieldsight/fcm/add/";
    public static final String REMOVE_FCM = "/fieldsight/fcm/logout/";
    public static final String ADD_SITE_URL = "/fieldsight/api/survey-sites/";
    public static final String SITE_UPDATE_URL = "/fieldsight/api/update-site/";
    public static final String PROJECT_UPDATE_URL = "/fieldsight/api/async_save_project/";

    public static final String GET_CLUSTER_LIST = "/fieldsight/project/region-list/{project_id}/";
    public static final String GET_ALL_SUBMISSION = "/forms/last-submissions/";
    public static final String GET_MY_SITES = "/users/mysites";
    public static final String GET_MY_SITES_v2 = "users/api/v2/mysites/";

    public static final String GET_USER_PROFILE = "/users/api/profile/";
    public static final String GET_INSTANCE_SUBMISSION_ATTACHMENTS = "/forms/api/instance/get_attachments_of_finstance/{instance_submission_id}";
    public static final String GET_INSTANCE_XML = "/forms/api/instance/download_submission";
    public static final String GET_FORM_XML = "/forms/api/instance/download_xml_version";


    public static final String GET_EXCHANGE_TOKEN = "/users/api/exchange/google-oauth2/";


    public static class V3 {

        public static final String GET_PROJECTS = "/fv3/api/projects/";
        public static final String GET_SITES = "/fv3/api/sites/";
        public static final String GET_NOTIFICATION = "/fv3/api/user/logs/";
        public static final String GET_SITE_DOCUMENTS = "/fv3/api/site/blueprint/";
    }

    public class PARAMS {
        public static final String PROJECT_ID = "project_id";
        public static final String REGION_ID = "region_id";
        public static final String SITE_ID = "site_id";

    }


}