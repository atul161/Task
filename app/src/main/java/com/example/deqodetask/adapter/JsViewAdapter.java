package com.example.deqodetask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deqodetask.Model.ProgressModel;
import com.example.deqodetask.R;
import com.example.deqodetask.Utils.Helper;

import java.util.ArrayList;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class JsViewAdapter extends RecyclerView.Adapter<JsViewAdapter.ViewHolder> {

     private ArrayList<ProgressModel> progressModels;
     private Context context;

     public JsViewAdapter(Context context, ArrayList<ProgressModel> models){
         this.progressModels = models;
         this.context = context;
     }

    @NonNull
    @Override
    public JsViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(context).inflate(R.layout.item_view , parent , false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JsViewAdapter.ViewHolder holder, int position) {
        holder.operation.setText(Helper.OPERATION +progressModels.get(position).getOperation());
        holder.message.setText(Helper.MESSAGE + progressModels.get(position).getMessage());
        holder.circularProgressIndicator.setMaxProgress(100);
        holder.circularProgressIndicator.setCurrentProgress(progressModels.get(position).getProgress());
    }


    public void updateData(ArrayList<ProgressModel> model){
         this.progressModels.clear();
         this.progressModels.addAll(model);
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public int getItemCount() {
        return progressModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

         private TextView operation, message;
         private CircularProgressIndicator circularProgressIndicator;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            operation = itemView.findViewById(R.id.operation);
            message = itemView.findViewById(R.id.message);
            circularProgressIndicator = itemView.findViewById(R.id.circular_progress);
        }
    }
}
