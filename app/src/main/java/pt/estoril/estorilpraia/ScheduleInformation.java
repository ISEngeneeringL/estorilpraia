package pt.estoril.estorilpraia;

import java.util.ArrayList;

/**
 * Created by Leonardo on 19/02/2017.
 */
public class ScheduleInformation {

    public String time;

    public long numUsers;
    public boolean booked;
    public ArrayList<String> users;

    public ScheduleInformation(){

    }

    public ScheduleInformation(boolean booked, long numUsers, String time, ArrayList<String> users) {
        this.time = time;
        this.booked = booked;
        this.numUsers = numUsers;
        this.users = users;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(long numUsers) {
        this.numUsers = numUsers;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }
}
