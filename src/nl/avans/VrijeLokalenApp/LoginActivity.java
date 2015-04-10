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
public class LoginActivity extends Activity {
    private SharedPreferences prefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get preferences
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!loginInformationFound()) {
            startLogin();
        } else {
            moveToHome();
        }
        finish();

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