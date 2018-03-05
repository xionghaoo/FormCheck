package com.zdtco.datafetch;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.xh.formlib.R;
import com.zdtco.FUtil;

/**
 * Created by G1494458 on 2017/12/7.
 */

public class DownloadTool implements Observer {

    Repository mRepository;
    private AlertDialog mDialog;
    private Button btnStart;
    private Button btnClose;
    private Callback callback;

    public DownloadTool(Repository repository, Callback callback) {
        mRepository = repository;
        ((Observable) mRepository).addObserver(this);
        this.callback = callback;
    }

    public void start(Context context, List<String> threads) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);

        final MultiThreadDownloadView progressView = view.findViewById(R.id.multiThreadDownloadView);
        progressView.create(threads);

        btnStart = view.findViewById(R.id.download);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setEnabled(false);
                btnClose.setEnabled(false);
                callback.downloadStart();
                mRepository.downloadAll(progressView);
            }
        });
        btnClose = view.findViewById(R.id.close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("资料下载")
                .setCancelable(false)
                .create();
        mDialog.show();
    }

    public void cancel() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Repository) {
            if (btnStart != null && btnClose != null) {
                btnStart.setEnabled(true);
                btnClose.setEnabled(true);
            }
            int status = (int) arg;
            if (status == Repository.DOWNLOAD_SUCCESS) {
                callback.downloadComplete();
            } else {
                callback.downloadFailure();
            }
        }
    }

    public interface Callback {
        void downloadComplete();
        void downloadStart();
        void downloadFailure();
    }
}
