package uk.org.tiro.android.PPP;

import android.os.Bundle;

import android.app.Activity;

import android.database.Cursor;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.app.LoaderManager;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.Gallery;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.CursorAdapter;
import android.widget.ArrayAdapter;

import android.content.Context;

import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockFragment;

public class DebateView extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle args;
		String guid;
		Integer house;

		super.onCreate(savedInstanceState);

		setContentView(R.layout.debate);

		guid = this.getIntent().getExtras().getString("guid");
		house = this.getIntent().getExtras().getInt("house");

		DebateFragment debate = (DebateFragment)getSupportFragmentManager().findFragmentById(R.id.debate);

		debate.setDebate(house, guid);
	}

}
