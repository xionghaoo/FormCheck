package com.zdtco.formui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.xh.formlib.R
import kotlinx.android.synthetic.main.fragment_template_hc.*


/**
 * A simple [Fragment] subclass.
 */
class TemplateHCFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_template_hc, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        web_view.loadUrl("http://10.182.45.71/e_system_mobile/login.aspx?db=2")
    }
}// Required empty public constructor
