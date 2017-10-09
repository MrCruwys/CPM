package com.mrcruwys.cpm;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DBEntryAdapter extends RecyclerView.Adapter<DBEntryAdapter.MyViewHolder> {

    private List<DBEntry> pwordList;

    public interface OnDataChangeListener{
        public void onDataChanged(int position);
    }

    private OnDataChangeListener mOnDataChangeListener;

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txt_title);
        }
    }

    public void add(int position, String item) {
        //pwordList.add(position, item);
        //notifyItemInserted(position);
    }

    public void remove(int position) {
        //pwordList.remove(position);
        //notifyItemRemoved(position);
    }

    public DBEntryAdapter(List<DBEntry> pwList) {
        this.pwordList = pwList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entry_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        DBEntry singleEntry = pwordList.get(position);
        holder.title.setText(singleEntry.getName());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDataChangeListener.onDataChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pwordList.size();
    }
}