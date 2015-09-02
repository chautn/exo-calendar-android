package org.exoplatform.calendar.client.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chautn on 8/17/15.
 */
public abstract class ComparableOccurrence implements Comparable<ComparableOccurrence>, Identifiable {

  public static final String iso8601dateformat = "yyyy-MM-dd'T'HH:mm:ssZ";

  public abstract Date getStartDate();

  public abstract Date getEndDate();

  @Override
  public int compareTo(ComparableOccurrence another) {
    return getStartDate().compareTo(another.getStartDate());
  }

  public String getStartAMPM() {
    return (new SimpleDateFormat("h:mma")).format(getStartDate());
  }

  public String getEndAMPM() {
    return (new SimpleDateFormat("h:mma")).format(getEndDate());
  }

  public abstract String getTitle();

  public String getStart24() {
    return (new SimpleDateFormat("hh:mm").format(getStartDate()));
  }
  public String getEnd24() {
    return (new SimpleDateFormat("hh:mm").format(getEndDate()));
  }

  public abstract String getCalendar();
  public String getCalendarId() {
    return getCalendar().substring(getCalendar().lastIndexOf("/") +1);
  }
}
