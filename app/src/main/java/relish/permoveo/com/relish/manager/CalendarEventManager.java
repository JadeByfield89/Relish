package relish.permoveo.com.relish.manager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.TimeZone;

import relish.permoveo.com.relish.model.Invite;

/**
 * Created by byfieldj on 9/29/15.
 */
public enum CalendarEventManager {
    get;

    long calID = 3;
    private OnEventInsertedListener listener;
    private Context context;

    public void init(final Context context) {
        this.context = context;
    }

    public void insertEventIntoCalender(Invite invite, final OnEventInsertedListener listener) {
        this.listener = listener;
        new InsertInviteTask(invite, listener).execute();
    }

    private class InsertInviteTask extends AsyncTask<Void, Void, Boolean> {

        private Invite invite;
        private OnEventInsertedListener listener;

        public InsertInviteTask(final Invite invite, final OnEventInsertedListener listener) {
            this.invite = invite;
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //getLastCalendarId()

            DateTime time = new DateTime().withMillis(invite.time);
            DateTime date = new DateTime().withMillis(invite.date);
            DateTime when = new DateTime()
                    .withYear(date.getYear())
                    .withMonthOfYear(date.getMonthOfYear())
                    .withDayOfMonth(date.getDayOfMonth())
                    .withHourOfDay(time.getHourOfDay())
                    .withMinuteOfHour(time.getMinuteOfHour());

            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, when.getMillis());
//            values.put(CalendarContract.Events.ALL_DAY, true);
            values.put(CalendarContract.Events.DTEND, when.plusHours(1).getMillis());
            values.put(CalendarContract.Events.TITLE, invite.title);
//            values.put(CalendarContract.Events.DESCRIPTION, "blah");

            long calId = Long.parseLong(getCalendarId(context));

            Log.d("CalendarEventManager", "Calendar ID -> " + 1);
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, when.getZone().getID());
            values.put(CalendarContract.Events.HAS_ALARM, 1);
//            values.put(CalendarContract.Events.DURATION, 60 * 60 * 1000);
            values.put(CalendarContract.Events.AVAILABILITY, 0);
            values.put(CalendarContract.Events.EVENT_LOCATION, invite.location.address);
            values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);

            //values.put(CalendarContract.Events.STATUS, 1);

            Log.d("CalendarEventManager", "Timezone -> " + TimeZone.getDefault().getID());
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            Log.d("CalendarEventManager", "Calendar Events URI -> " + CalendarContract.Events.CONTENT_URI);

            // get the event ID that is the last element in the Uri
            String pathSegment = uri.getLastPathSegment();
            long eventID = Long.parseLong(pathSegment);
            Log.d("CalendarEventManager", "Event ID -> " + eventID);

            //
//            ContentResolver resolver = context.getContentResolver();
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
//            contentValues.put(CalendarContract.Calendars.VISIBLE, 1);
//
//            resolver.update(ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, 1), contentValues, null, null);

            if (pathSegment == null) {
                return false;
            } else
                return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            listener.OnEventInserted(aBoolean);
        }

        public String getCalendarId(Context c) {
            String calID = null;
            int idCol = 0;

            String projection[] = {"_id", "calendar_displayName"};
            Uri calendars;
            calendars = Uri.parse("content://com.android.calendar/calendars");

            ContentResolver contentResolver = c.getContentResolver();
            Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

            if (managedCursor.moveToFirst()) {
                //m_calendars = new MyCalendar[managedCursor.getCount()];
                String calName;

                int cont = 0;
                int nameCol = managedCursor.getColumnIndex(projection[1]);
                idCol = managedCursor.getColumnIndex(projection[0]);
                do {
                    calName = managedCursor.getString(nameCol);
                    Log.d("CalendarEventManager", "Calendar Name -> " + calName);
                    calID = managedCursor.getString(idCol);
                    Log.d("CalendarEventManager", "Calendar ID -> " + calID);
                    //m_calendars[cont] = new MyCalendar(calName, calID);
                    cont++;
                } while (managedCursor.moveToNext());
                managedCursor.close();
            }
            return calID;

        }
    }

    public interface OnEventInsertedListener {
        void OnEventInserted(boolean succes);
    }
}
