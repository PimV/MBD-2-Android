package nl.avans.VrijeLokalenApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by PimGame on 10-4-2015.
 */
public class ResultLoader {
    private static HashMap<String, HashMap<String, HashMap<String, ArrayList<EmptyRoomEntry>>>> tempStorage;
    private static Context appContext;

    private static ResultLoader ourInstance = new ResultLoader();

    public static ResultLoader getInstance() {
        return ourInstance;
    }

    private ResultLoader() {
        tempStorage = new HashMap<String, HashMap<String, HashMap<String, ArrayList<EmptyRoomEntry>>>>();
    }

    public void setContext(Context context) {
        appContext = context;
    }

    private OAuthConsumer getConsumer(SharedPreferences prefs) {

        String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
        String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(oAuthConstants.CONSUMER_KEY, oAuthConstants.CONSUMER_SECRET);
        consumer.setTokenWithSecret(token, secret);
        return consumer;
    }

    public HashMap<String, HashMap<String, ArrayList<EmptyRoomEntry>>> parse(String json) throws JSONException {
        HashMap<String, HashMap<String, ArrayList<EmptyRoomEntry>>> data = new HashMap<>();

        JSONArray root = new JSONArray(json);
        for (int i = 0; i < root.length(); ++i) {
            JSONObject rec = root.getJSONObject(i);
            String entryDate = rec.getString("datum");
            int collegeHour = rec.getInt("lesuur");
            String entryClassRoom = rec.getString("ruimte");
            int roomSize = !rec.isNull("grootte") ? rec.getInt("grootte") : -1;
            String type = rec.getString("type");
            boolean occupied = rec.getString("bezet").equals("1");

            EmptyRoomEntry ere = new EmptyRoomEntry();
            ere.setClassRoom(entryClassRoom);
            ere.setCollegeHour(collegeHour);
            ere.setDate(entryDate);
            ere.setOccupied(occupied);
            ere.setRoomSize(roomSize);
            ere.setType(type);

            if (!data.containsKey(entryDate)) {
                data.put(entryDate, new HashMap<String, ArrayList<EmptyRoomEntry>>());
            }
            if (!data.get(entryDate).containsKey(entryClassRoom)) {
                data.get(entryDate).put(entryClassRoom, new ArrayList<EmptyRoomEntry>());
            }
            data.get(entryDate).get(entryClassRoom).add(ere);
        }

        return data;
    }

    public HashMap<String, HashMap<String, ArrayList<EmptyRoomEntry>>> getAndParse()
            throws Exception {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);

        String dateStart = prefs.getString("dateStart", "");
        String dateEnd = prefs.getString("dateEnd", "");
        String rooms = prefs.getString("rooms", "OB2");
        OAuthConsumer consumer = getConsumer(prefs);

        return getAndParse(dateStart, dateEnd, rooms, consumer);
    }

    public HashMap<String, HashMap<String, ArrayList<EmptyRoomEntry>>> getAndParse(String room,
                                                                                   OAuthConsumer consumer)
            throws Exception {
        return getAndParse("", "", room, consumer);
    }

    public HashMap<String, HashMap<String, ArrayList<EmptyRoomEntry>>> getAndParse(String dateStart,
                                                                                   String dateEnd,
                                                                                   String room,
                                                                                   OAuthConsumer consumer)
            throws Exception {
        HashMap<String, HashMap<String, ArrayList<EmptyRoomEntry>>> data;


        String url = "https://publicapi.avans.nl/oauth/lokaalbeschikbaarheid/?type=all";

        SimpleDateFormat ft =
                new SimpleDateFormat("yyyy-MM-dd");

        if (dateStart.isEmpty()) {
            dateStart = ft.format(new Date());
        }
        url += "&start=" + dateStart;

        if (dateEnd.isEmpty()) {
            dateEnd = ft.format(new Date());
        }
        url += "&end=" + dateEnd;

        if (room.isEmpty()) {
            room = "OB2";
        }
        url += "&filter=" + room;

        //Retrieve from tempStorage if found
        if (tempStorage.containsKey(url)) {
            data = tempStorage.get(url);
        } else {
            data = parse(doGet(url, consumer));
            tempStorage.put(url, data);
        }


        return data;
    }

    public String doGet(String url, OAuthConsumer consumer) throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        consumer.sign(request);
        HttpResponse response = httpclient.execute(request);
        InputStream data = response.getEntity().getContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
        String responseLine;
        StringBuilder responseBuilder = new StringBuilder();
        while ((responseLine = bufferedReader.readLine()) != null) {
            responseBuilder.append(responseLine);
        }
        System.out.println("Response : " + responseBuilder.toString());
        return responseBuilder.toString();
    }
}
