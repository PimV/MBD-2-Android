package nl.avans.oAuthdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.widget.*;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.http.HttpRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup.OnCheckedChangeListener;


/**
 * Entry point in the application.
 * Launches the OAuth flow by starting the PrepareRequestTokenActivity
 *
 */
public class OAuthFlowApp extends Activity {
	final String TAG = getClass().getName();
	private SharedPreferences prefs;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

        RadioGroup radiogroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        radiogroup1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				performApiCall();
			}
        });
        
        performApiCall();        

    }
	
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
		}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.clear:
            	clearCredentials();
            	performApiCall();
	            break;
	        case R.id.refresh:
            	performApiCall();
	            break;
	        case R.id.login:
	        	Intent intent = new Intent();
	        	intent.setClass(this.getApplicationContext(),PrepareRequestTokenActivity.class);
	        	startActivity(intent);
	            break;
	        case R.id.exit:
            	finish();
	            break;
	    }
	    return true;
	}

	private void performApiCall() {
		TextView textView = (TextView) findViewById(R.id.response_code);
		RadioButton rbPeople=(RadioButton)findViewById(R.id.people);
		
		
		String jsonOutput = "";
        try {
        	if(rbPeople.isChecked())
        		jsonOutput = doGet(oAuthConstants.API_REQUEST_LOKAAL,getConsumer(this.prefs));
        	
        	//textView.setText("Raw Data : " + jsonOutput);

			ArrayList<EmptyRoomEntry> emptyRooms = new ArrayList<>();

			JSONArray root = new JSONArray(jsonOutput);
			for (int i = 0; i < root.length(); ++i) {
				JSONObject rec = root.getJSONObject(i);
				String date = rec.getString("datum");
				int collegeHour = rec.getInt("lesuur");
				String classRoom = rec.getString("ruimte");
				int roomSize = rec.getInt("grootte");
				String type = rec.getString("type"); //Perhaps to enum?
				boolean occupied = !(rec.getString("bezet").equals("0"));

				EmptyRoomEntry ere = new EmptyRoomEntry();
				ere.setClassRoom(classRoom);
				ere.setCollegeHour(collegeHour);
				ere.setDate(date);
				ere.setOccupied(occupied);
				ere.setRoomSize(roomSize);
				ere.setType(type);
				emptyRooms.add(ere);
			}
			ListView lv = (ListView) findViewById(R.id.my_listview);
			ArrayAdapter<EmptyRoomEntry> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emptyRooms);
			lv.setAdapter(arrayAdapter);
			System.out.println("Empty Room Entries: " + emptyRooms.size());
        	Log.i(TAG, jsonOutput);
        	//textView.setText(contacts);
		} catch (Exception e) {
			Log.e(TAG, "Error executing request",e);
			textView.setText("Error retrieving contacts : " + jsonOutput);
		}
	}

	

    private void clearCredentials() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
	}

	
	private OAuthConsumer getConsumer(SharedPreferences prefs) {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(oAuthConstants.CONSUMER_KEY, oAuthConstants.CONSUMER_SECRET);
		consumer.setTokenWithSecret(token, secret);
		return consumer;
	}
	
	private String doGet(String url,OAuthConsumer consumer) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
    	request.setHeader("User-Agent", TAG);
    	Log.i(TAG,"Requesting URL : " + url);
    	consumer.sign(request);
    	HttpResponse response = httpclient.execute(request);
    	Log.i(TAG,"Statusline : " + response.getStatusLine());
    	InputStream data = response.getEntity().getContent();
    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
        String responeLine;
        StringBuilder responseBuilder = new StringBuilder();
        while ((responeLine = bufferedReader.readLine()) != null) {
        	responseBuilder.append(responeLine);
        }
        Log.i(TAG,"Response : " + responseBuilder.toString());
        return responseBuilder.toString();
	}	
	
    //@Override
    protected HttpRequest createRequest(String endpointUrl) throws MalformedURLException, IOException {
       HttpURLConnection connection = (HttpURLConnection)new URL(endpointUrl).openConnection();
       connection.setRequestMethod("GET");
       connection.setAllowUserInteraction(false);
       connection.setRequestProperty("Content-Length", "0");
       return new HttpURLConnectionRequestAdapter(connection);
    }
}