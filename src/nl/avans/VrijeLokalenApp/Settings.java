package nl.avans.VrijeLokalenApp;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
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

        this.prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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

        Button saveButton = (Button) findViewById(R.id.saveButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker startdatePicker = (DatePicker)findViewById(R.id.startdatePicker);
                DatePicker enddatePicker = (DatePicker)findViewById(R.id.enddatePicker);

                int startYear = startdatePicker.getYear();
                int startMonth = startdatePicker.getMonth() +1;
                int startDay =  startdatePicker.getDayOfMonth();

                int endYear = enddatePicker.getYear();
                int endMonth = enddatePicker.getMonth() +1;
                int endDay =  enddatePicker.getDayOfMonth();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("dateStart", startYear + "-" + startMonth + "-" + startDay);
                editor.putString("dateEnd", endYear + "-" + endMonth + "-" + endDay);
                editor.commit();
                System.out.println("Save Clicked");
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Cancel Clicked");
                finish();
            }
        });
    }

    private DatePicker.OnDateChangedListener dateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {
                if(view.equals(findViewById(R.id.startdatePicker))) {
                    System.out.println("dateStart " + year + "-" + monthOfYear + "-" + dayOfMonth);
                }
                if(view.equals(findViewById(R.id.enddatePicker))) {
                    System.out.println("dateEnd " + year + "-" + monthOfYear + "-" + dayOfMonth);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
