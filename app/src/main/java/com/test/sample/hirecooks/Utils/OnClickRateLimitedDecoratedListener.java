package com.test.sample.hirecooks.Utils;

import android.view.View;

public class OnClickRateLimitedDecoratedListener implements View.OnClickListener {

    private final static int CLICK_DELAY_DEFAULT = 300;
    private View.OnClickListener onClickListener;
    private int mClickDelay;


        public OnClickRateLimitedDecoratedListener(View.OnClickListener onClickListener) {
            this(onClickListener, CLICK_DELAY_DEFAULT);
        }

        //customize your own delay
        public OnClickRateLimitedDecoratedListener(View.OnClickListener onClickListener, int delay) {
            this.onClickListener = onClickListener;
            mClickDelay = delay;
        }

        @Override
        public void onClick(final View v) {
            v.setClickable(false);
            onClickListener.onClick(v);

            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    v.setClickable(true);
                }
            }, mClickDelay);
        }
    }