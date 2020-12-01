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

import java.text.BreakIterator;
import java.util.List;

public class OrdersRecycleViewAdapter  extends RecyclerView.Adapter<OrdersRecycleViewAdapter.ViewHolder>{
    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private Context mContext;


    public OrdersRecycleViewAdapter(Context context, List<JSONObject> mData) {
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public OrdersRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rvorder_item, parent, false);
        return new OrdersRecycleViewAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(OrdersRecycleViewAdapter.ViewHolder holder, int position) {
        JSONObject order = mData.get(position);
        try {
            String oTicket = order.get("ticket").toString();
            String oStatus = order.get("status").toString();
            String tag = order.get("id").toString();

            JSONObject ticketJSON = new JSONObject(oTicket);
            String oTitle = ticketJSON.get("title").toString();

            System.out.println(ticketJSON);
            holder.myOrderTitle.setText(oTitle);
            holder.myOrderStatus.setText(oStatus);
            holder.btnOrderView.setTag(tag);
            holder.btnOrderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // move to OrderView Activity
                    Intent intent = new Intent(mContext, OrderViewActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("order_id", tag);
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
        public BreakIterator txtOrderTitle;
        TextView myOrderTitle;
        TextView myOrderStatus;
        Button btnOrderView;
        ViewHolder(View itemView) {
            super(itemView);
            myOrderTitle = itemView.findViewById(R.id.txtOrderTitle);
            myOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
            btnOrderView = itemView.findViewById(R.id.btnOrderView);
        }
    }

}

