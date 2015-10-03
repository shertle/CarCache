package com.carcache.carcache.Models;

import java.util.Date;
import android.location.Location;

/**
 * Created by peterpei98 on 10/3/15.
 */
public class CCuser {
    private Date curTime;
    private Location curLoc;

    /*
    Construct a new CCuser object that contains the date and location
     */
    public CCuser(Date d, Location l)
    {
        curTime = d;
        curLoc = l;
    }

    /*
    set the Date
     */
    public void setDate(Date d)
    {
        curTime = d;
    }

    /*
    set the Location
     */
    public void setLocation(Location l)
    {
        curLoc = l;
    }

    /*
    returns date of this CCuser
     */
    public Date getDate()
    {
        return curTime;
    }

    /*
    returns Location of this CCuser
     */
    public Location getLocation()
    {
        return curLoc;
    }
}
