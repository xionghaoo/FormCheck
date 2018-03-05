package xh.formmodules

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import kotlinx.android.synthetic.fpc.activity_factory_select.*

class FactorySelectActivity : AppCompatActivity() {
    companion object {
        val FACTORY: String = "FactorySelectActivity_Factory"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_factory_select)
        btnSZ.setOnClickListener {
//            intent.setClass(this,UserLoginActivity::class.java)
//            intent.putExtra(FACTORY,"sz")
//            startActivity(intent)
            val intent = Intent(this, UserLoginActivity::class.java)
            intent.putExtra(FACTORY,"sz")
            setResult(1, intent)
            finish()
        }
//        btnSZ.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(p0: View?) {
//
//            }
//        })
        btnQHD.setOnClickListener {
            val intent = Intent(this, UserLoginActivity::class.java)
            intent.putExtra(FACTORY,"qhd")
            setResult(1, intent)
            finish()
        }
        btnHA.setOnClickListener {
            val intent = Intent(this, UserLoginActivity::class.java)
            intent.putExtra(FACTORY,"ha")
            setResult(1, intent)
            finish()
        }
        btnYK.setOnClickListener {
            val intent = Intent(this, UserLoginActivity::class.java)
            intent.putExtra(FACTORY,"yk")
            setResult(1, intent)
            finish()
        }


    }

    fun onClick(v: View) {
        when(v.id) {
            R.id.btnQHD -> {

            }
        }
    }

}
