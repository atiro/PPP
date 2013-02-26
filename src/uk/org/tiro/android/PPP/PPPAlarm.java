package uk.org.tiro.android.PPP;

import android.app.AlarmManager;
import android.content.Context;

import android.app.PendingIntent;
import android.content.Context;

import android.os.SystemClock;
import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.Calendar;

public class PPPAlarm implements WakefulIntentService.AlarmListener {

	public void scheduleAlarms(AlarmManager mgr, PendingIntent pi,
					Context ctxt) {

		Calendar alarm = Calendar.getInstance();

		// TODO user should be able to set time(s)

		alarm.set(Calendar.HOUR_OF_DAY, 6);
		alarm.set(Calendar.MINUTE, 30); // TODO Randomize
		alarm.set(Calendar.SECOND, 0); // TODO Randomize

		mgr.setRepeating(AlarmManager.RTC_WAKEUP,
					alarm.getTimeInMillis(),
					AlarmManager.INTERVAL_DAY, pi);

	}

	public void sendWakefulWork(Context ctxt) {
		WakefulIntentService.sendWakefulWork(ctxt, PPPUpdate.class);
	}

	public long getMaxAge() {
		return(AlarmManager.INTERVAL_DAY * 2);
	}
}
