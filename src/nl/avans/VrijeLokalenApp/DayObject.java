package nl.avans.VrijeLokalenApp;

import java.util.ArrayList;

/**
 * Created by PimGame on 8-4-2015.
 */
public class DayObject {

    private String date;
    private EmptyRoomEntry[] entries;


    public DayObject() {
        entries = new EmptyRoomEntry[16];
    }

    public void addEntry(EmptyRoomEntry ere) {
        int collegeHour = ere.getCollegeHour();
        if (collegeHour >= 0 && collegeHour < entries.length) {
            entries[collegeHour] = ere;
        }
    }

    public ArrayList<EmptyRoomEntry> getEmptyRooms() {
        ArrayList<EmptyRoomEntry> empties = new ArrayList<>();
        for (EmptyRoomEntry ere : this.entries) {
            if (!ere.isOccupied()) {
                empties.add(ere);
            }
        }
        return empties;
    }


}
