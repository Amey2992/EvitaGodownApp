package com.infosolutions.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infosolutions.evita.R;

import java.util.ArrayList;
import java.util.List;



public class ChipSelectionAdapter extends RecyclerView.Adapter<ChipSelectionAdapter.ChipViewHolder>{

    private Context mContext;
    private List<ChipModel> chipList = new ArrayList<>();
    private int selectedPosition=-1;


    public ChipSelectionAdapter(Context mContext, List<ChipModel> moduleList) {
        this.mContext = mContext;
        this.chipList = moduleList;
    }


    @Override
    public ChipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chip_view, parent, false);
        return new ChipViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChipViewHolder holder, final int position) {

        final ChipModel album = chipList.get(position);
        holder.tvChipTitle.setText(album.getChipTitle());

        if(selectedPosition==position){
            holder.tvChipTitle.setBackgroundResource(R.drawable.chip_background);
        } else {
            holder.tvChipTitle.setBackgroundResource(R.drawable.chip_not_selected_background);
        }

        holder.tvChipTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition=position;
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return chipList.size();
    }


    class ChipViewHolder extends RecyclerView.ViewHolder{

        TextView tvChipTitle;

        public ChipViewHolder(View itemView) {
            super(itemView);

            tvChipTitle = itemView.findViewById(R.id.tvChipView);
        }
    }
}
