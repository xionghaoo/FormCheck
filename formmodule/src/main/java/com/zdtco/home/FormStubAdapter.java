package com.zdtco.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xh.formlib.R;
import com.zdtco.datafetch.data.FormStub;
import com.zdtco.home.FormStubFragment.OnFormStubFragmentListener;
import com.zdtco.home.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnFormStubFragmentListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FormStubAdapter extends RecyclerView.Adapter<FormStubAdapter.ViewHolder> {

    private final List<FormStub> mValues;
    private final OnFormStubFragmentListener mListener;

    public FormStubAdapter(List<FormStub> items, OnFormStubFragmentListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void notifyItemChanged(List<FormStub> items) {
        mValues.clear();
        mValues.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_formstub, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).formID);
        holder.mContentView.setText(mValues.get(position).machineID);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public FormStub mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
