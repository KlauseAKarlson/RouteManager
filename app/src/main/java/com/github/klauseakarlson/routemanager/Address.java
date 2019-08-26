package com.github.klauseakarlson.routemanager;

public class Address {
    private String _Name, _Street, _City, _State;
    private boolean _Active;

    public static final char seperator=',';//the tab key is not expected on an android keyboard
    public static final char active='T', inactive='F';//used to save active/inactive state to string

    public Address()
    {
        _Active=true;
        _Name="New Address";
        _Street="Street";
        _City="City";
        _State="State";
    }

    public String toString()
    {
        //write the address to a string
        return (_Active ? active : inactive) + seperator
                + _Name + seperator
                +_Street+seperator
                +_City+seperator
                +_State;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Address)
        {
            Address a=(Address)o;
            return(_Name==a._Name && _Street==a._Street && _City==a._City && _State==a._State);
            //ignore _Active, as it is a variable state rather than a unique identifier
        }
        else
            return false;

    }
    public boolean isValid()
    {
        return _City!="" && _State!="" && _Street != "" && _Name!="Empty";
    }
    //getters and setters
    public boolean getActive()
    {
        return _Active;
    }
    public String getCity() {
        return _City;
    }

    public String getName() {
        return _Name;
    }

    public String getState() {
        return _State;
    }

    public String getStreet() {
        return _Street;
    }
    public void setActive(boolean active)
    {
        this._Active=active;
    }

    public void setName(String Name) {
        this._Name = Name;
    }

    public void setStreet(String Street) {
        this._Street = Street;
    }

    public void setCity(String City) {
        this._City = City;
    }

    public void setState(String State) {
        this._State = State;
    }
}
