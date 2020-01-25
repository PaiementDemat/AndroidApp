package com.paiementdemat.mobilepay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {
    private ArrayList<History> listData;
    private LayoutInflater layoutInflater;

    public HistoryAdapter(Context aContext, ArrayList<History> listData){
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount(){
        return listData.size();
    }

    @Override
    public Object getItem(int position){
        return listData.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View v, ViewGroup vg){
        ViewHolder holder;
        if(v == null){
            v = layoutInflater.inflate(R.layout.item_history, null);
            holder = new ViewHolder();
            holder.uShop = (TextView) v.findViewById(R.id.item_shop_title);
            holder.uAmount = (TextView) v.findViewById(R.id.item_price);
            holder.uDate = (TextView) v.findViewById(R.id.shop_date);

            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.uShop.setText(listData.get(position).getShop());
        holder.uAmount.setText(listData.get(position).getAmount());
        holder.uDate.setText(listData.get(position).getCreatedAt());
        return v;
    }

    static class ViewHolder{
        TextView uShop;
        TextView uAmount;
        TextView uDate;
    }
}
