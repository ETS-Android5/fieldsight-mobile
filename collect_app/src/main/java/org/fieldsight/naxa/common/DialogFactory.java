package org.fieldsight.naxa.common;

/**
 * Created by Nishon Tandukar on 16 Jun 2017 .
 *
 * @email nishon.tan@gmail.com
 */


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.login.model.Site;
import org.odk.collect.android.application.Collect;

import java.util.Calendar;
import java.util.List;


public final class DialogFactory {

    private DialogFactory(){

    }


    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.RiseUpDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.RiseUpDialog)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createDataSyncErrorDialog(Context context, String message, String code) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.RiseUpDialog)
                .setTitle(context.getString(R.string.dialog_error_title_sync_failed, code))
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }


    public static Dialog createMessageDialog(final Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.RiseUpDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }


    public static AlertDialog.Builder createActionDialog(final Context context, String title, String message) {
        return new AlertDialog.Builder(context, R.style.RiseUpDialog)
                .setTitle(title).setCancelable(false)
                .setMessage(message);
    }

    public static Dialog createGenericErrorDialog(Context context, @StringRes int messageResource) {
        return createGenericErrorDialog(context, context.getString(messageResource));
    }

    public static Dialog createDataSyncErrorDialog(Context context, @StringRes int messageResource, String responseCode) {
        return createDataSyncErrorDialog(context, context.getString(messageResource), responseCode);
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.RiseUpDialog);
        progressDialog.setTitle(message);

        return progressDialog;
    }


    public static ProgressDialog createProgressDialogHorizontal(Context context, String title) {
        ProgressDialog progress = new ProgressDialog(context, R.style.RiseUpDialog);
        progress.setTitle(title);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.setProgressNumberFormat(null);
        progress.setProgressPercentFormat(null);

        return progress;
    }


    public static ProgressDialog createProgressBarDialog(Context context, String title, String message) {


        final ProgressDialog progress = new ProgressDialog(context, R.style.RiseUpDialog);

        DialogInterface.OnClickListener buttonListerns =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                progress.dismiss();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };


        progress.setMessage(message);
        progress.setTitle(title);

        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.dialog_action_hide), buttonListerns);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setCancelable(false);


        return progress;
    }

    public static ProgressDialog createProgressDialog(Context context,
                                                      @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }

    public static AlertDialog showErrorDialog(Context context, String[] errors) {
        return showListDialog(context, context.getString(R.string.dialog_unexpected_error_title), errors);
    }

    private static AlertDialog showListDialog(Context context, String title, String[] listItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(listItems, null);

        return builder.create();
    }


    public static AlertDialog.Builder createListActionDialog(Context context, String title, CharSequence[] items, DialogInterface.OnClickListener onClickListener) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RiseUpDialog);
        builder.setTitle(title).setItems(items, onClickListener);
        return builder;
    }


    public static AlertDialog.Builder createSiteListDialog(Context context, List<Site> items, DialogInterface.OnClickListener listener) {
        ListAdapter adapter = new ArrayAdapter<Site>(context, R.layout.sub_site_list_item, items) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                final LayoutInflater inflater = LayoutInflater.from(context);
                if (position == 0) {
                    convertView = inflater.inflate(R.layout.subsite_list_header, parent, false);
                } else {
                    convertView = inflater.inflate(R.layout.sub_site_list_item, parent, false);
                }
                Site siteAtpos = items.get(position);
                TextView tv_subsiteName = convertView.findViewById(R.id.tv_sub_site_name);
                TextView tv_icon_text = convertView.findViewById(R.id.tv_icon_text);
                TextView tv_sub_site_identifier = convertView.findViewById(R.id.tv_sub_site_identifier);
                tv_sub_site_identifier.setText(siteAtpos.getIdentifier());
                tv_subsiteName.setText(siteAtpos.getName());
                tv_icon_text.setText(siteAtpos.getName().substring(0, 1));

                return convertView;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RiseUpDialog);
        builder.setAdapter(adapter, listener);
        return builder;
    }

    private static AlertDialog.Builder showCustomLayoutDialog(Context context, View view) {

        return new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false);
//        Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.setContentView(view);
//        return dialog;
    }


    public static DatePickerDialog createDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener listener) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH);
        int curDay = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(context, listener, curYear, curMonth, curDay);
    }

    public static AlertDialog.Builder createActionConsentDialog(Context context, String title, String message) {
        View viewInflated = LayoutInflater.from(Collect.getInstance()).inflate(R.layout.dialog_site_project_filter, null, false);
        return showCustomLayoutDialog(context, viewInflated);
    }
}
