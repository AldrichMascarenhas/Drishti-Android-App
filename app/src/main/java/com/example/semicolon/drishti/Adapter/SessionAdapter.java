package com.example.semicolon.drishti.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.semicolon.drishti.Model.SessionData;
import com.example.semicolon.drishti.R;

import java.util.List;

/**
 * Created by semicolon on 2/25/2017.
 */

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
    OnItemClickListener clickListener;
    Context context;
    List<SessionData> sessionDataList;


    public SessionAdapter(Context context, List<SessionData> sessionDataList) {
        this.context = context;
        this.sessionDataList = sessionDataList;
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item_layout, parent, false);
        SessionViewHolder viewHolder = new SessionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
        holder.info.setText(sessionDataList.get(position).getInfo());
        holder.tag.setText(sessionDataList.get(position).getTag());
        holder.tag.setText(sessionDataList.get(position).getLevel());
    }

    @Override
    public int getItemCount() {
        return sessionDataList.size();
    }

    class SessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView tag, info,level;

    public  SessionViewHolder(View itemView) {
        super(itemView);

        tag = (TextView) itemView.findViewById(R.id.tag);
        info = (TextView) itemView.findViewById(R.id.info);
        level =(TextView)itemView.findViewById(R.id.level);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        clickListener.onItemClick(v, getAdapterPosition());
    }
}

public interface OnItemClickListener {
    public void onItemClick(View view, int position);
}

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void addItemsToList(SessionData sessionData){
        sessionDataList.add(sessionData);
        notifyDataSetChanged();
    }
}

