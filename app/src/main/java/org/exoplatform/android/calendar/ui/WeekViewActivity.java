package org.exoplatform.android.calendar.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.exoplatform.android.calendar.ExoCalendarApp;
import org.exoplatform.android.calendar.R;
import org.exoplatform.calendar.client.model.ExoCalendar;
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;
import org.exoplatform.calendar.client.rest.ExoCalendarRestService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chautn on 8/21/15.
 */
public class WeekViewActivity extends Activity {

  public RecyclerView week_view;
  public VerticalTextView week_view_caption;
  public WeekViewAdapter adapter;
  public ExoCalendarConnector connector;
  public RecyclerView.LayoutManager layoutManager;
  public Calendar[] week;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.week);
    week_view = (RecyclerView) findViewById(R.id.week_view);
    week_view.setHasFixedSize(true);

    week = new Calendar[7];
    for (int i=0; i < 7; i++) {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.clear(Calendar.MINUTE);
      cal.clear(Calendar.SECOND);
      cal.clear(Calendar.MILLISECOND);
      cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
      cal.add(Calendar.DAY_OF_WEEK, i);
      week[i] = cal;
    }

    connector = ((ExoCalendarApp) getApplicationContext()).getConnector();
    final ExoCalendarRestService service = connector.getService();

    adapter = new WeekViewAdapter(this, connector, week);
    layoutManager = new LinearLayoutManager(this);
    week_view.setLayoutManager(layoutManager);
    week_view.setAdapter(adapter);

    week_view_caption = (VerticalTextView) findViewById(R.id.week_view_caption);
    DateFormat dateFormat = new SimpleDateFormat("MMMM' 'yyyy");
    week_view_caption.setText(dateFormat.format(week[0].getTime()));
  }
}
