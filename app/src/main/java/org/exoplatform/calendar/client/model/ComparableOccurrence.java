package org.exoplatform.calendar.client.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chautn on 8/17/15.
 */
public abstract class ComparableOccurrence implements Comparable<ComparableOccurrence> {

  public static final String iso8601dateformat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  public abstract Date getStartDate();

  public abstract Date getEndDate();

  @Override
  public int compareTo(ComparableOccurrence another) {
    return getStartDate().compareTo(another.getStartDate());
  }

  public String getStartAMPM() {
    Date date = getStartDate();
    DateFormat df = new SimpleDateFormat("h:mm a");
    return df.format(date);
  }

  public String getEndAMPM() {
    Date date = getEndDate();
    DateFormat df = new SimpleDateFormat("h:mm a");
    return df.format(date);
  }
}
