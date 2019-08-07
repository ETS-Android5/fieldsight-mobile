package org.fieldsight.naxa.educational;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.fieldsight.collect.android.R;

/**
 * Created by susan on 7/5/2017.
 */

public class ViewHolderText extends RecyclerView.ViewHolder{

    public static TextView label1, label2;
    public static LinearLayout linearClick;

    public ViewHolderText(View itemView) {
        super(itemView);
        label1 = itemView.findViewById(R.id.tv_title);
        label2 = itemView.findViewById(R.id.tv_desc);
        linearClick = itemView.findViewById(R.id.linear_layout_click);
    }

    public TextView getLabel1() {
        return label1;
    }

    public void setLabel1(TextView label1) {
        ViewHolderText.label1 = label1;
    }

    public TextView getLabel2() {
        return label2;
    }

    public void setLabel2(TextView label2) {
        ViewHolderText.label2 = label2;
    }
}
