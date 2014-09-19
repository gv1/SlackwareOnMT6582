// see src/jackpal/androidterm/TermPreferences.java
// PreferenceActivity with sample code:
// file:///arch1/android/adt-bundle-linux-x86-20140702/sdk/docs/reference/android/preference/PreferenceActivity.html

package com.startx.android;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.ListPreference;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;


public class StartXPreferencesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	TextView text = (TextView)findViewById(R.id.msgbox);
	text.append("Hello World");
        switch (item.getItemId()) {
	    // case ActionBarCompat.ID_HOME:
	    //  // Action bar home button selected
	    //  finish();
	    //  return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    } 
    */
}
