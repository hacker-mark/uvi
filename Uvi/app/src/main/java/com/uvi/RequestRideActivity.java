package com.uvi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.uvi.placesautocomplete.PlacesAutoCompleteAdapter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 18/04/2015.
 */
public class RequestRideActivity extends Activity {

    private String startLocation = "";
    private String destinationLocation = "";
    private String startLocationId = "";
    private String destinationLocationId = "";

    private Pubnub pubnub = new Pubnub("pub-c-3c307dc8-de18-434e-adc0-1b23767e7c80", "sub-c-873b71b2-df3f-11e4-a502-0619f8945a4f");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_ride);

        AutoCompleteTextView startSearch = (AutoCompleteTextView)findViewById(R.id.start);
        setupSearchView(startSearch, true);
        AutoCompleteTextView destinationSearch = (AutoCompleteTextView)findViewById(R.id.destination);
        setupSearchView(destinationSearch, false);

        Button doneButton = (Button)findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Callback callback = new Callback() {
                    public void successCallback(String channel, Object response) {
                        System.out.println(response.toString());
                    }
                    public void errorCallback(String channel, PubnubError error) {
                        System.out.println(error.toString());
                    }
                };
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("rider_start", startLocation);
                    obj.put("rider_destination", destinationLocation);
                    pubnub.publish("rider", obj, callback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            pubnub.subscribe("rider_response", new Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                public void reconnectCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void successCallback(String channel, Object message) {
                    System.out.println("SUBSCRIBE : " + channel + " : "
                            + message.getClass() + " : " + message.toString());
                    JSONObject riderResponse = (JSONObject)message;
                    Log.d("TOIKE", riderResponse.toString());
                    try {
                        JSONObject driver = riderResponse.getJSONObject("driver");
                        final String start = driver.getString("start");
                        final String destination = driver.getString("destination");
                        final String startId = driver.getString("start_id");
                        final String destinationId = driver.getString("destination_id");
                        final String phone = driver.getString("phone");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                                intent.putExtra("start", start);
                                intent.putExtra("destination", destination);
                                intent.putExtra("start_id", startId);
                                intent.putExtra("destination_id", destinationId);
                                intent.putExtra("rider_start_id", startLocationId);
                                intent.putExtra("rider_destination_id", destinationLocationId);
                                intent.putExtra("rider_start", startLocation);
                                intent.putExtra("rider_destination", destinationLocation);
                                intent.putExtra("phone", phone);
                                startActivity(intent);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    System.out.println("SUBSCRIBE : ERROR on channel " + channel
                            + " : " + error.toString());
                }
            }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }

    }

    private void setupSearchView(AutoCompleteTextView auto, final boolean isSource) {
        final PlacesAutoCompleteAdapter placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, R.layout.places_autocomplete_list_item);
        auto.setAdapter(placesAutoCompleteAdapter);
        auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectAddressToAdd(placesAutoCompleteAdapter, position, isSource);
            }
        });
        auto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (placesAutoCompleteAdapter.hasElements()) {
                        selectAddressToAdd(placesAutoCompleteAdapter, 0, isSource);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void selectAddressToAdd(PlacesAutoCompleteAdapter adapter, int position, boolean isSource) {
        if (isSource) {
            startLocation = adapter.getItem(position);
            startLocationId = adapter.getPlaceId(position);
        } else {
            destinationLocation = adapter.getItem(position);
            destinationLocationId = adapter.getPlaceId(position);
        }
    }

}
