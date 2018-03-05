package xh.formmodules;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.zdtco.FUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        setTitle("最近一次异常日志");

        File f = new File(getFilesDir(), ".");
        File[] logs = f.listFiles();
        Log.d("test", "logs: " + logs.length);
        if (logs.length > 0) {
            File lastLog = logs[logs.length -1];
            InputStream is = null;
            try {
                is = new FileInputStream(lastLog);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String log = FUtil.getString(is, this);
            Log.d("test", "json: " + log);
            ((TextView) findViewById(R.id.log)).setText(log);
        }

    }
}
