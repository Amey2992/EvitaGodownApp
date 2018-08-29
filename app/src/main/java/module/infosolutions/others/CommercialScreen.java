package module.infosolutions.others;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.infosolutions.evita.R;

import module.infosolutions.others.Adapters.CommercialPagerAdapter;


public class CommercialScreen extends AppCompatActivity {

    private CommercialPagerAdapter adapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commercial);
        if (Build.VERSION.SDK_INT >= 21){ getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));}
        setToolbarHost();

    }

    private void setToolbarHost()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Commercial");
        setSupportActionBar(toolbar);

        adapter = new CommercialPagerAdapter(getSupportFragmentManager());
        //mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }




}
