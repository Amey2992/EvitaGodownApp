package com.infosolutions.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.infosolutions.evita.R;

import java.util.ArrayList;
import java.util.List;


public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>{


    private Context mContext;
    private List<ModuleModel> moduleList = new ArrayList<>();

    public ModuleAdapter(Context mContext, List<ModuleModel> moduleList) {
        this.mContext = mContext;
        this.moduleList = moduleList;
    }


    @Override
    public ModuleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, parent, false);
        return new ModuleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( ModuleViewHolder holder, int position) {

        ModuleModel album = moduleList.get(position);
        holder.tvTextModule.setText(album.getModuleTitle());
        Glide.with(mContext).load(album.getModuleIconImage()).placeholder(R.drawable.ic_background_image).into(holder.ivImageModule);
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }


    class ModuleViewHolder extends RecyclerView.ViewHolder{

        ImageView ivImageModule;
        TextView tvTextModule;

        public ModuleViewHolder(View itemView) {
            super(itemView);
            ivImageModule = itemView.findViewById(R.id.ivModule);
            tvTextModule = itemView.findViewById(R.id.tvModule);
        }
    }
}
