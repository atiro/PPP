package uk.org.tiro.android.PPP;

import android.preference.PreferenceActivity;
import android.os.Bundle;

public class PPPSettings extends PreferenceActivity {
//	implements OnSharedPreferenceChangeListener {
	final String KEY_PREF_DEBATES_VISIBLE = "debates_visible";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

//	public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
//		if(key.equals(KEY_PREF_DEBATES_VISIBLE)) {
			// TODO Update summary (apparently should be done, why?)
//	}

}

