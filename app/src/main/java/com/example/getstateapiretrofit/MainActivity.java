package com.example.getstateapiretrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.getstateapiretrofit.databinding.ActivityMainBinding;
import com.example.getstateapiretrofit.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityMainBinding binding;
    String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setupUi();
        setContentView(view);
    }

    private void setupUi(){
        //set on Click listener on button to search based on city name
        binding.searchButton.setOnClickListener(view -> {
            cityName = binding.cityNameEditText.getText().toString();
            try {
                getData();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
    }

    //get data from our url
    private void getData() throws MalformedURLException {
        Uri uri = Uri.parse("https://datausa.io/api/data?drilldowns=State&measures=Population&year=latest")
                .buildUpon().build();
        URL url = new URL(uri.toString());
        new apiCall().execute(url);
    }


    class apiCall extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... urls) {
            String data = null;
           URL url = urls [0];
            try {
                data = NetworkUtils.makeHTTPRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                parseJson(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseJson(String data) throws JSONException {
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray cityArray = jsonObject.getJSONArray("data");

            for (int i=0; i<cityArray.length();i++){
                JSONObject cityObject = cityArray.getJSONObject(i);
                String apiCityName =cityObject.get("State").toString();
                if (apiCityName.equals(cityName)){
                    String population = cityObject.get("Population").toString();
                    binding.stateResultTextView.setText(cityName + " has population of:");
                    binding.populationResultTextView.setText(population);
                }else{
                    binding.populationResultTextView.setError("Make sure your State is spelled correct");
                    Toast.makeText(getApplicationContext(), "State name not found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public void onClick(View view) {

    }
}