package nl.avans.VrijeLokalenApp;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import nl.avans.oAuthdemo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SpinnerFragment extends Fragment {

    private Spinner roomSpinner;
    private Spinner dateSpinner;
    private onSpinnerChangedListener listener;
    private SharedPreferences prefs;
    private SimpleDateFormat niceFormat = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat uniformFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_spinners,
                container, false);

        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        roomSpinner = (Spinner) view.findViewById(R.id.room_spinner);
        dateSpinner = (Spinner) view.findViewById(R.id.date_spinner);

        fillSpinners();

        AdapterView.OnItemSelectedListener listenNow = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateResults();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };

        roomSpinner.setOnItemSelectedListener(listenNow);
        dateSpinner.setOnItemSelectedListener(listenNow);

        //checkSpinners();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fillSpinners();
    }

    public void checkSpinners() {
        if (dateSpinner.getAdapter().getCount() == 0) {
            dateSpinner.setEnabled(false);
        } else {
            dateSpinner.setEnabled(true);
        }

        if (roomSpinner.getAdapter().getCount() == 0) {
            roomSpinner.setEnabled(false);
        } else {
            roomSpinner.setEnabled(true);
        }
    }

    public void fillSpinners() {
        try {
            HashMap<String, HashMap<String, ArrayList<EmptyRoomEntry>>> data = ResultLoader
                    .getInstance()
                    .getAndParse();
            /* String to Date */
            ArrayList<Date> dates = new ArrayList<>();
            for (Object s : data.keySet().toArray()) {
                dates.add(uniformFormat.parse(s.toString()));
            }
            /* Sort ArrayList with Date Objects */
            Collections.sort(dates);


            /* Date to String */
            ArrayList<String> dateStrings = new ArrayList<>();
            for (Date d : dates) {
                dateStrings.add(niceFormat.format(d));
            }
            ArrayAdapter dateAdap = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, dateStrings);
            dateAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dateSpinner.setAdapter(dateAdap);

            ArrayList<String> classRooms = new ArrayList<>();
            for (Object s : data.get(getSelectedDate()).keySet().toArray()) {
                classRooms.add(s.toString());
            }

            /* Sort classRooms by id */
            Collections.sort(classRooms, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    String s1NumberString = s1.substring(s1.length() - 2);
                    String s2NumberString = s2.substring(s2.length() - 2);

                    int s1Number = Integer.parseInt(s1NumberString);
                    int s2Number = Integer.parseInt(s2NumberString);

                    if (s1Number > s2Number) {
                        return 1;
                    } else if (s1Number < s2Number) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            ArrayAdapter roomAdap = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, classRooms);
            roomAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            roomSpinner.setAdapter(roomAdap);
            roomSpinner.setSelection(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSelectedDate() {

        if (dateSpinner.getSelectedItem() != null) {

            try {
                Date d = niceFormat.parse(dateSpinner.getSelectedItem().toString());
                System.out.println(uniformFormat.format(d));
                return uniformFormat.format(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";


    }

    public String getSelectedRoom() {
        if (roomSpinner.getSelectedItem() == null) {
            return "";
        }
        return roomSpinner.getSelectedItem().toString();
    }

    public interface onSpinnerChangedListener {
        public void onSpinnerChanged(String date, String classRoom);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof onSpinnerChangedListener) {
            listener = (onSpinnerChangedListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement SpinnerFragment.onDateChangedListener " +
                    "or SpinnerFragment.onRoomChangedListener");
        }
    }

    public void updateResults() {
        listener.onSpinnerChanged(getSelectedDate(), getSelectedRoom());
    }
} 