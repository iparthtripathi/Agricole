package com.pratik.agricole.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratik.agricole.FarmDetails;
import com.pratik.agricole.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FarmAdapter extends RecyclerView.Adapter<FarmAdapter.ViewHolder>{


    Context context;
    ArrayList<FarmModel> farmModels;

    public FarmAdapter(Context context, ArrayList<FarmModel> farmModels) {
        this.context = context;
        this.farmModels = farmModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.farmveiw,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FarmModel model = farmModels.get(position);
        Picasso.get().load(model.farmimage).into(holder.farmimage);
        holder.farmsize.setText(model.farmsize + " Ha");

       holder.farmname.setText(model.farmname);
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(context, FarmDetails.class);
               intent.putExtra("farmnumber", model.farmnumber);
               context.startActivity(intent);
           }
       });
    }

    @Override
    public int getItemCount() {
        return farmModels.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView farmname , farmsize ;
        ImageView farmimage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            farmimage  = itemView.findViewById(R.id.farmimage);
            farmname = itemView.findViewById(R.id.framname);
            farmsize = itemView.findViewById(R.id.farmsize);


        }
    }
}
