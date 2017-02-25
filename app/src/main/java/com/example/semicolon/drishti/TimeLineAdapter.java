package com.example.semicolon.drishti;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.semicolon.drishti.Model.Sessions;

import java.util.List;

/**
 * Created by Sarvesh on 24-02-2017.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<Sessions> sessionsList;
    private Context mContext;

    public TimeLineAdapter(List<Sessions> sessionsList, Context mContext) {
        this.sessionsList = sessionsList;
        this.mContext = mContext;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        mContext = parent.getContext();
        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timeline, parent, false);

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {
        Sessions sessionModel = sessionsList.get(position);

        holder.name.setText( ""+ sessionModel.getName());
        holder.date.setText("Date :"+ sessionModel.getDate());
    }

    @Override
    public int getItemCount() {
        return (sessionsList != null ? sessionsList.size() : 0);
    }
}
