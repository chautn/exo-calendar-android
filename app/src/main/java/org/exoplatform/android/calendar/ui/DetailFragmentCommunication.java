package org.exoplatform.android.calendar.ui;

import org.exoplatform.calendar.client.model.ComparableOccurrence;
import org.exoplatform.calendar.client.model.ExoCalendar;

import java.util.ArrayList;

/**
 * Created by chautn on 9/6/15.
 */
public interface DetailFragmentCommunication {

  public void onItemDeleted(String itemId);

  public void onItemUpdated(String itemId, ComparableOccurrence item);

  public int getItemColor(String calendarId);

  public String getCalendarName(String calendarId);

  public ArrayList<String> getCalendarJsonList(); //using offline data.

}
