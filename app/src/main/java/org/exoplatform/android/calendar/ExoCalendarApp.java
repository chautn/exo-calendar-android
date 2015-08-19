package org.exoplatform.android.calendar;

import android.app.Application;

import org.exoplatform.calendar.client.rest.ExoCalendarConnector;

/**
 * Created by chautn on 8/17/15.
 */
public class ExoCalendarApp extends Application {

  private static ExoCalendarApp instance;

  private static ExoCalendarConnector connector;

  public ExoCalendarApp getInstance() {
    return instance;
  }

  public ExoCalendarConnector getConnector() {
    return connector;
  }

  @Override
  public void onCreate() {
    super.onCreate();;
    instance = this;
    connector = new ExoCalendarConnector("http://10.0.2.2:8080/rest/private/v1/calendar", "john", "gtngtn");
  }
}
