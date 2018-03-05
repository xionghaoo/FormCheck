package com.zdtco.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import com.xh.formlib.R
import com.zdtco.datafetch.data.Machine
import com.zdtco.datafetch.data.MachineOwnedForm

import com.zdtco.home.MachineListFragment.OnMachineListFragListener

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnMachineListFragListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MachineListAdapter(private val mValues: MutableList<Machine>,
                         private val mListenerMachine: OnMachineListFragListener?,
                         private val mContext: Context,
                         private val mCallback: Callback)
    : RecyclerView.Adapter<MachineListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_machine_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val m = mValues[position]
        holder.mItem = m
        holder.mIdView?.text = "设备ID:\n" + mValues[position].id
        holder.mNameView?.text = "设备名称: " + mValues[position].machineName
        holder.clsView?.text = "课别: " + mValues[position].classr
        holder.factroyView?.text = "厂区: " + mValues[position].facName
        holder.lineView?.text = "线体: " + mValues[position].line

        holder.formListView?.adapter = FormItemAdapter(mContext, mValues[position].machineOwnedForms)
        holder.formListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val adapter: FormItemAdapter = parent?.adapter as FormItemAdapter
            mCallback.showFormPage(adapter.getItem(position))
        }

        holder.mView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    fun notifyItemChanged(items: List<Machine>) {
        mValues.clear()
        mValues.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView?
        val mNameView: TextView?
        val factroyView: TextView?
        val clsView: TextView?
        val lineView: TextView?
        val formListView: GridView?
        var mItem: Machine? = null

        init {
            mIdView = mView.findViewById(R.id.machine_id)
            mNameView = mView.findViewById(R.id.machine_name)
            formListView = mView.findViewById(R.id.machine_owned_form)
            factroyView = mView.findViewById(R.id.machine_type_param)
            clsView = mView.findViewById(R.id.machine_type_locale_name)
            lineView = mView.findViewById(R.id.machine_type_place)
        }
    }

    interface Callback {
        fun showFormPage(form: MachineOwnedForm)
    }
}
