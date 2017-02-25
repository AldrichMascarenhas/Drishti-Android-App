package com.example.semicolon.drishti.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.semicolon.drishti.Model.OnGoingSessionData;
import com.example.semicolon.drishti.Model.SessionData;
import com.example.semicolon.drishti.R;

import java.io.File;
import java.util.List;

/**
 * Created by semicolon on 2/25/2017.
 */

public class OnGoingSessionAdapter extends RecyclerView.Adapter<OnGoingSessionAdapter.SessionViewHolder>  {

    SessionAdapter.OnItemClickListener clickListener;
    Context context;
    List<OnGoingSessionData> onGoingSessionDataList;
    RecyclerView recyclerView;


    public OnGoingSessionAdapter(Context context, List<OnGoingSessionData> onGoingSessionDataList) {
        this.context = context;
        this.onGoingSessionDataList = onGoingSessionDataList;
    }


    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item_layout, parent, false);
        OnGoingSessionAdapter.SessionViewHolder viewHolder = new OnGoingSessionAdapter.SessionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
        holder.info_textview.setText(onGoingSessionDataList.get(position).getResult());

        File imgFile = new File(onGoingSessionDataList.get(position).getImage_location());

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imageView.setImageBitmap(myBitmap);

        }
    }

    @Override
    public int getItemCount() {
       return onGoingSessionDataList.size();
    }


    class SessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView info_textview;
        ImageView imageView;

        public SessionViewHolder(View itemView) {
            super(itemView);

            info_textview = (TextView) itemView.findViewById(R.id.info_textview);
            imageView = (ImageView) itemView.findViewById(R.id.coverImageView);

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

    public void SetOnItemClickListener(final SessionAdapter.OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void addItemsToList(OnGoingSessionData sessionData) {
        onGoingSessionDataList.add(0, sessionData);
        notifyDataSetChanged();


    }
}
