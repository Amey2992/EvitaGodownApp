package module.infosolutions.others.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infosolutions.evita.R;

import java.util.ArrayList;


/**
 *
 * @author S.R.Mishra on 15-08-2017.
 */



public class NotificationAdapter extends
        RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

    private ArrayList<String> notiList= new ArrayList<>();
    private Typeface custom_font;
    private Context mContext;

    public NotificationAdapter(Context mContext, ArrayList<String> listNotification) {
        this.mContext = mContext;
        this.notiList = listNotification;
        this.custom_font = Typeface.createFromAsset(mContext.getAssets(),  "fonts/Ubuntu-M.ttf");
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification, parent, false);
        return new NotificationAdapter.NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NotificationViewHolder notificationViewHolder, int position) {
        final String notificationModel = notiList.get(position);
        notificationViewHolder.tvNotiLabel.setTypeface(custom_font);
        notificationViewHolder.tvNotiLabel.setText(notificationModel);

        notificationViewHolder.tvNotiLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }

    @Override
    public int getItemCount() {
        return notiList.size();
    }


    public class NotificationViewHolder extends RecyclerView.ViewHolder{

        TextView tvNotiLabel;
        public NotificationViewHolder(View itemView) {
            super(itemView);
            tvNotiLabel = itemView.findViewById(R.id.tvNotificationLabel);
        }
    }
}
