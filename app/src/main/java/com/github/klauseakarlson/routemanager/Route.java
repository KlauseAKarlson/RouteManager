package com.github.klauseakarlson.routemanager;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.LinkedList;

public class Route{
    //use the singleton pattern to access the same route from different activities
    //This is safe because the application is not online and only uses one route on a single machine
    //if these change the program should be refactored to use SQLite, possible the Room persistance library
    private static Route _Instance;
    //data storage
    private LinkedList<Address> _Route;
    //SQL names
    public static final String DataBaseName="RouteManager.db";
    private SaveHelper _Save;//database containing saves
    private Boolean _DBReady =false;//used to ensure DB and table are present for save and load ops
    public static final String TableName="Route",
        CName="Name", CStreet="Street", CCity="City", CState="State",
        CActive="Active", CPosition="Position";

    public static Route getInstance()
    {
        if (_Instance==null)
            _Instance=new Route();
        return _Instance;
    }
    private Route()
    {
        _Route=new LinkedList<Address>();

    }
    public void openOrCreateDB(Context context)
    {
        //creates the database helper for saving the route
        _DBReady =true;
        _Save=new SaveHelper(context);
    }
    public void load()
    {
        //this will clear the rout, it should not be used to add more stops to an existing route
        //first ensure that the database is accessable
        if(_DBReady) {
            SQLiteDatabase save=_Save.getReadableDatabase();
            //create cursor
            Cursor routeReader = save.rawQuery("SELECT * from "+TableName + " ORDER BY "+ CPosition,null);
            //store collumn numbers for access in loop
            int Name = routeReader.getColumnIndex(CName), Street = routeReader.getColumnIndex(CStreet),
                    City = routeReader.getColumnIndex(CCity), State = routeReader.getColumnIndex(CState),
                    Active = routeReader.getColumnIndex(CActive);

            _Route.clear();
            Address stop;
            //load route into local storage
            while (routeReader.moveToNext()) {
                //read row into an address object
                stop = new Address();
                stop.setName(routeReader.getString(Name));
                stop.setStreet(routeReader.getString(Street));
                stop.setCity(routeReader.getString(City));
                stop.setState(routeReader.getString(State));
                stop.setActive(routeReader.getInt(Active) > 0);
                if (!_Route.contains(stop))//prevent collision
                    _Route.add(stop);
            }
        }
    }//end load route from sqlite
    public void Save()
    {
        //first ensure that the Database is accessable
        if (_DBReady) {
            SQLiteDatabase save=_Save.getWritableDatabase();
            //first remove old values, then add in the new ones
            save.execSQL("DELETE FROM " + TableName);
            Address stop;
            ContentValues values;
            for (int position=0;position<_Route.size();position++) {
                //fill variables
                values=new ContentValues();
                stop=_Route.get(position);
                //fill values
                values.put(CName,stop.getName());
                values.put(CStreet,stop.getStreet());
                values.put(CCity,stop.getCity());
                values.put(CState,stop.getState());
                values.put(CActive,stop.getActive());
                values.put(CPosition,position);//index isn't stored in the Address object
                save.insert(TableName,null,values);

            }//end loop
        }
    }//end save


    public Address get(int index)
    {
        return _Route.get(index);
    }
    public int size()
    {
        return _Route.size();
    }

    public void remove(int index)
    {
        _Route.remove(index);
    }
    public void remove(Address a)
    {
        _Route.remove(a);
    }

    public void add(Address a)
    {
        _Route.add(a);
    }

    public boolean moveUp(int index)
    {
        //swaps the address with the one before it
        //returns true only if the change has occured
        if (index > 0 && index< _Route.size() )//boundry checking
        {
            Address temp= _Route.remove(index-1);
            _Route.add(index, temp);
            return true;
        }
        else
            return false;
    }//end move up
    public boolean moveDown(int index)
    {
        //swaps the address with the one after it
        if (index >= 0 && index< _Route.size()-1 )//boundry checking
        {
            Address temp= _Route.remove(index+1);
            _Route.add(index, temp);
            return true;
        }else
            return false;
    }

    public Navigator getNavigator()
    {
        return new Navigator();
    }

    public class Navigator{
        private int position =0;
        private LinkedList<Address> _ActiveRoute;

        private Navigator()
        {
            //add all active stops from the route to the active route
            _ActiveRoute=new LinkedList<Address>();
            for (Address stop:_Route)
            {
                if (stop.getActive())
                    _ActiveRoute.add(stop);
            }
        }
        public Address remember()
        {
            //looks at previous stop without changing current position
            if (position >1)
                return _ActiveRoute.get(position -1);
            else
                return _ActiveRoute.get(position);
        }
        public Address current()
        {
            return _ActiveRoute.get(position);
        }

        public Address peek()
        {
            //provides the next stop without moving the position
            if(position<_ActiveRoute.size()-1)
                return _ActiveRoute.get(position+1);
            else
                return _ActiveRoute.get(position);
        }
        public Address next()
        {
            //moves the position to the next stop and then returns the address
            if (position <_ActiveRoute.size()-1)
                position++;
            return _ActiveRoute.get(position);
        }

        public Address previous()
        {
            //moves to the previous stop and provides the address
            if (position >0)
                position--;
            return _ActiveRoute.get(position);
        }
    }//end navigator

    private class SaveHelper extends SQLiteOpenHelper
    {

        public SaveHelper(Context context) {
            super(context, DataBaseName, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //create table
            db.execSQL("CREATE TABLE IF NOT EXISTS "+TableName +"(" +
                    CName+" TEXT PRIMARY KEY, " +
                    CStreet+ " TEXT Not Null, " +
                    CCity + " TEXT Not Null, " +
                    CState+" TEXT Not Null, " +
                    CActive +" BOOLEAN, " +
                    CPosition +" INTEGER Not Null )");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //do nothing
        }
    }
}//end route
