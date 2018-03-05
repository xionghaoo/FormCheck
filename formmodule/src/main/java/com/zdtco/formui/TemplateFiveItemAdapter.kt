package com.zdtco.formui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.xh.formlib.R
import com.zdtco.datafetch.data.FormPostData

import com.zdtco.formui.TemplateFiveItemFragment.OnListFragmentInteractionListener

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class TemplateFiveItemAdapter(private val mValues: MutableList<FormPostData>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<TemplateFiveItemAdapter.ViewHolder>() {

    fun notifyItemChanged(list: MutableList<FormPostData>) {
        mValues.clear()
        mValues.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_template_five_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mIdView.text = mValues[position].machineID
        holder.mContentView.text = mValues[position].formID

        holder.mView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView
        val mContentView: TextView
        var mItem: FormPostData? = null

        init {
            mIdView = mView.findViewById<TextView>(R.id.id)
            mContentView = mView.findViewById<TextView>(R.id.content)
        }

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
