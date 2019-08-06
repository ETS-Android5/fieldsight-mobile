package org.bcss.collect.naxa.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.GlideApp;
import org.bcss.collect.naxa.common.utilities.SnackBarUtils;


public class ContactDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private View rootView;
    private FieldSightContactModel contactDetail;
    private ImageView profilePicture;
    private TextView fullname, username, role, address, gender, email, skype, twitter, tango, hike, qq, googletalk, viber, whatsapp, wechat;

    public static ContactDetailsBottomSheetFragment getInstance() {
        return new ContactDetailsBottomSheetFragment();
    }

    public void setContact(FieldSightContactModel contactDetail) {
        this.contactDetail = contactDetail;
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.bottom_sheet_contact, container, false);

        ContactDetailToViews();

        return rootView;
    }

    private void ContactDetailToViews() {
        profilePicture = rootView.findViewById(R.id.iv_contactdetail_image);
        if (contactDetail.getProfilePicture() != null) {
            GlideApp.with(this)
                    .load(contactDetail.getProfilePicture())
                    .centerCrop()
                    .into(profilePicture);
        }

        BindAndSetOrHide(fullname, R.id.tv_contactdetail_fullname, contactDetail.getFull_name());

        BindAndSetOrHide(username, R.id.tv_contactdetail_username, contactDetail.getUsername());

        BindAndSetOrHide(role, R.id.tv_contactdetail_role, contactDetail.getRoleString());

        BindAndSetOrHide(address, R.id.tv_contactdetail_address, contactDetail.getAddress());

        BindAndSetOrHide(gender, R.id.tv_contactdetail_gender, contactDetail.getGender());

        BindAndSetOrHide(email, R.id.tv_contactdetail_email, contactDetail.getEmail(), R.id.iv_email_icon);

        BindAndSetOrHide(skype, R.id.tv_contactdetail_skype, contactDetail.getSkype(), R.id.iv_skype_icon);

        BindAndSetOrHide(twitter, R.id.tv_contactdetail_twitter, contactDetail.getTwitter(), R.id.iv_twitter_icon);

        BindAndSetOrHide(tango, R.id.tv_contactdetail_tango, contactDetail.getTango(), R.id.iv_tango_icon);

        BindAndSetOrHide(hike, R.id.tv_contactdetail_hike, contactDetail.getHike(), R.id.iv_hike_icon);

        BindAndSetOrHide(qq, R.id.tv_contactdetail_qq, contactDetail.getQq(), R.id.iv_qq_icon);

        BindAndSetOrHide(googletalk, R.id.tv_contactdetail_googletalk, contactDetail.getGoogle_talk(), R.id.iv_googletalk_icon);

        BindAndSetOrHide(viber, R.id.tv_contactdetail_viber, contactDetail.getViber(), R.id.iv_viber_icon);

        BindAndSetOrHide(whatsapp, R.id.tv_contactdetail_whatsapp, contactDetail.getWhatsapp(), R.id.iv_whatsapp_icon);

        BindAndSetOrHide(wechat, R.id.tv_contactdetail_wechat, contactDetail.getWechat(), R.id.iv_wechat_icon);

        boolean isValidPhoneNumber = !TextUtils.isEmpty(contactDetail.getPhone());

        rootView.findViewById(R.id.btn_phone_call).setVisibility(isValidPhoneNumber ? View.VISIBLE : View.GONE);

        rootView.findViewById(R.id.btn_phone_call)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contactDetail.getPhone(),null));
                        if(canDeviceHandleCall(callIntent)){
                            startActivity(callIntent);
                        }

                    }
                });

    }

    private boolean canDeviceHandleCall(Intent callIntent) {
        if (callIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            return true;
        }

        SnackBarUtils.showFlashbar(requireActivity(),"Device does not support phone calls");
        return false;
    }

    private void BindAndSetOrHide(TextView textView, int viewId, String string) {
        textView = rootView.findViewById(viewId);
        if (TextUtils.isEmpty(string)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(string);
        }
    }

    private void BindAndSetOrHide(TextView textView, int viewId, String string, int iconId) {
        textView = rootView.findViewById(viewId);
        if (TextUtils.isEmpty(string)) {
            textView.setVisibility(View.GONE);
            rootView.findViewById(iconId).setVisibility(View.GONE);
        } else {
            textView.setText(string);
        }
    }


}