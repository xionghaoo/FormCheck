package xh.formmodules.nfcbound

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView

import com.zdtco.datafetch.data.Machine

import xh.formmodules.R
import xh.formmodules.nfcbound.MachineItemFragment.OnListFragmentInteractionListener

class MachineItemAdapter(private val mValues: MutableList<Machine>,
                         private val mListener: OnListFragmentInteractionListener?,
                         private val mContext: Context)
    : RecyclerView.Adapter<MachineItemAdapter.ViewHolder>() {

    fun notifyItemsChanged(items: List<Machine>) {
        mValues.clear()
        mValues.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_machineitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val machine = mValues[position]
        holder.mItem = machine
        holder.mIdView?.text = "设备ID:\n" + mValues[position].id
        holder.mNameView?.text = "设备名称: " + mValues[position].machineName
        holder.clsView?.text = "课别: " + mValues[position].classr
        holder.factroyView?.text = "厂区: " + mValues[position].facName
        holder.lineView?.text = "线体: " + mValues[position].line

        if (machine.nfcCode == "") {
            holder.status?.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_error))
        } else {
            holder.status?.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_right))
        }

        holder.mView.setOnClickListener {
            mListener?.onItemClick(holder.mItem!!.id)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView?
        val mNameView: TextView?
        val factroyView: TextView?
        val clsView: TextView?
        val lineView: TextView?
        val status: ImageView?
        var mItem: Machine? = null

        init {
            mIdView = mView.findViewById(R.id.machine_id)
            mNameView = mView.findViewById(R.id.machine_name)
            factroyView = mView.findViewById(R.id.machine_type_param)
            clsView = mView.findViewById(R.id.machine_type_locale_name)
            lineView = mView.findViewById(R.id.machine_type_place)
            status = mView.findViewById(R.id.binding_status)
        }
    }
}
