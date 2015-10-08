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

import java.util.Calendar;
import java.util.TimeZone;

import relish.permoveo.com.relish.model.Invite;

/**
 * Created by byfieldj on 9/29/15.
 */
public class CalendarEventManager {

    long calID = 3;
    private Invite invite;
    private OnEventInsertedListener listener;
    private Context context;

    public CalendarEventManager(final Context context, final Invite invite) {
        this.invite = invite;
        this.context = context;
    }

    public void insertEventIntoCalender(final OnEventInsertedListener listener) {
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

            Calendar beginTime = Calendar.getInstance();
            beginTime.set(2015, 9, 30, 7, 30);

            Calendar endTime = Calendar.getInstance();
            beginTime.set(2015, 10, 2, 8, 30);

            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
            values.put(CalendarContract.Events.TITLE, "Rock");
            values.put(CalendarContract.Events.DESCRIPTION, "blah");

            Log.d("CalendarEventManager", "Calendar ID -> " + 1);
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            values.put(CalendarContract.Events.HAS_ALARM, 1);
            values.put(CalendarContract.Events.AVAILABILITY, 0);
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
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
            contentValues.put(CalendarContract.Calendars.VISIBLE, 1);

            long calId = Long.parseLong(getCalendarId(context));
            resolver.update(ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, 1), contentValues, null, null);



            if (pathSegment == null) {
                return false;
            } else
                return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                listener.OnEventInserted(true);
            } else {
                listener.OnEventInserted(false);
            }

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