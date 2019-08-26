package com.github.klauseakarlson.routemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StopEditorActivity extends AppCompatActivity {

    public static final String SelectedAddress="SelectedAddress";

    private Address _Stop;
    private EditText TxName, TxStreet,TxCity, TxState;
    private Button BSave, BDelete;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_editor);

        //retrieve address being edited
        Route route=Route.getInstance();//singleton pattern used to pass data around
        int selectedPosition=getIntent().getIntExtra(SelectedAddress, route.size()-1);
        _Stop=route.get(selectedPosition);
        //bind buttons
        BSave=findViewById(R.id.BSaveStop);
        BSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContents();
                finish();
            }
        });

        BDelete=findViewById(R.id.BDeleteStop);
        BDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Route.getInstance().remove(_Stop);
                finish();
            }
        });
        //bind and fill text boxes
        TxName=findViewById(R.id.TxName_StopEditor);
        TxName.setText(_Stop.getName());

        TxStreet=findViewById(R.id.TxStreet_StopEditor);
        TxStreet.setText(_Stop.getStreet());

        TxCity=findViewById(R.id.TxCity_StopEditor);
        TxCity.setText(_Stop.getCity());

        TxState=findViewById(R.id.TxState_StopEditor);
        TxState.setText(_Stop.getState());
    }//end on create

    public void saveContents()
    {
        _Stop.setStreet(TxStreet.getText().toString());
        _Stop.setName(TxName.getText().toString());
        _Stop.setCity(TxCity.getText().toString());
        _Stop.setState(TxState.getText().toString());
    }
}
