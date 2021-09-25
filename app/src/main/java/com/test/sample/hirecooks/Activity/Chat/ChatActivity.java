package com.test.sample.hirecooks.Activity.Chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Adapter.Chat.ChatAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Chat.Example;
import com.test.sample.hirecooks.Models.Chat.Message;
import com.test.sample.hirecooks.Models.Chat.MessageResponse;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.NotificationApi;
import com.test.sample.hirecooks.WebApis.UserApi;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText type_message;
    private TextView user_name,user_status,typing_status,go_back,call,video_call;
    private CircleImageView user_profile;
    private Button send_message;
    private View appRoot;
    private User users;
    private User user;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private List<Message> messagesList = new ArrayList<>(  );
    private DatabaseReference typingRef;
    Boolean isTyping = false;
    private Intent intent;
    private Boolean typingTracker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
        user = SharedPrefManager.getInstance( this ).getUser();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById( R.id.recyclerViewMessages );
        type_message = findViewById( R.id.type_message );
        send_message = findViewById( R.id.send_message );
        user_status = findViewById( R.id.user_status );
        typing_status = findViewById( R.id.typing_status );
        appRoot = findViewById( R.id.appRoot );

        View toolbar_view = findViewById( R.id.toolbar_interface );
        user_name = toolbar_view.findViewById( R.id.user_name );
        user_profile = toolbar_view.findViewById( R.id.user_profile );
        go_back = toolbar_view.findViewById( R.id.go_back );
        call = toolbar_view.findViewById( R.id.call );
        video_call = toolbar_view.findViewById( R.id.video_call );

        //checkFirebaseUser();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            users = (User) bundle.getSerializable( "User" );
            if (users != null) {
                getMessages( users.getId() );
                user_name.setText( users.getName() );
                if (users.getImage() != null) {
                    if (users.getImage().contains( "https://" )) {
                        Picasso.with( this ).load( users.getImage() ).into( user_profile );
                    } else if (users.getImage().contains( " " )) {

                    } else {
                        Picasso.with( this ).load( APIUrl.PROFILE_URL + users.getImage() ).into( user_profile );
                    }
                }
            }
        }

        send_message.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        } );

        go_back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );

        type_message.addTextChangedListener(new TextWatcher() {
            boolean isTyping = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            private Timer timer = new Timer();
            private final long DELAY = 5000; // milliseconds

            @Override
            public void afterTextChanged(final Editable s) {
                Log.d("", "");
                if(!isTyping) {
                    Log.d("TAG", "started typing");
                    // Send notification for start typing event
                    startTypingMessage(user.getId(),"Typing");
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                isTyping = false;
                                Log.d("TAG", "stopped typing");
                                //send notification for stopped typing event
                                startTypingMessage(user.getId(),"Stop");
                            }
                        },
                        DELAY
                );
            }
        });
        call.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CALL_PHONE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions( ChatActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                } else {
                    startActivity(new Intent(Intent.ACTION_CALL)
                            .setAction(Intent.ACTION_DIAL).setData( Uri.parse("tel:"+users.getPhone())));
                }
            }
        } );
        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setWelcomePageEnabled(false)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setRoom("xub Meeting")
                        .setWelcomePageEnabled(false)
                        .build();
                JitsiMeetActivity.launch( ChatActivity.this, options);
            }
        });
    }

    private void validate() {
        String message = type_message.getText().toString().trim();
        if (!TextUtils.isEmpty( message )){
            if(users!=null){
                sendMessage( user.getId() ,"Hello",message);
            }
        }
    }

    private void startTypingMessage(Integer id, String typing) {
        ArrayList<Example> exampleArrayList = new ArrayList<>(  );
        Example example = new Example();
        ArrayList<Message> messageArrayList = new ArrayList<>(  );
        Message message = new Message();
        message.setId( id );
        message.setMessage( typing );
        messageArrayList.add( message );
        example.setMessages( messageArrayList );
        exampleArrayList.add( example );

        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<List<Example>> call = service.startTypingMessage(id,exampleArrayList);
        call.enqueue(new Callback<List<Example>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<Example>> call, @NonNull Response<List<Example>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    for (Example messages:response.body()){
                        if(!messages.getError()){
                            for(Message message: messages.getMessages()){
                                if(user.getId() != message.getId()&&message.getMessage().equalsIgnoreCase( "Typing" )) {
                                    typing_status.setVisibility( View.VISIBLE );
                                    typing_status.setText( message.getMessage() );
                                }else if(message.getMessage().equalsIgnoreCase( "Stop" )){
                                    typing_status.setVisibility( View.GONE );
                                }
                            }
                        }
                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<Example>> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void getMessages(Integer id) {
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<List<Example>> call = service.getMessages(id);
        call.enqueue(new Callback<List<Example>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<Example>> call, @NonNull Response<List<Example>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    List<Message> messageList = new ArrayList<>(  );
                    List<Message> filterMessageList = new ArrayList<>(  );
                    for (Example messages:response.body()){
                        messagesList = messages.getMessages();
                        if(!messages.getError()){
                          for(Message message : messages.getMessages()){
                              if(users!=null){
                                  if(message.getFrom_users_id().equals( users.getId()) || message.getTo_users_id().equals( users.getId())){
                                      messageList.add( message );
                                      Set<Message> set = new HashSet<>( messageList );
                                      filterMessageList = new ArrayList<>(set);
                                  }
                              }else{
                                  if(message.getFrom_users_id().equals( user.getId()) || message.getTo_users_id().equals( user.getId())){
                                      messageList.add( message );
                                      Set<Message> set = new HashSet<>( messageList );
                                      filterMessageList = new ArrayList<>(set);
                                  }
                              }
                          }
                          if(filterMessageList!=null&&filterMessageList.size()!=0){
                              adapter = new ChatAdapter( ChatActivity.this, filterMessageList);
                              recyclerView.setAdapter(adapter);
                              adapter.notifyDataSetChanged();
                          }
                        }
                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<Example>> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

/*    private void checkFirebaseUser() {
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            for (UserInfo profile : firebaseUser.getProviderData()) {
                user_status.setText( "Online" );
            }
        }else{
            user_status.setText( "Offline" );
        }
    }*/

    private void sendMessage(int id, final String title, final String message) {
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<MessageResponse> call = service.sendMessage( id, users.getId(), title, message,1,0);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if(response.code()==200) {
                    if (response.body() != null) {
                        type_message.setText( " " );
                      //  recyclerView.scrollToPosition(messagesList.size() - 1);
                        firebaseMessageSend( id, users.getId(), title, message, 1, 0 );
                        Toast.makeText( ChatActivity.this, response.body().getMessage(), Toast.LENGTH_LONG ).show();
                        getMessages( users.getId() );
                        UserApi mService = ApiClient.getClient().create( UserApi.class );
                        Call<TokenResult> call1 = mService.getTokenByFirmId( user.getId(), users.getFirmId() );
                        call1.enqueue( new Callback<TokenResult>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onResponse(Call<TokenResult> call, Response<TokenResult> response) {
                                int statusCode = response.code();
                                if (statusCode == 200) {
                                    NotificationApi mService = ApiClient.getClient().create( NotificationApi.class );
                                    Call<String> calls = mService.chatNotification( response.body().getToken().getToken(), users.getFirmId(), message );
                                    calls.enqueue( new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if (response.code() == 200) {

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            System.out.println( t.toString() );
                                            Toast.makeText( ChatActivity.this, R.string.error, Toast.LENGTH_SHORT ).show();
                                        }
                                    } );
                                }
                            }

                            @Override
                            public void onFailure(Call<TokenResult> call, Throwable t) {
                                Toast.makeText( getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG ).show();
                                System.out.println( "Suree: " + t.getMessage() );
                            }
                        } );
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call,  @NonNull Throwable t) {
                Toast.makeText( ChatActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void firebaseMessageSend(int from_user_id, Integer to_user_id, String title, String message, int sent, int recieve) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>(  );
        hashMap.put( "from_user_id",from_user_id );
        hashMap.put( "to_user_id",to_user_id );
        hashMap.put( "title",title );
        hashMap.put( "message",message );
        hashMap.put( "sent",sent );
        hashMap.put( "recieve",recieve );
        reference.child( "Chat" ).push().setValue( hashMap );
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}