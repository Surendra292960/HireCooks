package com.test.sample.hirecooks.Adapter.Chat;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.test.sample.hirecooks.Models.Chat.Message;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Message> list;
    SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
    DateFormat date = new SimpleDateFormat("dd MMM yyyy");
    DateFormat time = new SimpleDateFormat("HH:mm a");
    private final Context context;
    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;

    public ChatAdapter(Context context, List<Message> list) { // you can pass other parameters in constructor
        this.context = context;
        this.list = list;
    }

    private class MessageInViewHolder extends RecyclerView.ViewHolder {
        TextView chat_meesage_in,chat_date_in,chat_time_in;
        MessageInViewHolder(final View itemView) {
            super(itemView);
            chat_meesage_in = itemView.findViewById( R.id.chat_meesage_in);
            chat_date_in = itemView.findViewById( R.id.chat_date_in);
            chat_time_in = itemView.findViewById( R.id.chat_time_in);
        }
        void bind(int position) {
            Message message = list.get(position);
            chat_meesage_in.setText(message.getMessage());
            chat_date_in.setText(date.format( getDateTime(message.getSentat())));
            chat_time_in.setText(time.format( getDateTime(message.getSentat())));
        }
    }

    private class MessageOutViewHolder extends RecyclerView.ViewHolder {
        TextView chat_meesage_out,chat_date_out,chat_time_out;
        MessageOutViewHolder(final View itemView) {
            super(itemView);
            chat_meesage_out = itemView.findViewById( R.id.chat_meesage_out);
            chat_date_out = itemView.findViewById( R.id.chat_date_out);
            chat_time_out = itemView.findViewById( R.id.chat_time_out);
        }
        void bind(int position) {
            Message message = list.get(position);
            chat_meesage_out.setText(message.getMessage());
            chat_date_out.setText(date.format( getDateTime(message.getSentat())));
            chat_time_out.setText(time.format( getDateTime(message.getSentat())));
        }
    }

    private Date getDateTime(String sentat) {
        Date date= null;
        try{
            date = format.parse(sentat);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_TYPE_IN) {
            view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_text_in, parent, false );
            return new MessageInViewHolder(view);
        }else if(viewType == MESSAGE_TYPE_OUT){
            view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_text_out, parent, false );
            return new MessageOutViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case MESSAGE_TYPE_IN:
                ((MessageInViewHolder) holder).bind( position );
                break;
            case MESSAGE_TYPE_OUT:
                ((MessageOutViewHolder) holder).bind( position );
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = list.get(position);
        if (message.getFrom_users_id().equals( SharedPrefManager.getInstance( context ).getUser().getId() )) {
            // If the current user is the sender of the message
            return MESSAGE_TYPE_IN;
        } else{
            // If some other user sent the message
            return MESSAGE_TYPE_OUT;
        }
    }
}