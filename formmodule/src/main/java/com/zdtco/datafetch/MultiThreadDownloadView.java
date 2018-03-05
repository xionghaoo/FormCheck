package com.zdtco.datafetch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xh.formlib.R;

/**
 * Created by G1494458 on 2017/12/7.
 */

public class MultiThreadDownloadView extends LinearLayout {

    private int mThreadCount = 0;
    private Map<String, SingleProgressBar> progressBarMap = new HashMap<>();
    private List<String> mThreads;

    public MultiThreadDownloadView(Context context) {
        super(context);
        init();
    }

    public MultiThreadDownloadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiThreadDownloadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setOrientation(VERTICAL);
    }

    public void create(List<String> threads) {
        mThreadCount = threads.size();
        mThreads = threads;
        for (String thread: threads) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.widget_multi_download_progress, this, false);
            ProgressBar progressBar = view.findViewById(R.id.progress_bar);
            TextView tvName = view.findViewById(R.id.thread_name);
            TextView tvProgress = view.findViewById(R.id.progress);

            progressBarMap.put(thread, new SingleProgressBar(tvProgress, progressBar));
            tvName.setText(thread);
            addView(view);
        }
    }

    public SingleProgressBar getSingleProgressBar(String name) {
        return progressBarMap.get(name);
    }

    public int getmThreadCount() {
        return mThreadCount;
    }

    public List<String> getThreads() {
        return mThreads;
    }

//    public class Builder {
//        private List<String> threads;
//
//        public Builder addThread(String threadName) {
//            threads.add(threadName);
//            return this;
//        }
//
//        public Builder build(Context context) {
//            MultiThreadDownloadView multiThreadDownloadView = new MultiThreadDownloadView(context);
//            multiThreadDownloadView.create(threads);
//            return this;
//        }
//    }
}
