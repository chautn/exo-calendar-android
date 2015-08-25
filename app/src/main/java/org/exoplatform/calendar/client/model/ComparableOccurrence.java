package org.exoplatform.calendar.client.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chautn on 8/17/15.
 */
public abstract class ComparableOccurrence implements Comparable<ComparableOccurrence> {

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
    Date date = getEndDate();
    return (new SimpleDateFormat("h:mma")).format(getEndDate());
  }

  public abstract String getTitle();
}
