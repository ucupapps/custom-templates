package com.garudatekno.jemaah.menu;

/**
 * Created by thegoeh on 5/16/2017.
 */

public class items {

    String id;
    String message;
    String time;
    String from;
    String jum;

    public items(String id,String message,String time,String from,String jum)
    {
        this.id=id;
        this.message=message;
        this.time=time;
        this.from=from;
        this.jum=jum;
    }
    public String getId()
    {
        return id;
    }
    public String getMessage()
    {
        return message;
    }
    public String getTime()
    {
        return time;
    }
    public String getFrom()
    {
        return from;
    }
    public String getJum()
    {
        return jum;
    }

}
