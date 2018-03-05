package com.zdtco.settings

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.xh.formlib.R
import com.zdtco.datafetch.data.Line
import com.zdtco.datafetch.data.Machine

import com.zdtco.settings.MachineTimeLimitFragment.OnListFragmentInteractionListener

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MachineTimeLimitAdapter(private val mValues: MutableList<Line>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<MachineTimeLimitAdapter.ViewHolder>() {

    fun notifyItemsChanged(items: List<Line>) {
        mValues.clear()
        mValues.addAll(items)
        notifyDataSetChanged()
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_machinetimelimit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val machine = mValues[position]
        holder.mItem = machine
        holder.mIdView?.text = "线体编号:\n" + mValues[position].line
        holder.mNameView?.text = "线体名称: " + mValues[position].lineName
        holder.mClsrNameView?.text = "课别: " + mValues[position].classr
        holder.timelimit?.text = "时间限制: " + machine.limitTime + "分钟"


        holder.mView.setOnClickListener {
            mListener?.onItemClicked(machine.line)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView?
        val mNameView: TextView?
        val mClsrNameView: TextView?
        val timelimit: TextView?
        var mItem: Line? = null

        init {
            mIdView = mView.findViewById(R.id.line_id)
            mNameView = mView.findViewById(R.id.line_name)
            mClsrNameView = mView.findViewById(R.id.clsr_name)
            timelimit = mView.findViewById(R.id.time_limit)
        }
    }
}
