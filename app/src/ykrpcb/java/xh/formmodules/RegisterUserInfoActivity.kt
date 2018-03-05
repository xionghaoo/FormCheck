package xh.formmodules

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import com.zdtco.nfc.NFCActivity
import kotlinx.android.synthetic.ykrpcb.activity_register_user_info.*

class RegisterUserInfoActivity : NFCActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user_info)

        title = "注册人员信息"

        val webSettings = web_view.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        web_view.addJavascriptInterface(WebAppInterface(this), "Android")

        web_view.loadUrl("file:///android_asset/register.html")
    }

    override fun receivedDecTag(decTag: String?) {
        web_view.loadUrl("javascript:setNFCCode('" + decTag + "');")
    }

    override fun receivedHexTag(hexTag: String?) {

    }
}
