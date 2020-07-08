package com.test.sample.hirecooks.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.test.sample.hirecooks.Models.users.Message;
import com.test.sample.hirecooks.R;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;
    private Context mCtx;

    public MessageAdapter(Context mCtx, List<Message> messages) {
        this.messages = messages;
        this.mCtx = mCtx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_messages, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Message message = messages.get(position);
        holder.textViewName.setText("Sent By "+message.getFrom()+" ,");
        holder.textViewTitle.setText(message.getTitle()+" !");
        holder.textViewMessage.setText(message.getMessage());
        holder.textViewTime.setText(message.getSent());
        Toast.makeText(mCtx, "Suree", Toast.LENGTH_LONG).show();

    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName,textViewMessage,textViewTitle,textViewTime;

         ViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }
    }
}