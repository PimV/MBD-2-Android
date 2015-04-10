package nl.avans.VrijeLokalenApp;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import nl.avans.oAuthdemo.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultFragment extends Fragment {

    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.result_table,
                container, false);

        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());


        return view;
    }

    public void update(String date, String classRoom) throws Exception {
        HashMap<String, HashMap<String, ArrayList<EmptyRoomEntry>>> data = ResultLoader
                .getInstance()
                .getAndParse();
        if (!data.containsKey(date)) {
            System.out.println("No entry found with this date");
            return;
        }
        for (EmptyRoomEntry myEntry : data.get(date).get(classRoom)) {
            String statusId = "ch_status_" + (myEntry.getCollegeHour());
            int resId = getResources().getIdentifier(statusId, "id", OAuthFlowApp.PACKAGE_NAME);
            TextView tv = (TextView) getView().findViewById(resId);
            if (myEntry.isOccupied()) {
                tv.setBackgroundColor(getResources().getColor(R.color.red));
                tv.setText("BEZET");
            } else {
                tv.setBackgroundColor(getResources().getColor(R.color.green));
                tv.setText("VRIJ");
            }
        }
    }
} 