package com.example.parmindr.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setDetailsContent();
    }

    private void setDetailsContent() {
        // set data that in the details text view
        Intent intent = getIntent();
        TextView detailsTextView = (TextView) findViewById(R.id.details_textview);
        detailsTextView.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailsactivity, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_share);
        shareActionProvider =  (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        Intent shareWeatherData = new Intent(Intent.ACTION_SEND);
        shareWeatherData.putExtra(Intent.EXTRA_TEXT, ((TextView) findViewById(R.id.details_textview)).getText());
        shareWeatherData.setType("text/plain");
        shareActionProvider.setShareIntent(shareWeatherData);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_settings) {
            Intent launchSettingsActivty = new Intent(getApplication(), SettingsActivity.class);
            startActivity(launchSettingsActivty);
            return true;
        } else if (item.getItemId() == R.id.menu_item_show_on_map) {
            String location = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString(
                    getString(R.string.pref_postal_code_key),
                    getString(R.string.pref_default_postal_code));
            Uri uri = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q", location)
                    .build();
            Intent showPreferenceOnMap = new Intent(Intent.ACTION_VIEW);
            showPreferenceOnMap.setData(uri);

            if (showPreferenceOnMap.resolveActivity(getApplication().getPackageManager()) != null) {
                startActivity(showPreferenceOnMap);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
