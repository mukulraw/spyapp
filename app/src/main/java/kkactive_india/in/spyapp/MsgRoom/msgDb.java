package kkactive_india.in.spyapp.MsgRoom;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;

public class msgDb {

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



}
