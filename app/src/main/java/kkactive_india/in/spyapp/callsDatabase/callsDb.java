package kkactive_india.in.spyapp.callsDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class callsDb {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "call_type")
    private String type;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "duration")
    private String duration;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }
}
