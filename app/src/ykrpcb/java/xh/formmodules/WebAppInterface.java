package xh.formmodules;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * Created by G1494458 on 2018/3/3.
 */

public class WebAppInterface {
    private Context context;

    public WebAppInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void submit(Map<String, String> form) {
        Log.d("test", "submit: " + form.get("a"));
    }
}
