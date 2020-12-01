package com.example.gittixapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TicketsRecyclerViewAdapter extends RecyclerView.Adapter<TicketsRecyclerViewAdapter.ViewHolder> {
    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private Context mContext;


    public TicketsRecyclerViewAdapter(Context context, List<JSONObject> mData) {
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rvtickets_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject ticket = mData.get(position);
        try {
            String tTitle = ticket.get("title").toString();
            String tPrice = ticket.get("price").toString();
            String tag = ticket.get("id").toString();

            holder.myTextView.setText(tTitle);
            holder.myPrice.setText(tPrice);
            holder.btnView.setTag(tag);
            holder.btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // move to TicketView Activity
                    Intent intent = new Intent(mContext, TicketViewActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("ticket_id", tag);
                    mContext.startActivity(intent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView myTextView;
        TextView myPrice;
        Button btnView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.myTextView);
            myPrice = itemView.findViewById(R.id.myPrice);
            btnView = itemView.findViewById(R.id.btnView);
        }


    }


}
