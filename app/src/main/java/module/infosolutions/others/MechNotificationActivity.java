package module.infosolutions.others;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.infosolutions.evita.R;

import java.util.ArrayList;

import module.infosolutions.others.Adapters.NotificationAdapter;


public class MechNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerNotification;
    private NotificationAdapter mAdapter;
    private ArrayList<String> listNotification = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech_notification);

        setToolBar();
        uiInitialise();
    }

    private void uiInitialise()
    {
        recyclerNotification = findViewById(R.id.recyclerNotification);
        listNotification.add("You have to grow from the inside out. None can teach you, " +
                "none can make you spiritual. There is no other teacher but your own soul.");
        listNotification.add("We are what our thoughts have made us; so take care about what you think. " +
                "Words are secondary. Thoughts live; they travel far.");
        listNotification.add("The world is the great gymnasium where we come to make ourselves strong.");
        listNotification.add("All the powers in the universe are already ours. " +
                "It is we who have put our hands before our eyes and cry that it is dark.");


        setDataForNotificationList();
    }


    private void setDataForNotificationList(){
        mAdapter = new NotificationAdapter(getApplicationContext(),listNotification);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerNotification.setLayoutManager(mLayoutManager);
        recyclerNotification.setItemAnimator(new DefaultItemAnimator());
        recyclerNotification.setAdapter(mAdapter);
    }


    private void setToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.notification);
        setSupportActionBar(toolbar);
    }
}
