package xh.formmodules

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.fpc.activity_test.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val items = MutableList<String>(4, {index: Int ->
            when(index) {
                0 -> "464136513"
                1 -> "sdfhisunsuihvbiusbvdsnvusndihciusnvnihniubiybgiuhniuhius"
                2 -> "efnwefnn"
                3 -> "hunuv"
                else -> "wrf"
            }
        })

//        wrap_view.columnCount = items.size

//        val l = LinearLayout(this)
//        l.weightSum = items.size.toFloat()


        for (item in items) {
            val view = layoutInflater.inflate(R.layout.form_row_multi_single_display, wrap_view, false)
            val tv1 = view.findViewById<TextView>(R.id.col_title)
            val tv2 = view.findViewById<TextView>(R.id.display_value)
            tv1.text = "A"
            tv2.text = item

            val lp: LinearLayout.LayoutParams = view.layoutParams as LinearLayout.LayoutParams
            lp.weight = 1f
            view.layoutParams = lp
            wrap_view.addView(view)
        }

    }

    fun inflateTxt(items: MutableList<String>) : MutableList<String> {
        var maxLength = 0
        for (item in items) {
            if (item.length > maxLength) {
                maxLength = item.length
            }
        }

        val tmp = MutableList<String>(0, {index ->  ""})

        for (item in items) {
            tmp.add(item + getSpace(maxLength - item.length))
        }
        return tmp
    }

    private fun getSpace(length: Int) : String {
        val builder = StringBuilder()
        for (i in 1..length) {
            builder.append("_")
        }
        return builder.toString()
    }
}
