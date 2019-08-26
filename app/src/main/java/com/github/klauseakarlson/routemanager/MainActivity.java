package com.github.klauseakarlson.routemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{



    private static final int _RC_WriteExternalStorage=1;

    private Route _Route;//all of our data will be stored here, this is a singleton that can be accessed by otehr activities
    private RecyclerView _StopList;


    private Button BStart, BEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _Route=Route.getInstance();
        prepareSaveFile();
        //initiate buttons
        BStart=findViewById(R.id.BStart);
        BStart.setOnClickListener(this);
        BEdit=findViewById(R.id.BEdit);
        BEdit.setOnClickListener(this);
        //initiate recycler view
        _StopList=findViewById(R.id.StopListView);
        _StopList.hasFixedSize();
        _StopList.setLayoutManager(new LinearLayoutManager(this));
        _StopList.setAdapter(new StopSelectionAdapter());
    }

    private void prepareSaveFile()
    {
        _Route.openOrCreateDB(this);//cannot load or saved until this function is called
        _Route.load();//will clear the route if the singleton is still active
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        _StopList.getAdapter().notifyDataSetChanged();//lets just assume it changed
    }

    @Override
    protected void onDestroy()
    {
        _Route.Save();//save route when closing
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view==BEdit)
        {
            Intent toEdit=new Intent(this, RouteEditorActivity.class);
            startActivity(toEdit);//data transfer is handled by the Route singleton class
        }else if (view==BStart)
        {
            Intent toNavigate=new Intent(this, NavigationActivity.class );
            startActivity(toNavigate);
        }
    }

    public class StopSelectionAdapter extends RecyclerView.Adapter<StopSelectionAdapter.StopSelector>
    {
        @NonNull
        @Override
        public StopSelector onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(parent.getContext());
            View v=inflater.inflate(R.layout.stop_selection_view,parent,false);
            return new StopSelector(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StopSelector holder, int position) {
            Address a=_Route.get(position);
            String name =a.getName();
            Boolean active=a.getActive();
            //Log.d("Main:RecycleView", "Bind: "+name+":"+active);//debug
            holder.Name.setText(name);
            holder.Selector.setChecked(active);
            holder.Position=position;

        }

        @Override
        public int getItemCount() {
            return _Route.size();
        }

        public class StopSelector extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            public CheckBox Selector;
            public TextView Name;
            public int Position;
            public StopSelector(View StopView)
            {
                super(StopView);
                Selector=StopView.findViewById(R.id.StopSelectionView_checkBox);
                Selector.setOnClickListener(this);
                Name=StopView.findViewById(R.id.StopSelectionView_Name);
                Position=-1;//prevent onClick calls from editing adress states until bound
            }

            @Override
            public void onClick(View view) {
                //if within boundry, syncronize address state with selector
                if(Position>=0&&Position<_Route.size())
                    _Route.get(Position).setActive(Selector.isChecked());
            }
        }//end stop selector

    }//end check box adapter
}//end main activity
