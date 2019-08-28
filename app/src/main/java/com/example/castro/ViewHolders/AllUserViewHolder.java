package com.example.castro.ViewHolders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.castro.R;

public class AllUserViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout llMainRow;
    public LinearLayout llTextRow;
    public ImageView imvProfile;
    public TextView tvName;
    public TextView tvDepartment;
    public CardView cardRow;


    public AllUserViewHolder(@NonNull View itemView) {
        super(itemView);

        llMainRow = (LinearLayout) itemView.findViewById(R.id.ll_row_main);
        llTextRow = (LinearLayout) itemView.findViewById(R.id.ll_row_text);
        imvProfile = (ImageView) itemView.findViewById(R.id.imv_row_image);
        tvName = (TextView) itemView.findViewById(R.id.tv_row_name);
        tvDepartment = (TextView) itemView.findViewById(R.id.tv_row_department);
        cardRow = (CardView) itemView.findViewById(R.id.card_row);
    }
}
