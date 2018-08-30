package com.infosolutions.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infosolutions.evita.R;
import com.infosolutions.model.StockReportModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by R D Mishra on 15-07-2017.
 */

public class StockReportAdapter extends RecyclerView.Adapter<StockReportAdapter.ReportViewHolder> {

    private Context mContext;
    private List<StockReportModel> moduleList = new ArrayList<>();

    public StockReportAdapter(Context mContext, List<StockReportModel> moduleList)
    {
        this.mContext = mContext;
        this.moduleList = moduleList;
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_report, parent, false);
        return new ReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReportViewHolder holder, int position) {

        StockReportModel album = moduleList.get(position);
        holder.tvTextModule.setText(album.getStockReportTitle());
    }

    @Override
    public int getItemCount()
    {
        return moduleList.size();
    }


    class ReportViewHolder extends RecyclerView.ViewHolder{

        TextView tvTextModule;

        public ReportViewHolder(View itemView) {
            super(itemView);
            tvTextModule = itemView.findViewById(R.id.tvModule);

        }
    }

}
