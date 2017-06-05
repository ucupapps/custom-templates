package salam.gohajj.id.menu;

/**
 * Created by thegoeh on 5/16/2017.
 */

public class items_hotel {

    String id;
    String nama;
    String alamat;
    String rating;
    String lat;
    String lng;

    public items_hotel(String id, String nama, String alamat, String rating, String lat, String lng)
    {
        this.id=id;
        this.nama=nama;
        this.alamat=alamat;
        this.rating=rating;
        this.lat=lat;
        this.lng=lng;
    }
    public String getId()
    {
        return id;
    }
    public String getNama()
    {
        return nama;
    }
    public String getAlamat()
    {
        return alamat;
    }
    public String getRating()
    {
        return rating;
    }
    public String getLat()
    {
        return lat;
    }
    public String getLng()
    {
        return lng;
    }

}
