package nl.avans.VrijeLokalenApp;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.DatePicker;
import nl.avans.oAuthdemo.R;

import java.util.Calendar;

/**
 * Created by Daniel on 4/11/2015.
 */
public class Settings extends Activity {

    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Calendar c = Calendar.getInstance();

        DatePicker startdatePicker = (DatePicker)findViewById(R.id.startdatePicker);
        DatePicker enddatePicker = (DatePicker)findViewById(R.id.enddatePicker);

        int startYear = c.get(Calendar.YEAR);
        int startMonth = c.get(Calendar.MONTH);
        int startDay =  c.get(Calendar.DAY_OF_MONTH);

        int endYear = c.get(Calendar.YEAR);
        int endMonth = 1;
        if(startMonth < 12) {
            endMonth = c.get(Calendar.MONTH) + 1;
        }
        int endDay =  c.get(Calendar.DAY_OF_MONTH);


        startdatePicker.init(startYear, startMonth, startDay, dateChangedListener);
        enddatePicker.init(endYear, endMonth, endDay, dateChangedListener);
    }

    private DatePicker.OnDateChangedListener dateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            System.out.println("Test " + year + "-" + monthOfYear + "-" + dayOfMonth);
        }
    };
}
