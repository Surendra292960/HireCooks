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
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.UserApi;
import com.test.sample.hirecooks.databinding.TableLayoutBinding;

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

public class FirmUserReportActivity extends AppCompatActivity {
    SimpleDateFormat format = new SimpleDateFormat( "yyy-ddd-MM HH:mm:ss" );
    SimpleDateFormat format2 = new SimpleDateFormat( "yyy-dd-MM" );
    DateFormat dateFormat = new SimpleDateFormat("yyy-dd-MM");
    DateFormat date = new SimpleDateFormat( "dd MMM yyyy" );
    DateFormat time = new SimpleDateFormat( "HH:mm a" );
    private AppCompatButton generate_report;
    private RecyclerView report_list_recycler_view;
    private User user;
    private ProgressBarUtil progressBarUtil;
    private ProgressBar progress_btn;
    private UserApi mService;
    private Example exampleList;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private View mToolabr ,goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_firm_user_report );
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            user = (User) bundle.getSerializable( "FirmUser" );
            if(user!=null){

            }
        }
        if(NetworkUtil.checkInternetConnection(this)) {
            initViews();
        }
        else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        progressBarUtil = new ProgressBarUtil(this);
        generate_report = findViewById( R.id.generate_report );
        progress_btn = findViewById( R.id.progress_btn );
        report_list_recycler_view = findViewById( R.id.report_list_recycler_view );
        View view = findViewById(R.id.m_toolbar_interface);
        mToolabr = view.findViewById(R.id.m_toolbar);
        mToolabr.setVisibility(View.VISIBLE);
        goBack = view.findViewById(R.id.go_back);
        goBack.setOnClickListener(v->finish());
        generate_report.setOnClickListener(v -> {
            if(generate_report.getText().toString().equalsIgnoreCase( "Select Date" )){
                selectDate( "Report" );
            }else if(generate_report.getText().toString().equalsIgnoreCase( "Generate Report" )){
                progressBarUtil.showProgress();
                if(generateFirmUserReport(exampleList.getFirmusers())){
                    sendEmailattache();
                }else{
                    progressBarUtil.hideProgress();
                    Toast.makeText( FirmUserReportActivity.this, "Failed to load File", Toast.LENGTH_SHORT ).show();
                }
            }
        });
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

    public void getFirmUserByDate(Integer userId, String from_date, String to_date) {
        System.out.println( "Suree : " + from_date + "  " + to_date );
        progress_btn.setVisibility( View.VISIBLE );
        mService = ApiClient.getClient().create( UserApi.class );
        Call<List<Example>> call = mService.getFirmUserByDate( userId, from_date, to_date );
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
                    getFirmUserByDate( user.getId(),dateFormat.format(format2.parse(from_date.getText().toString())),dateFormat.format(format2.parse(to_date.getText().toString())));
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
            FirmUserReportAdapter adapter = new  FirmUserReportAdapter(FirmUserReportActivity.this,firmuserList);
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

    public class FirmUserReportAdapter extends RecyclerView.Adapter<FirmUserReportActivity.FirmUserReportAdapter.ViewHolder> {
        private List<Firmuser> firmuserList;
        private Context mCtx;

        public FirmUserReportAdapter(Context mCtx, List<Firmuser> firmuserList) {
            this.firmuserList = firmuserList;
            this.mCtx = mCtx;
        }

        @NonNull
        @Override
        public FirmUserReportActivity.FirmUserReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FirmUserReportActivity.FirmUserReportAdapter.ViewHolder(TableLayoutBinding.inflate(getLayoutInflater()));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final FirmUserReportActivity.FirmUserReportAdapter.ViewHolder holder, final int position) {
            final Firmuser firmuser = firmuserList.get(position);
           if(firmuser!=null){
               holder.binding.id.setText(""+firmuser.getId());
               holder.binding.name.setText(firmuser.getName());
               holder.binding.userType.setText(firmuser.getUserType());
               holder.binding.loginDate.setText(firmuser.getSigninDate());
               holder.binding.logoutDate.setText(firmuser.getSignoutDate() );
               holder.binding.loginAddress.setText(firmuser.getSigninAddress());
               holder.binding.logoutAddress.setText(firmuser.getSignoutAddress());
               holder.binding.firmId.setText(firmuser.getFirmId());
               holder.binding.status.setText(firmuser.getStatus());
               holder.binding.userId.setText(firmuser.getUserId());
               holder.binding.createdAt.setText(firmuser.getCreatedAt());
           }
        }
        @Override
        public int getItemCount() {
            return firmuserList == null ? 0 : firmuserList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TableLayoutBinding binding;

            ViewHolder(@NonNull TableLayoutBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
            }
        }
    }
}
