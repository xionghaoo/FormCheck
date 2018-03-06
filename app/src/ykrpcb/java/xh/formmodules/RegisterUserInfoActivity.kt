package xh.formmodules

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.*
import com.zdtco.nfc.NFCActivity
import kotlinx.android.synthetic.ykrpcb.activity_register_user_info.*
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.zdtco.FUtil


class RegisterUserInfoActivity : NFCActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user_info)

        title = "注册人员信息"

        val webSettings = web_view.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        web_view.addJavascriptInterface(WebAppInterface(this), "Android")
        web_view.webViewClient = RegisterWebClient()
        web_view.loadUrl("file:///android_asset/register.html")
    }

    override fun receivedDecTag(decTag: String?) {
        web_view.loadUrl("javascript:setNFCCode('" + decTag + "');")
    }

    override fun receivedHexTag(hexTag: String?) {

    }

    private class RegisterWebClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url!!.matches("^http://10.182.34.124:8999/[\\w\\W]+$".toRegex())) {
//                view?.loadUrl("file:///android_asset/register.html")
//                FUtil.showToast(view?.context, "注册成功")
                return false
            }
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }

//        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
//            Log.d("test", "onReceivedError")
//            super.onReceivedError(view, errorCode, description, failingUrl)
//        }
//
//        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse {
//            Log.d("test", "shouldInterceptRequest")
//            return super.shouldInterceptRequest(view, request)
//        }
//
//        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//            Log.d("test", "new shouldOverrideUrlLoading")
//            return super.shouldOverrideUrlLoading(view, request)
//        }
    }

}
