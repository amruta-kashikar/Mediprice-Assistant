package com.mediprice.assistant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mediprice.assistant.R;
import com.mediprice.assistant.model.hospitalModel;

import java.util.ArrayList;

public class myadapter extends RecyclerView.Adapter<myadapter.myviewholder>
{
    ArrayList<hospitalModel> datalist;

    public myadapter(ArrayList<hospitalModel> datalist) {
        this.datalist = datalist;
    }
    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_details,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.t1.setText(datalist.get(position).getName());
        holder.t2.setText(datalist.get(position).getPhone());
        holder.t3.setText(datalist.get(position).getEmail());
        holder.t4.setText(datalist.get(position).getPrice());
//        holder.t5.setText(String.valueOf(datalist.get(position).getVacant()));
        holder.t5.setText(datalist.get(position).getFacility());
        holder.t6.setText(datalist.get(position).getArea());
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public void filteredList(ArrayList<hospitalModel> filterList) {
        datalist = filterList;
        notifyDataSetChanged();
    }

    class myviewholder extends RecyclerView.ViewHolder {

        TextView t1,t2,t3,t4,t5,t6;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(R.id.t1);
            t2 = itemView.findViewById(R.id.t2);
            t3 = itemView.findViewById(R.id.t3);
            t4 = itemView.findViewById(R.id.t4);
            t5 = itemView.findViewById(R.id.t5);
            t6 = itemView.findViewById(R.id.t6);
        }
    }
}
