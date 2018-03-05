package com.zdtco.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xh.formlib.R
import com.zdtco.datafetch.data.Machine
import com.zdtco.datafetch.data.MachineOwnedForm
import com.zdtco.formui.FormActivity

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class MachineListFragment : Fragment() {
    // TODO: Customize parameters
    private var mColumnCount = 1
    private var mListenerMachine: OnMachineListFragListener? = null
    private lateinit var mAdapter: MachineListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_machinelist_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            val recyclerView = view as RecyclerView
            if (mColumnCount <= 1) {
                recyclerView.layoutManager = LinearLayoutManager(context)
            } else {
                recyclerView.layoutManager = GridLayoutManager(context, mColumnCount)
            }
            mAdapter = MachineListAdapter(ArrayList(), mListenerMachine, activity, object : MachineListAdapter.Callback {
                override fun showFormPage(form: MachineOwnedForm) {
                    mListenerMachine?.onItemClicked(form)
                }
            })
            recyclerView.adapter = mAdapter
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMachineListFragListener) {
            mListenerMachine = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListenerMachine = null
    }

    fun receivedNFCCode(code: String?) {
        mListenerMachine?.loadMachines(code!!)
    }

    fun updateMachineList(machines: List<Machine>) {
        mAdapter.notifyItemChanged(machines)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnMachineListFragListener {
        fun loadMachines(nfcCode: String)
        fun onItemClicked(form: MachineOwnedForm)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): MachineListFragment {
            val fragment = MachineListFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
