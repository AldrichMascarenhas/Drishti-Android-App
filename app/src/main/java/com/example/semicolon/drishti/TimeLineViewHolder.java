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
    public TimelineView mTimelineView;
    public ImageView imageView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.tv_name);
        date = (TextView) itemView.findViewById(R.id.tv_date);
        imageView = (ImageView) itemView.findViewById(R.id.image_letter);

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color1 = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound("A", color1);
        imageView.setImageDrawable(drawable);


        mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }


}
