package com.github.klauseakarlson.routemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NavigationActivity extends AppCompatActivity{

    private TextView LAddress;
    private Button BPrev, BNext, BDone, BGoogleMap;

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


        //BPrev sets the destination to the previous stop on the route
        BPrev=findViewById(R.id.navigation_BPrev);
        BPrev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                setDestination(_Navigator.previous());
            }
        });//end BPrev
        //BDone closes the current activity, return the user to the main activity
        BDone=findViewById(R.id.navigation_BDone);
        BDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });//end BDone
        //BNext sets the destination to the next active stop on the route, if one is available
        BNext=findViewById(R.id.navigation_BNext);
        BNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDestination(_Navigator.next());
            }
        });//end BNext
        /**
         * BGoogleMap launches google maps app with an intent to give the user directions to the
         * currently selected location
         */
        BGoogleMap=findViewById(R.id.BGoogleMap_NavigationActivity);
        BGoogleMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //launch google maps app using an intent
                /**
                 * Acoring to the Google maps documentation
                 * https://developers.google.com/maps/documentation/urls/android-intents
                 * The package should be "com.google.android.apps.maps"
                 * and the action should be Intent.ACTION_VIEW
                 * the Uri creation is placed in a separate function for clarity and modularity
                 */
                Intent mapIntent =getPackageManager().getLaunchIntentForPackage(
                        "com.google.android.apps.maps");
                if (mapIntent != null)//make sure google maps is installed
                {
                    Uri gmUri=Uri.parse( CreateGMapURL() );
                    mapIntent.setAction(Intent.ACTION_VIEW);
                    mapIntent.setData(gmUri);
                    startActivity(mapIntent);
                }else{
                    //tell user this aciton reqwuires Google Maps app
                    Toast errorMessage=Toast.makeText(
                            NavigationActivity.this,
                            "Google Maps not Installed",
                            Toast.LENGTH_LONG
                    );
                    errorMessage.show();
                }//end if else
            }//end onclick
        });//end BGoogleMap

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
        //text resizes to fill screen
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(LAddress,12,
                48,4, TypedValue.COMPLEX_UNIT_DIP);
    }

    private String CreateGMapURL()
    {
        Address destination = _Navigator.current();//get current address for navigation
        //https://developers.google.com/maps/documentation/urls/guide
        //we want directions
        String Url="https://www.google.com/maps/dir/?api=1";//base URL
        //paramaters are on seperate lines for clarity and ease of editing
        //create address string
        String rawAddress=destination.getStreet()+", "+destination.getCity()+", "+destination.getState();
        Url+= "&destination=" + Uri.encode(rawAddress);//this makes the string URL safe
        Url+= "&travelmode=driving";//users are expected to be driving a bus
        Url+= "dir_action=navigate";//we want navigation directions
        return Url;
    }
}//end navigation activity
