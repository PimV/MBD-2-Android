package nl.avans.VrijeLokalenApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import nl.avans.oAuthdemo.R;
import oauth.signpost.OAuth;


/**
 * Entry point in the application.
 * Launches the OAuth flow by starting the PrepareRequestTokenActivity
 */
public class LoginActivity extends Activity {
    private SharedPreferences prefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get preferences
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (isNetworkAvailable()) {
            if (!loginInformationFound()) {
                startLogin();
            } else {
                moveToHome();
            }
            finish();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("No internet!")
                    .setMessage("No internet found. Turn on your internet and re-open the application!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }


    }



    private boolean loginInformationFound() {
        if (this.prefs.getString(OAuth.OAUTH_TOKEN, "").isEmpty() || this.prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "").isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private void startLogin() {
        Intent intent = new Intent();
        intent.setClass(this.getApplicationContext(), PrepareRequestTokenActivity.class);
        startActivity(intent);
    }

    private void moveToHome() {
        Intent intent = new Intent();
        intent.setClass(this.getApplicationContext(), OAuthFlowApp.class);
        startActivity(intent);
    }
}