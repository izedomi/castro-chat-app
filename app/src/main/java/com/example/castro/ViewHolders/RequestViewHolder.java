package com.example.castro.ViewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.castro.R;

import org.w3c.dom.Text;

public class RequestViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout llMainRow;
    public LinearLayout llTextRow;
    public ImageView imvProfile;
    public TextView tvName;
    public TextView tvInfo;
    public Button btnAccept;
    public Button btnDecline;


    public RequestViewHolder(@NonNull View itemView) {
        super(itemView);

        llMainRow = (LinearLayout) itemView.findViewById(R.id.ll_row_main);
        llTextRow = (LinearLayout) itemView.findViewById(R.id.ll_row_text);
        imvProfile = (ImageView) itemView.findViewById(R.id.imv_row_image);
        tvName = (TextView) itemView.findViewById(R.id.tv_row_name);
        tvInfo = (TextView) itemView.findViewById(R.id.tv_row_msg);
        btnAccept = (Button) itemView.findViewById(R.id.btn_accept);
        btnDecline = (Button) itemView.findViewById(R.id.btn_decline);
    }
}
