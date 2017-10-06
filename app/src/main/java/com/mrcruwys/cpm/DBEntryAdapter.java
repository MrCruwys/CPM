package com.mrcruwys.cpm;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DBEntryAdapter extends RecyclerView.Adapter<DBEntryAdapter.MyViewHolder> {

    private List<DBEntry> pwordList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, pword;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txt_title);
            pword = (TextView) view.findViewById(R.id.txt_password);
        }
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DBEntry singleEntry = pwordList.get(position);
        holder.title.setText(singleEntry.getName());
        holder.pword.setText(singleEntry.getName()); // TODO : Change to actual password
    }

    @Override
    public int getItemCount() {
        return pwordList.size();
    }
}