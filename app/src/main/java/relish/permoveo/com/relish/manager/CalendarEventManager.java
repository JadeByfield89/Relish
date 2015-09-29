package relish.permoveo.com.relish.manager;

import android.content.ContentResolver;
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

    public CalendarEventManager(final Context context, final Invite invite){
        this.invite = invite;
        this.context = context;
    }

    public void insertEventIntoCalender(final OnEventInsertedListener listener){
        this.listener = listener;
        new InsertInviteTask(invite, listener).execute();

    }


    private class InsertInviteTask extends AsyncTask<Void, Void, Boolean>{

        private Invite invite;
        private OnEventInsertedListener listener;
        public InsertInviteTask(final Invite invite, final OnEventInsertedListener listener){
            this.invite  = invite;
            this.listener = listener;
        }
        @Override
        protected Boolean doInBackground(Void... params) {

            //getLastCalendarId()

            Calendar beginTime = Calendar.getInstance();
            beginTime.set(2015, 8, 30, 7, 30);

            Calendar endTime = Calendar.getInstance();
            beginTime.set(2015, 8, 30, 8, 30);

            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
            values.put(CalendarContract.Events.TITLE, invite.title);
            values.put(CalendarContract.Events.DESCRIPTION, invite.note);

            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
            values.put(CalendarContract.Events.HAS_ALARM, 1);
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // get the event ID that is the last element in the Uri
            String pathSegment = uri.getLastPathSegment();
            long eventID = Long.parseLong(pathSegment);
            Log.d("CalendarEventManager", "Event ID -> " + eventID);

            if(pathSegment == null){
                listener.OnEventInserted(false);
                return false;
            }else
                listener.OnEventInserted(true);
                return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);


        }

        public String  getCalendarId(Context c) {
            String calID = null;
            int idCol = 0;

            String projection[] = {"_id", "calendar_displayName"};
            Uri calendars;
            calendars = Uri.parse("content://com.android.calendar/calendars");

            ContentResolver contentResolver = c.getContentResolver();
            Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

            if (managedCursor.moveToFirst()){
                //m_calendars = new MyCalendar[managedCursor.getCount()];
                String calName;

                int cont= 0;
                int nameCol = managedCursor.getColumnIndex(projection[1]);
                idCol = managedCursor.getColumnIndex(projection[0]);
                do {
                    calName = managedCursor.getString(nameCol);
                    Log.d("CalendarEventManager", "Calendar Name -> " + calName);
                    calID = managedCursor.getString(idCol);
                    Log.d("CalendarEventManager", "Calendar ID -> " + calID);
                    //m_calendars[cont] = new MyCalendar(calName, calID);
                    cont++;
                } while(managedCursor.moveToNext());
                managedCursor.close();
            }
            return calID;

        }
    }

    public interface OnEventInsertedListener {
        void OnEventInserted(boolean succes);
    }
}
