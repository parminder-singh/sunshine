package com.example.parmindr.sunshine;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by parmindr on 4/6/16.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        forecastAdapter = getAdapter();

        //bind list adapter to the listView
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
        listView.setAdapter(forecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecastString = ((TextView) view).getText().toString();
                Intent launchDetailActictyIntent = new Intent(getActivity(), DetailsActivity.class).putExtra(Intent.EXTRA_TEXT, forecastString);
                startActivity(launchDetailActictyIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_refresh) {
            getWeatherData();
            return true;
        } else if (item.getItemId() == R.id.menu_item_settings) {
            Intent launchSettingsActivty = new Intent(getActivity(), SettingsActivity.class);
            startActivity(launchSettingsActivty);
            return true;
        }  else if (item.getItemId() == R.id.menu_item_show_on_map) {
            String location = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                    getString(R.string.pref_postal_code_key),
                    getString(R.string.pref_default_postal_code));
            Uri uri = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q", location)
                    .build();
            Intent showPreferenceOnMap = new Intent(Intent.ACTION_VIEW);
            showPreferenceOnMap.setData(uri);
            if(showPreferenceOnMap.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(showPreferenceOnMap);
            }
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        getWeatherData();
    }

    private ArrayAdapter<String> getAdapter() {
        return new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview);
    }

    private void getWeatherData() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                getString(R.string.pref_postal_code_key),
                getString(R.string.pref_default_postal_code)));
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, List<String>> {

        public final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        private static final String OWM_URL_TEMPLATE = "http://api.openweathermap.org/data/2.5/forecast/daily?zip={zip_code},IN&cnt=16&mode=json&units=metric&appid=41b580d2dcc50552f38c6073aa9b96ef";

        private String getForecastJson(String zipCode) {

            HttpURLConnection urlConnection = null;
            Reader reader = null;
            String jsonWeatherData = null;

            try {

                //Create the Http connection
                URL url = new URL(OWM_URL_TEMPLATE.replace("{zip_code}", zipCode));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //read data
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                jsonWeatherData = bufferedReader.readLine();
                Log.d(LOG_TAG, jsonWeatherData);

            } catch (IOException exception) {
                Log.e(LOG_TAG, "Error occured while fetching data from OpenWeatherMap API", exception);
            } finally {

                //clean up
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException exception) {
                        Log.e(LOG_TAG, "Error occurred while closing the reader stream", exception);
                    }
                }
                urlConnection.disconnect();

                //finally return the read data
                return jsonWeatherData;
            }
        }

        private List<String> getForecastDataList(String forecastJson) {
            List<String> forecastList = new ArrayList<>();
            try {
                String cityName = new JSONObject(forecastJson).getJSONObject("city").getString("name");
                String countryName = new JSONObject(forecastJson).getJSONObject("city").getString("country");

                JSONArray forecastDataList = new JSONObject(forecastJson).getJSONArray("list");
                for (int dayIndex = 0; dayIndex < forecastDataList.length(); dayIndex++) {
                    JSONObject temperatureForDay = forecastDataList.getJSONObject(dayIndex).getJSONObject("temp");
                    String unitPreference = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                            getString(R.string.pref_temperature_unit_key),
                            getString(R.string.pref_default_temperature_unit));
                    Log.d(LOG_TAG, unitPreference);
                    String minimumTemp = getMinTemperature(temperatureForDay,unitPreference);
                    String maximumTemp = getMaxTemperature(temperatureForDay,unitPreference);

                    JSONObject weatherForDay = forecastDataList.getJSONObject(dayIndex).getJSONArray("weather").getJSONObject(0);
                    String description = weatherForDay.getString("main");

                    long timestamp = forecastDataList.getJSONObject(dayIndex).getLong("dt");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM d");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));

                    String viewText = cityName + ", " + countryName + " - " + simpleDateFormat.format(new Date((long) timestamp * 1000)) + " - " + minimumTemp + "/" + maximumTemp + " - " + description;
                    Log.d(LOG_TAG, viewText);
                    forecastList.add(viewText);
                }
            } catch (JSONException exception) {
                Log.e(LOG_TAG, "Error while parsing JSON data", exception);
            }
            return forecastList;
        }

        private String getMinTemperature(JSONObject temperatureForDay, String unitPreference) throws JSONException {
            if (unitPreference.equals(getString(R.string.pref_temperature_unit_value_imperial))) {
                return String.valueOf(Math.round(convertToFahrenheit(temperatureForDay.getDouble("min"))));
            } else{
                return  String.valueOf(Math.round(temperatureForDay.getDouble("min")));
            }
        }

        private String getMaxTemperature(JSONObject temperatureForDay, String unitPreference) throws JSONException {
            if (unitPreference.equals(R.string.pref_temperature_unit_value_imperial)) {
                return String.valueOf(Math.round(convertToFahrenheit(temperatureForDay.getDouble("max"))));
            } else {
                return  String.valueOf(Math.round(temperatureForDay.getDouble("max")));
            }
        }

        private double convertToFahrenheit(double temperatureInCelsius) {
            return temperatureInCelsius * 1.8 + 32;
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String forecastJson = getForecastJson(params[0]);
            return getForecastDataList(forecastJson);
        }

        @Override
        protected void onPostExecute(List<String> weatherDataList) {
            super.onPostExecute(weatherDataList);
            forecastAdapter.clear();
            forecastAdapter.addAll(weatherDataList);
        }

    }

}
