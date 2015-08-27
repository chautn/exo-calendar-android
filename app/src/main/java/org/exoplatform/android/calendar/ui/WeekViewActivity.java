package org.exoplatform.android.calendar.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.exoplatform.android.calendar.ExoCalendarApp;
import org.exoplatform.android.calendar.R;
import org.exoplatform.calendar.client.model.ComparableOccurrence;
import org.exoplatform.calendar.client.model.Event;
import org.exoplatform.calendar.client.model.ExoCalendar;
import org.exoplatform.calendar.client.model.ParsableList;
import org.exoplatform.calendar.client.model.Task;
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chautn on 8/21/15.
 */
public class WeekViewActivity extends AppCompatActivity {

  public TextView caption;
  public TextView next;
  public TextView prev;
  public RecyclerView recyclerView;
  public RecyclerView.LayoutManager layoutManager;
  public ExoCalendarConnector connector;
  public ParsableList<ExoCalendar> calendar_ds;
  public List<ParsableList<Event>> event_ds;
  public List<ParsableList<Task>> task_ds;
  public List<List<ComparableOccurrence>> occurrences;
  public List<Date> week;
  public WeekViewAdapter adapter;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.week);
    caption = (TextView) findViewById(R.id.week_view_caption);
    recyclerView = (RecyclerView) findViewById(R.id.week_view);
    recyclerView.setHasFixedSize(true);
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    connector = ((ExoCalendarApp) getApplicationContext()).getConnector();

    init();
    adapter = new WeekViewAdapter(this, connector, week, occurrences);
    recyclerView.setAdapter(adapter);

    // Display offline data then load remote items.
    caption.setText((new SimpleDateFormat("MMMM' 'yyyy")).format(week.get(0).getTime()));
    download();

    //next & previous button
    next = (TextView) findViewById(R.id.next_week);
    prev = (TextView) findViewById(R.id.previous_week);

    next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        for (int i = 0; i < 7; i++) {
          Date date = new Date(week.get(i).getTime() + 1000 * 60 * 60 * 24 * 7);
          week.set(i, date);
        }
        //reset all the lists and the calendar_ds as well. Re-caption the view.
        reset();
        caption.setText((new SimpleDateFormat("MMMM' 'yyyy")).format(week.get(0).getTime()));
        //reload data
        download();
      }
    });

    prev.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        for (int i = 0; i < 7; i++) {
          Date date = new Date(week.get(i).getTime() - 1000 * 60 * 60 * 24 * 7);
          week.set(i, date);
        }
        //reset all the lists and the calendar_ds as well. Re-caption the view.
        reset();
        caption.setText((new SimpleDateFormat("MMMM' 'yyyy")).format(week.get(0).getTime()));
        //reload data
        download();
      }
    });
  }

  public void init() {
    calendar_ds = new ParsableList<ExoCalendar>();
    week = new ArrayList<Date>();
    event_ds = new ArrayList<ParsableList<Event>>();
    task_ds = new ArrayList<ParsableList<Task>>();
    occurrences = new ArrayList<List<ComparableOccurrence>>();

    for (int i=0; i < 7; i++) {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.clear(Calendar.MINUTE);
      cal.clear(Calendar.SECOND);
      cal.clear(Calendar.MILLISECOND);
      cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
      cal.add(Calendar.DAY_OF_WEEK, i);
      week.add(cal.getTime());
      event_ds.add(new ParsableList<Event>());
      task_ds.add(new ParsableList<Task>());
      occurrences.add(new ArrayList<ComparableOccurrence>());
    }
  }

  public void reset() {
    calendar_ds.data = null;
    for (int i=0; i < 7; i++) {
      event_ds.set(i, new ParsableList<Event>());
      task_ds.set(i, new ParsableList<Task>());
      occurrences.set(i, new ArrayList<ComparableOccurrence>());
    }
  }

  public void download() {
    final Callback<ParsableList<ExoCalendar>> callback = new Callback<ParsableList<ExoCalendar>>() {
      @Override
      public void success(ParsableList<ExoCalendar> exoCalendarParsableList, Response response) {
        calendar_ds.add(exoCalendarParsableList);
        if (calendar_ds.data.length < exoCalendarParsableList.getSize()) {
          // doesn't start to load events if calendar ds is not complete
          connector.getService().getCalendars(true, calendar_ds.data.length, this);
        } else {
          // start load events and notifyDataSetChanged
          for (int i=0; i < 7; i++) {
            final int position = i;
            DateFormat df = new SimpleDateFormat(ComparableOccurrence.iso8601dateformat);
            final String start = df.format(week.get(i));
            Date date = new Date(week.get(i).getTime() + 1000*60*60*24 -1);
            final String end = df.format(date);

            System.out.println(start + " " + end);

            for (final ExoCalendar calendar : calendar_ds.data) {

              //Event callback
              final Callback<ParsableList<Event>> callback1 = new Callback<ParsableList<Event>>() {
                @Override
                public void success(ParsableList<Event> eventParsableList, Response response) {
                  event_ds.get(position).add(eventParsableList);
                  if ((eventParsableList.data != null) && (eventParsableList.data.length != 0)) {
                    int length = eventParsableList.data.length;
                    for (int i=0; i < length; i++) {
                      occurrences.get(position).add(eventParsableList.data[i]);
                      Collections.sort(occurrences.get(position));
                    }
                  }
                  adapter.notifyItemChanged(position); //update views
                  if (event_ds.get(position).data.length < eventParsableList.getSize()) {
                    connector.getService().getEventsByCalendarId(true, event_ds.get(position).data.length, start, end, calendar.getId(), this);
                  }
                }

                @Override
                public void failure(RetrofitError error) {
                  //
                }
              };
              //Task callback
              final Callback<ParsableList<Task>> callback2 = new Callback<ParsableList<Task>>() {
                @Override
                public void success(ParsableList<Task> taskParsableList, Response response) {
                  task_ds.get(position).add(taskParsableList);
                  if ((taskParsableList.data != null) && (taskParsableList.data.length != 0)) {
                    int length = taskParsableList.data.length;
                    for (int i=0; i < length; i++) {
                      occurrences.get(position).add(taskParsableList.data[i]);
                      Collections.sort(occurrences.get(position));
                    }
                  }
                  adapter.notifyItemChanged(position); //update views
                  if (task_ds.get(position).data.length < taskParsableList.getSize()) {
                    connector.getService().getTasksByCalendarId(true, task_ds.get(position).data.length, start, end, calendar.getId(), this);
                  }
                }

                @Override
                public void failure(RetrofitError error) {
                  //
                }
              };
              if ((event_ds.get(position).data != null) && (event_ds.get(position).data.length > 0)) {
                connector.getService().getEventsByCalendarId(true, event_ds.get(position).data.length, start, end, calendar.getId(), callback1);
              } else {
                connector.getService().getEventsByCalendarId(true, 0, start, end, calendar.getId(), callback1);
              }
              if ((task_ds.get(position).data != null) && (task_ds.get(position).data.length > 0)) {
                connector.getService().getTasksByCalendarId(true, task_ds.get(position).data.length, start, end, calendar.getId(), callback2);
              } else {
                connector.getService().getTasksByCalendarId(true, 0, start, end, calendar.getId(), callback2);
              }
            }
          }
        }
      }

      @Override
      public void failure(RetrofitError error) {

      }
    };
    connector.getService().getCalendars(true, 0, callback); //calendar loading starts here
  }
}
