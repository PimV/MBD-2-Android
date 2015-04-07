package nl.avans.oAuthdemo;

import java.io.Serializable;

/**
 * Created by PimGame on 7-4-2015.
 */
public class EmptyRoomEntry implements Serializable {
   /* Result Example
       {
           "datum": "2015-04-07",
               "lesuur": 0,
               "ruimte": "OB207",
               "grootte": 16,
               "type": "VL",
               "bezet": 0
       }
   */

    private String date;
    private int collegeHour;
    private String classRoom;
    private int roomSize;
    private String type; //Perhaps to enum?
    private boolean occupied;

    public EmptyRoomEntry() {
        //Load defaults
        date = "NOT_SET";
        collegeHour = -1;
        classRoom = "NOT_SET";
        roomSize = -1;
        type = "NOT_SET";
        occupied = false;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCollegeHour() {
        return collegeHour;
    }

    public void setCollegeHour(int collegeHour) {
        this.collegeHour = collegeHour;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public int getRoomSize() {
        return roomSize;
    }

    public void setRoomSize(int roomSize) {
        this.roomSize = roomSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @Override
    public String toString() {
        return collegeHour + "(lokaal: " + classRoom + "): " + occupied;
    }
}
