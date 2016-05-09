package com.example.parmindr.sunshine;

/**
 * Created by parmindr on 4/26/16.
 */
public class WeatherContract {

    public static class WeatherEntry {

        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_LOCATION_ID = "location_id";

        // stored as milliseconds since epoch in long
        public static final String COLUMN_DATE = "date";

        //stored as degree celsius in float
        public static final String COLUMN_MIN_TEMP = "min_temp";

        //stored as degree celsius in float
        public static final String COLUMN_MAX_TEMP = "max_temp";

        public static final String COLUMN_HUMIDITY = "humidity";

        public static final String COLUMN_WIND_SPEED = "wind_speed";

        public static final String COLUMN_PRESSURE = "pressure";

        //wind direction stored as meteorological degrees in float
        public static final String COLUMN_WIND_DIRECTION = "direction";

        public static final String COLUMN_WEATHER_ID = "weather_id";
    }

    public static class LocationEntry {
        public static final String TABLE_NAME = "location";
    }

}
