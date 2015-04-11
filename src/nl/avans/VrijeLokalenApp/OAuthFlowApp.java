package nl.avans.VrijeLokalenApp;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import nl.avans.oAuthdemo.R;
import oauth.signpost.OAuth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/**
 * Entry point in the application.
 * Launches the OAuth flow by starting the PrepareRequestTokenActivity
 */
public class OAuthFlowApp extends Activity implements SpinnerFragment.onSpinnerChangedListener {
    final String TAG = getClass().getName();
    private SharedPreferences prefs;
    private ResultFragment resultFragment;

    public static String PACKAGE_NAME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get preferences
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (this.prefs.getString(OAuth.OAUTH_TOKEN, "").isEmpty() || this.prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "").isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Inloggen vereist")
                    .setMessage("Om deze applicatie te gebruiken, moet u ingelogd zijn via Avans.")
                    .setPositiveButton("Inloggen", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           startLogin();
                        }
                    })
                    .setNegativeButton("Afsluiten", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }

        SharedPreferences.Editor editor = prefs.edit();
        if (!prefs.contains("dateStart")) {
            editor.putString("dateStart", "2015-03-05");
        }
        if (!prefs.contains("dateEnd")) {
            editor.putString("dateEnd", "2015-03-12");
        }
        if (!prefs.contains("rooms")) {
            editor.putString("rooms", "OB2");
        }
        editor.commit();

        ResultLoader.getInstance().setContext(getApplicationContext());
        setContentView(R.layout.main);

        resultFragment = (ResultFragment) getFragmentManager().findFragmentById(R.id.resultFragment);

        PACKAGE_NAME = getApplicationContext().getPackageName();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                clearCredentials();
                break;
            case R.id.refresh:
                try {
                    resultFragment.update(prefs.getString("currentDate", ""), prefs.getString("currentRoom", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.login:
               startLogin();
                break;
            case R.id.settings:
                startSettings();
                break;
            case R.id.exit:
                finish();
                break;
        }
        return true;
    }

    private void startLogin() {
        Intent intent = new Intent();
        intent.setClass(this.getApplicationContext(), PrepareRequestTokenActivity.class);
        startActivity(intent);
    }

    private void startSettings() {
        Intent intent = new Intent(getApplicationContext(), Settings.class);
        startActivity(intent);
    }


    private void clearCredentials() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final Editor edit = prefs.edit();
        edit.remove(OAuth.OAUTH_TOKEN);
        edit.remove(OAuth.OAUTH_TOKEN_SECRET);
        edit.commit();
    }


    @Override
    public void onSpinnerChanged(String date, String classRoom) {
        if (resultFragment != null && resultFragment.isInLayout()) {
            try {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("currentDate", date);
                editor.putString("currentRoom", classRoom);
                editor.commit();
                resultFragment.update(date, classRoom);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}