package com.test.sample.hirecooks.ExpendableList;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;
import com.test.sample.hirecooks.Models.Feed.Feed;
import com.test.sample.hirecooks.Models.Feed.HeadingView;
import com.test.sample.hirecooks.Models.Feed.Info;
import com.test.sample.hirecooks.Models.Feed.InfoView;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.JsonDataCall;

public class ExpendableListActivity extends AppCompatActivity {
    private ExpandablePlaceHolderView mExpandableView;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expendable_list);

     /*   mContext = this.getApplicationContext();
        mExpandableView = findViewById(R.id.expandableView);
        for(Feed feed : JsonDataCall.loadFeeds(this.getApplicationContext())){
            mExpandableView.addView(new HeadingView(mContext, feed.getHeading()));
            for(Info info : feed.getInfoList()){
                mExpandableView.addView(new InfoView(mContext, info));
            }
        }*/
    }
}
