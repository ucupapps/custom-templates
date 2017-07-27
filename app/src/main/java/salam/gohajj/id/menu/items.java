package salam.gohajj.id.menu;

/**
 * Created by thegoeh on 5/16/2017.
 */

public class items {

    String id;
    String message;
    String time;
    String userid;
    String from;
    String jum;
    String sts;

    public items(String id,String message,String time,String userid,String from,String jum,String sts)
    {
        this.id=id;
        this.message=message;
        this.time=time;
        this.userid=userid;
        this.from=from;
        this.jum=jum;
        this.sts=sts;
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
    public String getUserid()
    {
        return userid;
    }
    public String getFrom()
    {
        return from;
    }
    public String getJum()
    {
        return jum;
    }
    public String getStatus()
    {
        return sts;
    }

}
