package com.infosolutions.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infosolutions.evita.R;
import com.infosolutions.model.StockReportListModel;
import com.infosolutions.model.StockReportModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by R D Mishra on 15-07-2017.
 */

public class StockReportsListAdapter extends RecyclerView.Adapter<StockReportsListAdapter.ReportViewHolder> {

    private Context mContext;
    private List<StockReportListModel> listModel = new ArrayList<>();
    private Typeface custom_font;

    public StockReportsListAdapter(Context mContext, List<StockReportListModel> moduleList)
    {
        this.mContext = mContext;
        this.listModel = moduleList;
        this.custom_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Ubuntu-M.ttf");
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_report_list, parent, false);
        return new ReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReportViewHolder holder, int position) {

        StockReportListModel album = listModel.get(position);
        holder.tvSV.setText("SV :"+album.getSV());
        holder.tvNAME.setText("NAME :"+album.getNAME());
        holder.tvDBC.setText("DBC :"+album.getDBC());
        holder.tvRETURN_FULL.setText("RETURN FULL :"+album.getRETURN_FULL());
        holder.tvFULL_CYLINDER.setText("FULL CYLINDER :"+album.getFULL_CYLINDER());
        holder.tvEMPTY.setText("EMPTY :"+album.getEMPTY());


        holder.tvSV.setTypeface(custom_font);
        holder.tvNAME.setTypeface(custom_font);
        holder.tvDBC.setTypeface(custom_font);
        holder.tvRETURN_FULL.setTypeface(custom_font);
        holder.tvFULL_CYLINDER.setTypeface(custom_font);
        holder.tvEMPTY.setTypeface(custom_font);
    }

    @Override
    public int getItemCount()
    {
        return listModel.size();
    }


    class ReportViewHolder extends RecyclerView.ViewHolder{

        private TextView tvSV;
        private TextView tvNAME;
        private TextView tvDBC;
        private TextView tvRETURN_FULL;
        private TextView tvFULL_CYLINDER;
        private TextView tvEMPTY;

        public ReportViewHolder(View itemView) {
            super(itemView);
            tvSV = itemView.findViewById(R.id.tvSV);
            tvNAME = itemView.findViewById(R.id.tvNAME);
            tvDBC = itemView.findViewById(R.id.tvDBC);
            tvRETURN_FULL = itemView.findViewById(R.id.tvRETURN_FULL);
            tvFULL_CYLINDER = itemView.findViewById(R.id.tvFULL_CYLINDER);
            tvEMPTY = itemView.findViewById(R.id.tvEMPTY);
        }
    }

}
