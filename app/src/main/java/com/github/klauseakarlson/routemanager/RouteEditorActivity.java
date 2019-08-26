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
import android.widget.TextView;

public class RouteEditorActivity extends AppCompatActivity implements View.OnClickListener{

    private Route _Route;
    private Button BDone,BNewAddress;
    private  RecyclerView _Editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_editor);

        _Route=Route.getInstance();//data is shared between activities using the singleton pattern

        //prepare buttons
        BDone=findViewById(R.id.BDone);
        BDone.setOnClickListener(this);
        BNewAddress=findViewById(R.id.BNewAddress);
        BNewAddress.setOnClickListener(this);
        //prepare editing recycler view
        _Editor=findViewById(R.id.StopEditorView);
        _Editor.hasFixedSize();
        _Editor.setLayoutManager(new LinearLayoutManager(this));
        _Editor.setAdapter(new StopEditAdapter());

    }
    @Override
    protected void onResume()
    {
        super.onResume();
        _Editor.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if(view==BDone)
            this.finish();
        else if(view==BNewAddress)
            CreateNewAddress();
    }

    private void CreateNewAddress()
    {
        Address blank=new Address();
        _Route.add(blank);
        _Editor.getAdapter().notifyDataSetChanged();
    }

    protected void onStop()
    {
        super.onStop();
    }

    public class StopEditAdapter extends RecyclerView.Adapter<StopEditAdapter.StopHolder>
    {
        @NonNull
        @Override
        public StopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(parent.getContext());
            View v=inflater.inflate(R.layout.view_stop_editor,parent,false);
            StopHolder holder =new StopHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull StopHolder holder, int position) {
            //now load new data
            Address a=_Route.get(position);
            holder.Position=position;
            holder.LName.setText(a.getName());
            holder.LAddress.setText(a.getStreet()+"\n"+a.getCity()+", "+a.getState());
        }

        @Override
        public int getItemCount() {
            return _Route.size();
        }


        public class StopHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            public int Position;
            public TextView LName, LAddress;
            public Button BUp,BDown, BEdit;
            public StopHolder(@NonNull View itemView) {
                super(itemView);
                Position=-1;
                //bind labels
                LName=itemView.findViewById(R.id.LNameDisplay_StopView);
                LAddress=itemView.findViewById(R.id.LAddressDisplay_StopView);
                //bind buttons
                BUp=itemView.findViewById(R.id.BUp);
                BUp.setOnClickListener(this);
                BDown=itemView.findViewById(R.id.BDown);
                BDown.setOnClickListener(this);
                BEdit =itemView.findViewById(R.id.BEdit);
                BEdit.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (view==BUp)
                {
                    if(_Route.moveUp(Position) ) {
                        notifyDataSetChanged();
                    }
                }else if(view==BDown)
                {
                    if (_Route.moveDown(Position) ) {
                        notifyDataSetChanged();
                    }
                }else if(view== BEdit)
                {
                    Intent toEdit=new Intent(RouteEditorActivity.this, StopEditorActivity.class);
                    toEdit.putExtra(StopEditorActivity.SelectedAddress, Position);
                    startActivity(toEdit);
                }

            }//end click listener

        }//end stop editor
    }//end stop edit adapter
}//end rotue editor
