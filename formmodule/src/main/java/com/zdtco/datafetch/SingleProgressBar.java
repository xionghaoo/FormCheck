package com.zdtco.datafetch;

import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by G1494458 on 2017/12/14.
 */

public class SingleProgressBar {
    public final TextView progress;
    public final ProgressBar progressBar;

    public SingleProgressBar(TextView progress, ProgressBar progressBar) {
        this.progress = progress;
        this.progressBar = progressBar;
    }
}
