package org.bcss.collect.naxa.firebase;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.data.FieldSightNotificationBuilder;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.NotificationEvent.ALL_STAGE_DEPLOYED;
import static org.bcss.collect.naxa.common.Constant.NotificationEvent.SINGLE_STAGED_FORM_DEPLOYED;
import static org.bcss.collect.naxa.common.Constant.NotificationEvent.SINGLE_STAGE_DEPLOYED;
import static org.bcss.collect.naxa.common.Constant.NotificationType.ASSIGNED_SITE;
import static org.bcss.collect.naxa.common.Constant.NotificationType.UNASSIGNED_SITE;
import static org.bcss.collect.naxa.firebase.NotificationUtils.notifyNormal;

public class FieldSightFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseService";
    private final static AtomicInteger notificationId = new AtomicInteger(0);
    private static final String NOTIFY_STATUS = "status";
    public static final String NEW_FORM = "New Form";
    public static final String OUTSTANDING_FORM = "Outstanding";
    public static final String REJECTED_FORM = "Rejected";
    public static final String APPROVED_FORM = "Approved";
    public static final String FLAGGED_FORM = "Flagged";


    String notifyType;
    String siteId;
    String siteName;
    String projectId;
    String projectName;
    String formStatus;
    String role;
    String jrFormId;
    String isFormDeployed, notificationDescriptions;


    String date_str;
    String localTime;
    public static Boolean notificationStatus = false;

    DateFormat dateFormat, date1;
    Date date, currentLocalTime;
    Calendar cal;

    Uri notificationSound;
    Ringtone ringtonePlayer;
    private String comment;
    private String fsFormId;
    private String fsFormIdProject;
    private String formType;
    private String formName;
    private String formComment;
    private String form;

    private String deleteForm;
    private String isDeployed, webDeployedId;
    private String notificationDetailsUrl = "";

    private String isDeployedFromProject;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtonePlayer = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
        getAndSetDateTime();
        FieldSightNotificationBuilder builder = new FieldSightNotificationBuilder();

        String msg = remoteMessage.getData().toString();
        Context context = getApplicationContext();

        Timber.i(msg);

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> notificationData = remoteMessage.getData();
            parseNotificationData(notificationData);

            builder.setDetails_url(notificationDetailsUrl)
                    .setNotificationType(notifyType)
                    .setFsFormId(fsFormId)
                    .setFormName(formName)
                    .setSiteId(siteId)
                    .setSiteName(siteName)
                    .setProjectId(projectId)
                    .setProjectName(projectName)
                    .setFormStatus(formStatus)
                    .setRole(role)
                    .setNotifiedDate(date_str)
                    .setNotifiedTime(localTime)
                    .setIdString(jrFormId)
                    .setComment(formComment)
                    .setFormType(formType)
                    .setIsFormDeployed(isFormDeployed)
                    .setFsFormId(fsFormIdProject)
                    .setIsFormDeployed(isDeployed)
                    .createFieldSightNotification();

            FieldSightNotificationLocalSource.getInstance().save(builder.createFieldSightNotification());
            Pair<String, String> titleContent = FieldSightNotificationLocalSource.getInstance().generateNotificationContent(builder.createFieldSightNotification());

            String title = titleContent.first;
            String content = titleContent.second;

            notifyNormal(context, title, content);
        }

        if (remoteMessage.getData().containsValue("Flag")) {


        }

        if (remoteMessage.getData().containsValue("FormDeleted")) {
            //hello
        }
    }


    private void parseNotificationData(Map<String, String> notificationData) {

        if (notificationData.containsKey("notify_type")) {
            notifyType = notificationData.get("notify_type");
        }
        if (notificationData.containsKey("description")) {
            notificationDescriptions = notificationData.get("description");
        }
        if (notificationData.containsKey("status")) {
            formStatus = notificationData.get("status");
        }
        if (notificationData.containsKey("form_id")) {
            fsFormId = notificationData.get("form_id");
        }
        if (notificationData.containsKey("form_type_id")) {
            formType = notificationData.get("form_type_id");
        }
        if (notificationData.containsKey("site")) {
            String site = notificationData.get("site");
            try {
                JSONObject siteData = new JSONObject(site);
                if (siteData.has("name")) {
                    siteName = siteData.getString("name");
                }
                if (siteData.has("id")) {
                    siteId = siteData.getString("id");
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        if (notificationData.containsKey("project")) {
            String site = notificationData.get("project");
            try {
                JSONObject siteData = new JSONObject(site);
                if (siteData.has("name")) {
                    projectName = siteData.getString("name");
                }
                if (siteData.has("id")) {
                    projectId = siteData.getString("id");
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        if (notificationData.containsKey("xfid")) {
            jrFormId = notificationData.get("xfid");
        }
        if (notificationData.containsKey("comment")) {
            formComment = notificationData.get("comment");
        }
        if (notificationData.containsKey("form_name")) {
            formName = notificationData.get("form_name");
        }
        if (notificationData.containsKey("form_type")) {
            formType = notificationData.get("form_type");
        }
        if (notificationData.containsKey("form")) {
            String form = notificationData.get("form");
        }
        if (notificationData.containsKey("is_delete")) {
            deleteForm = notificationData.get("is_delete");
        }
        if (notificationData.containsKey("is_deployed")) {
            isDeployed = notificationData.get("is_deployed");
        }
        if (notificationData.containsKey("comment_url")) {
            notificationDetailsUrl = notificationData.get("comment_url");
        }

        if (notificationData.containsKey("deploy_id")) {
            webDeployedId = notificationData.get("deploy_id");
            Timber.i("deploy_id %s", webDeployedId);
        }

        if (notificationData.containsKey("is_project")) {
            isDeployedFromProject = notificationData.get("is_project");
        }

        if (notificationData.containsKey("project_form_id")) {
            fsFormIdProject = notificationData.get("project_form_id");
        }
    }


    public static int getID() {
        return notificationId.incrementAndGet();
    }


    private void getAndSetDateTime() {
        dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        date = new Date();
        date_str = dateFormat.format(date);
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-4:00"));
        currentLocalTime = cal.getTime();
        date1 = new SimpleDateFormat("hh:mm a", Locale.US);
        date1.setTimeZone(TimeZone.getTimeZone("GMT+5:45"));
        localTime = date1.format(currentLocalTime);

    }


}
