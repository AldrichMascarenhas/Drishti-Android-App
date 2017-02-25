package com.example.semicolon.drishti;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.vipul.hp_hp.timelineview.TimelineView;

/**
 * Created by Sarvesh on 24-02-2017.
 */

public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    public TextView date;
    public TextView name;
    public TextView location;

    public TimelineView mTimelineView;
    public ImageView imageView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.tv_name);
        date = (TextView) itemView.findViewById(R.id.tv_date);
        location = (TextView) itemView.findViewById(R.id.tv_location);
        imageView = (ImageView) itemView.findViewById(R.id.tv_image);




        mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }


}
