package com.github.klauseakarlson.routemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView LAddress;
    private Button BPrev, BNext, BDone;

    private Route.Navigator _Navigator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        //data is passed between instances using the singleton pattern
        _Navigator =Route.getInstance().getNavigator();

        //bind LAddress
        LAddress=findViewById(R.id.navigation_LAddress);
        ///bind buttons
        BPrev=findViewById(R.id.navigation_BPrev);
        BPrev.setOnClickListener(this);
        BDone=findViewById(R.id.navigation_BDone);
        BDone.setOnClickListener(this);
        BNext=findViewById(R.id.navigation_BNext);
        BNext.setOnClickListener(this);

        //show current destination
        setDestination(_Navigator.current());
    }

    protected void setDestination(Address stop)
    {
        //assigns the next destination to the provided stop
        //any changes to behavior should override/replace this
        String destination=stop.getName()+"\n"+
                stop.getStreet()+"\n"+
                stop.getCity()+", "+stop.getState();
        LAddress.setText(destination);

        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(LAddress,12,
                48,4, TypedValue.COMPLEX_UNIT_DIP);
    }

    @Override
    public void onClick(View view) {
        if (view==BDone)
        {
            finish();
        }else if(view==BPrev)
        {
            setDestination(_Navigator.previous());
        }else if(view==BNext)
        {
            setDestination(_Navigator.next());
        }
    }
}
