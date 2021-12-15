package com.test.sample.hirecooks.Adapter.Chat;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.test.sample.hirecooks.Activity.Chat.DateParser;
import com.test.sample.hirecooks.Models.Chat.ChatModelObject;
import com.test.sample.hirecooks.Models.Chat.DateObject;
import com.test.sample.hirecooks.Models.Chat.ListObject;
import com.test.sample.hirecooks.Models.Chat.Message;
import com.test.sample.hirecooks.R;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ListObject> listObjects;
    private int loggedInUserId;
    private Context context;

    public ChatAdapter(List<ListObject> listObjects, Context context) {
        this.listObjects = listObjects;
        this.context = context;
    }

    public void setUser(int userId) {
        this.loggedInUserId = userId;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataChange(List<ListObject> asList) {
        this.listObjects = asList;
        //now, tell the adapter about the update
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        //LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ListObject.TYPE_GENERAL_RIGHT:
                View currentUserView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right, parent, false);
                viewHolder = new ChatRightViewHolder(currentUserView); // view holder for normal items
                break;
            case ListObject.TYPE_GENERAL_LEFT:
                View otherUserView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left, parent, false);
                viewHolder = new ChatLeftViewHolder(otherUserView); // view holder for normal items
                break;
            case ListObject.TYPE_DATE:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
                viewHolder = new DateViewHolder(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case ListObject.TYPE_GENERAL_RIGHT:
                ChatModelObject generalItem = (ChatModelObject) listObjects.get(position);
                ChatRightViewHolder chatViewHolder = (ChatRightViewHolder) viewHolder;
                chatViewHolder.bind(generalItem.getChatModel());
                break;
            case ListObject.TYPE_GENERAL_LEFT:
                ChatModelObject generalItemLeft = (ChatModelObject) listObjects.get(position);
                ChatLeftViewHolder chatLeftViewHolder = (ChatLeftViewHolder) viewHolder;
                chatLeftViewHolder.bind(generalItemLeft.getChatModel());
                break;
            case ListObject.TYPE_DATE:
                DateObject dateItem = (DateObject) listObjects.get(position);
                DateViewHolder dateViewHolder = (DateViewHolder) viewHolder;
                dateViewHolder.bind(dateItem.getDate());
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (listObjects != null) {
            return listObjects.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return listObjects.get(position).getType(loggedInUserId);
    }

    public ListObject getItem(int position) {
        return listObjects.get(position);
    }

    private static class ChatRightViewHolder extends RecyclerView.ViewHolder {
        TextView chat_meesage_in,chat_time_in;
        public ChatRightViewHolder(View itemView) {
            super(itemView);
            chat_meesage_in = itemView.findViewById( R.id.chat_meesage_in);
            chat_time_in = itemView.findViewById( R.id.chat_time_in);
        }

        public void bind(Message message) {
            chat_meesage_in.setText(message.getMessage());
            chat_time_in.setText(DateParser.getTime(message.getSentat()));
        }
    }

    public static class ChatLeftViewHolder extends RecyclerView.ViewHolder {
        TextView chat_meesage_out,chat_time_out;
        public ChatLeftViewHolder(View itemView) {
            super(itemView);
            //TODO initialize your xml views
            chat_meesage_out = itemView.findViewById( R.id.chat_meesage_out);
            chat_time_out = itemView.findViewById( R.id.chat_time_out);
        }

        public void bind(final Message message) {
            //TODO set data to xml view via textivew.setText();
            chat_meesage_out.setText(message.getMessage());
            chat_time_out.setText(DateParser.getTime(message.getSentat()));
        }
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView text_date;
        public DateViewHolder(View itemView) {
            super(itemView);
            //TODO initialize your xml views
            text_date = itemView.findViewById(R.id.text_date);
        }

        public void bind(final String date) {
            //TODO set data to xml view via textivew.setText();
            text_date.setText(DateParser.getDate(date));
        }
    }
}