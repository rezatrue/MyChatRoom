package com.rezatrue.mychatroom.adapters;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rezatrue.mychatroom.MainActivity;
import com.rezatrue.mychatroom.R;
import com.rezatrue.mychatroom.pojo.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message>{

    private Context context;
    private ArrayList<Message> messages;

    private static class ViewHolder {
        ImageView userIV;
        TextView userTV;
        TextView timeTV;
        TextView msgTV;
        TextView seenTV;
    }

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, R.layout.msg_single_row, messages);
        this.context = context;
        this.messages = messages;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.msg_single_row, parent, false);

            viewHolder.userIV =  convertView.findViewById(R.id.user_image);
            viewHolder.userTV =  convertView.findViewById(R.id.user_name);
            viewHolder.timeTV =  convertView.findViewById(R.id.msg_time);
            viewHolder.msgTV = convertView.findViewById(R.id.msg_txt);
            viewHolder.seenTV = convertView.findViewById(R.id.msg_view_status);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        Message message = messages.get(position);

        if(message.getImage()!=null){
            Uri myUri = Uri.parse(message.getImage());
            Picasso.get().load(myUri).error(R.drawable.ic_person_black_48dp).into(viewHolder.userIV);
        }
        viewHolder.userTV.setText(message.getName());
        viewHolder.timeTV.setText(message.getTime());
        viewHolder.msgTV.setText(message.getMsg());
        if(message.getUid().equals(MainActivity.uid)) {
            viewHolder.seenTV.setText(message.getStatus());
        }else{
            viewHolder.seenTV.setText("");}

        return result;
    }
}
