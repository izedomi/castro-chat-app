package com.example.castro.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.castro.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessagesViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout relMsgWrapper;
    public CircleImageView imvMsgSender;
    public CircleImageView imvMsgReceiver;
    public TextView tvMsgSenderText;
    public TextView tvMsgReceiverText;
    public LinearLayout llSenderRow;
    public LinearLayout llReceiverRow;
    public ImageView imvImgSent;
    public ImageView imvImgReceived;

    public MessagesViewHolder(@NonNull View itemView) {
        super(itemView);

        imvMsgSender = itemView.findViewById(R.id.msg_sender_image);
        imvMsgReceiver = itemView.findViewById(R.id.msg_receiver_image);
        tvMsgSenderText = itemView.findViewById(R.id.msg_sender_text);
        tvMsgReceiverText = itemView.findViewById(R.id.msg_receiver_text);
        relMsgWrapper = itemView.findViewById(R.id.msg_wrapper);
        llSenderRow = itemView.findViewById(R.id.sender_row);
        llReceiverRow = itemView.findViewById(R.id.receiver_row);
        imvImgSent = itemView.findViewById(R.id.sender_img);
        imvImgReceived = itemView.findViewById(R.id.receiver_img);
    }
}
