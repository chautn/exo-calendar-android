package org.exoplatform.calendar.client.model;

import java.util.Date;

/**
 * Created by chautn on 8/17/15.
 */
public abstract class ComparableOccurrence implements Comparable<ComparableOccurrence> {

  public static final String iso8601dateformat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  abstract Date getStartDate();

  @Override
  public int compareTo(ComparableOccurrence another) {
    return getStartDate().compareTo(another.getStartDate());
  }

}
