package com.test.sample.hirecooks.Activity.Users.FirmUsers;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.FirmUsers.Example;
import com.test.sample.hirecooks.Models.FirmUsers.Firmuser;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.test.sample.hirecooks.Libraries.ExcelSheet.ReadWriteExcelFile.generateFirmUserReport;

public class AllFirmUsersReportActivity extends AppCompatActivity {
    SimpleDateFormat format = new SimpleDateFormat( "yyy-ddd-MM HH:mm:ss" );
    SimpleDateFormat format2 = new SimpleDateFormat( "yyy-dd-MM" );
    DateFormat dateFormat = new SimpleDateFormat("yyy-dd-MM");
    DateFormat date = new SimpleDateFormat( "dd MMM yyyy" );
    DateFormat time = new SimpleDateFormat( "HH:mm a" );
    private AppCompatButton generate_report;
    private RecyclerView report_list_recycler_view;
    private ProgressBarUtil progressBarUtil;
    private ProgressBar progress_btn;
    private UserApi mService;
    private Example exampleList;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_firm_user_report );
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle( "Report");
        initViews();
        if(user!=null){
            //getAllFirmUserByDate( user.getFirmId(), format2.format( new Date() ), format2.format( new Date() ) );
        }
    }

    private void initViews() {
        progressBarUtil = new ProgressBarUtil(this);
        user = SharedPrefManager.getInstance( this ).getUser();
        generate_report = findViewById( R.id.generate_report );
        progress_btn = findViewById( R.id.progress_btn );
        report_list_recycler_view = findViewById( R.id.report_list_recycler_view );
        generate_report.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generate_report.getText().toString().equalsIgnoreCase( "Select Date" )){
                    selectDate( "Report" );
                }else if(generate_report.getText().toString().equalsIgnoreCase( "Generate Report" )){
                    progressBarUtil.showProgress();
                    if(generateFirmUserReport(exampleList.getFirmusers())){
                        sendEmailattache();
                    }else{
                        progressBarUtil.hideProgress();
                        Toast.makeText( AllFirmUsersReportActivity.this, "Failed to load File", Toast.LENGTH_SHORT ).show();
                    }
                }
            }
        } );
    }

    private void sendEmailattache() {
        try{
            Intent emailIntent = new Intent( Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            File root = Environment.getExternalStorageDirectory();
            String pathToMyAttachedFile = "/HireCook/"+"FirmUser.xls";
            File file = new File(root, pathToMyAttachedFile);
            if (!file.exists() || !file.canRead()) {
                return;
            }
            Uri uri = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                uri = FileProvider.getUriForFile(this, "com.example.hirecook.fileprovider", file);
            }else{
                uri = Uri.fromFile(file);
            }
            if(uri!=null){
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            }else{
                Toast.makeText(this, "File cannot access", Toast.LENGTH_SHORT).show();
            }
            progressBarUtil.hideProgress();
            startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getAllFirmUserByDate(String firmId, String from_date, String to_date) {
        System.out.println( "Suree : " + from_date + "  " + to_date );
        progress_btn.setVisibility( View.VISIBLE );
        mService = ApiClient.getClient().create( UserApi.class );
        Call<List<Example>> call = mService.getAllFirmUserByDate( firmId, from_date, to_date );
        call.enqueue( new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                if (response.code() == 200) {
                    progress_btn.setVisibility( View.GONE );
                    progressBarUtil.hideProgress();
                    for (Example example : response.body()) {
                        if (!example.getError() && example.getFirmusers() != null && example.getFirmusers().size() != 0) {
                            setUserTrackingData( example.getFirmusers() );
                        } else {
                            setUserTrackingData( example.getFirmusers() );
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {
                progress_btn.setVisibility( View.GONE );
                System.out.println( "Suree :" + t.getMessage() );
            }
        } );
    }

    public void selectDate(String string) {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.generate_report,null);
        TextView text = view.findViewById( R.id.text );
        EditText from_date = view.findViewById( R.id.from_date );
        EditText to_date = view.findViewById( R.id.to_date );
        AppCompatTextView submit = view.findViewById( R.id.submit );
        text.setText( string );
        dialogBuilder.setView(view);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        from_date.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from_date.setText(getDate(from_date));
            }
        } );
        to_date.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_date.setText(getDate(to_date));
            }
        } );
        submit.setOnClickListener( v -> {
            try {
                dialog.dismiss();
                if(user!=null){
                    getAllFirmUserByDate( user.getFirmId(),dateFormat.format(format2.parse(from_date.getText().toString())),dateFormat.format(format2.parse(to_date.getText().toString())));
                }else{
                    Toast.makeText( this, "Some error occur", Toast.LENGTH_SHORT ).show();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
    }

    public String getDate(EditText txtDate){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> txtDate.setText(year +"-"+ dayOfMonth + "-" + (monthOfYear +1)), mYear, mMonth, mDay);
        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
        return null;
    }

    private String getTime(EditText txtTime){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
        return null;
    }

    private void setUserTrackingData(List<Firmuser> firmuserList) {
        if(firmuserList!=null&&firmuserList.size()!=0){
            exampleList = new Example(  );
            exampleList.setFirmusers(firmuserList);
            generate_report.setText( "Generate Report" );
            FirmUserReportAdapter adapter = new  FirmUserReportAdapter(AllFirmUsersReportActivity.this,firmuserList);
            report_list_recycler_view.setAdapter( adapter );
        }
    }

    private Date getDateTime(String sentat) {
        Date date = null;
        try {
            date = format.parse( sentat );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
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
        return super.onOptionsItemSelected( item );
    }

    public class FirmUserReportAdapter extends RecyclerView.Adapter<AllFirmUsersReportActivity.FirmUserReportAdapter.ViewHolder> {
        private List<Firmuser> firmuserList;
        private Context mCtx;

        public FirmUserReportAdapter(Context mCtx, List<Firmuser> firmuserList) {
            this.firmuserList = firmuserList;
            this.mCtx = mCtx;
        }

        @NonNull
        @Override
        public AllFirmUsersReportActivity.FirmUserReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_layout, parent, false);
            return new AllFirmUsersReportActivity.FirmUserReportAdapter.ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final AllFirmUsersReportActivity.FirmUserReportAdapter.ViewHolder holder, final int position) {
            final Firmuser firmuser = firmuserList.get(position);
            if(firmuser!=null){
                holder.id.setText(""+firmuser.getId());
                holder.name.setText(firmuser.getName());
                holder.user_type.setText(firmuser.getUserType());
                holder.login_date.setText(date.format( getDateTime( firmuser.getSigninDate() ) ));
                holder.logout_date.setText(date.format( getDateTime( firmuser.getSignoutDate() ) ));
                holder.login_address.setText(firmuser.getSigninAddress());
                holder.logout_address.setText(firmuser.getSignoutAddress());
                holder.firm_id.setText(firmuser.getFirmId());
                holder.status.setText(firmuser.getStatus());
                holder.user_id.setText(firmuser.getUserId());
                holder.created_at.setText(firmuser.getCreatedAt());
            }
        }
        @Override
        public int getItemCount() {
            return firmuserList == null ? 0 : firmuserList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView id,name,user_type,login_date,logout_date,login_address,logout_address,firm_id,status,user_id,created_at;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                id = itemView.findViewById(R.id.id);
                user_type = itemView.findViewById(R.id.user_type);
                login_address = itemView.findViewById(R.id.login_address);
                logout_address = itemView.findViewById(R.id.logout_address);
                login_date = itemView.findViewById(R.id.login_date);
                logout_date = itemView.findViewById(R.id.logout_date);
                firm_id = itemView.findViewById(R.id.firm_id);
                status = itemView.findViewById(R.id.status);
                user_id = itemView.findViewById(R.id.user_id);
                created_at = itemView.findViewById(R.id.created_at);
            }
        }
    }
}
