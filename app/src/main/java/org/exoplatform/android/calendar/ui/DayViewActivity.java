package org.exoplatform.android.calendar.ui;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
 * Created by chautn on 8/28/15.
 */
public class DayViewActivity extends AppCompatActivity implements OccurrenceViewFragment.CommunicationInterface {

  public Date date;
  public ParsableList<ExoCalendar> calendar_ds;
  public ParsableList<Event> event_ds;
  public ParsableList<Task> task_ds;
  public List<ComparableOccurrence> occurrences;
  public RecyclerView recyclerView;
  public RecyclerView.LayoutManager layoutManager;
  public DayViewAdapter adapter;
  public ExoCalendarConnector connector;

  public ActionBar actionBar;
  public Toolbar toolbar;
  public TextView caption;
  public TextView next;
  public TextView prev;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.day);
    toolbar = (Toolbar) findViewById(R.id.d_toolbar);
    caption = (TextView) findViewById(R.id.d_toolbar_date);
    next = (TextView) findViewById(R.id.next_day);
    prev = (TextView) findViewById(R.id.previous_day);
    setSupportActionBar(toolbar);
    actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(false);
    recyclerView = (RecyclerView) findViewById(R.id.day_recyclerview);
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    init();
    System.out.println(date);
    caption.setText((new SimpleDateFormat("MMM dd ''yy")).format(date));
    connector = ((ExoCalendarApp) getApplicationContext()).getConnector();
    adapter = new DayViewAdapter(this, connector, date, occurrences);
    recyclerView.setAdapter(adapter);

    next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onClickNext();
      }
    });
    prev.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onClickPrev();
      }
    });
    // click on the date caption in toolbar should bring it back to today.
    caption.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        today();
      }
    });

    download();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_day, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.create_event:
        createEvent();
        return true;
      case R.id.create_task:
        createTask();
        return true;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  public void init() {
    Intent intent = getIntent();
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.clear(Calendar.MINUTE);
    cal.clear(Calendar.SECOND);
    cal.clear(Calendar.MILLISECOND);
    if (intent.hasExtra("date")) {
      date = new Date(intent.getLongExtra("date", cal.getTimeInMillis()));
    } else {
      date = cal.getTime();
    }

    calendar_ds = new ParsableList<ExoCalendar>();
    event_ds = new ParsableList<Event>();
    task_ds = new ParsableList<Task>();
    occurrences = new ArrayList<ComparableOccurrence>();
  }

  public void reset() {
    calendar_ds = new ParsableList<ExoCalendar>();
    event_ds = new ParsableList<Event>();
    task_ds = new ParsableList<Task>();
    occurrences = new ArrayList<ComparableOccurrence>();
    adapter = new DayViewAdapter(this, connector, date, occurrences);
    recyclerView.setAdapter(adapter);
    removeFragment();
  }

  public void download() {
    final Callback<ParsableList<ExoCalendar>> callback = new Callback<ParsableList<ExoCalendar>>() {
      @Override
      public void success(ParsableList<ExoCalendar> exoCalendarParsableList, Response response) {
        calendar_ds.add(exoCalendarParsableList);
        if (calendar_ds.data.length < exoCalendarParsableList.getSize()) {
          connector.getService().getCalendars(true, calendar_ds.data.length, this);
        } else {
          DateFormat df = new SimpleDateFormat(ComparableOccurrence.iso8601dateformat);
          final String start = df.format(date);
          final String end = df.format(new Date(date.getTime() + 1000*60*60*24 -1));
          for (final ExoCalendar calendar : calendar_ds.data) {
            final Callback<ParsableList<Event>> callback1 = new Callback<ParsableList<Event>>() {
              @Override
              public void success(ParsableList<Event> eventParsableList, Response response) {
                event_ds.add(eventParsableList);
                if ((eventParsableList.data != null) && (eventParsableList.data.length != 0)) {
                  for (int i=0; i < eventParsableList.data.length; i++) {
                    occurrences.add(eventParsableList.data[i]);
                    Collections.sort(occurrences);
                  }
                }
                adapter.notifyDataSetChanged();
                if (event_ds.data.length < eventParsableList.getSize()) {
                  connector.getService().getEventsByCalendarId(true, event_ds.data.length, start, end, calendar.getId(), this);
                }
              }

              @Override
              public void failure(RetrofitError error) {

              }
            };

            final Callback<ParsableList<Task>> callback2 = new Callback<ParsableList<Task>>() {
              @Override
              public void success(ParsableList<Task> taskParsableList, Response response) {
                task_ds.add(taskParsableList);
                if ((taskParsableList != null) && (taskParsableList.data.length != 0)) {
                  for (int i=0; i < taskParsableList.data.length; i++) {
                    occurrences.add(taskParsableList.data[i]);
                  }
                }
                adapter.notifyDataSetChanged();
                if (task_ds.data.length < taskParsableList.getSize()) {
                  connector.getService().getTasksByCalendarId(true, task_ds.data.length, start, end, calendar.getId(), this);
                }
              }

              @Override
              public void failure(RetrofitError error) {

              }
            };
            if ((event_ds.data != null) && (event_ds.data.length > 0)) {
              connector.getService().getEventsByCalendarId(true, event_ds.data.length, start, end, calendar.getId(), callback1);
            } else {
              connector.getService().getEventsByCalendarId(true, 0, start, end, calendar.getId(), callback1);
            }
            if ((task_ds.data != null) && (task_ds.data.length > 0)) {
              connector.getService().getTasksByCalendarId(true, task_ds.data.length, start, end, calendar.getId(), callback2);
            } else {
              connector.getService().getTasksByCalendarId(true, 0, start, end, calendar.getId(), callback2);
            }
           }
        }
      }

      @Override
      public void failure(RetrofitError error) {
        //
      }
    };
    connector.getService().getCalendars(true, 0, callback); //calendar loading starts here
  }

  public void onClickNext() {
    date = new Date(date.getTime() + 1000*60*60*24);
    caption.setText((new SimpleDateFormat("MMM dd ''yy")).format(date));
    reset();
    download();
  }
  public void onClickPrev() {
    date = new Date(date.getTime() - 1000*60*60*24);
    caption.setText((new SimpleDateFormat("MMM dd ''yy")).format(date));
    reset();
    download();
  }
  public void today() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.clear(Calendar.MINUTE);
    cal.clear(Calendar.SECOND);
    cal.clear(Calendar.MILLISECOND);
    date = cal.getTime();
    caption.setText((new SimpleDateFormat("MMM dd ''yy")).format(date));
    reset();
    download();
  }

  public void createEvent() {
    if ((calendar_ds.data == null) || (calendar_ds.data.length == 0)) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("You've no calendar. Create one now?");
      builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
      });
      builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          createCalendar();
        }
      });
      builder.setNeutralButton("Take me to calendar view", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          Intent intent = new Intent(DayViewActivity.this, ManageCalendarActivity.class);
          startActivity(intent);
        }
      });
      builder.show();
    } else {
      //Intent intent = new Intent(DayViewActivity.this, EventActivity.class);
      //startActivity(intent);
    }
  }

  public void createTask() {
    //
  }

  public void createCalendar() {

  }

  //Implements CommunicationInterface
  public ExoCalendarConnector getConnector() {
    return connector;
  }
  public List<ComparableOccurrence> getOccurrenceList() {
    return occurrences;
  }
  public void onItemDeleted(int position, String id) {
    occurrences.remove(position);
    adapter.notifyItemRemoved(position);
    OccurrenceViewFragment fragment = (OccurrenceViewFragment) getFragmentManager().findFragmentById(R.id.day_fragment_container);
    if (fragment != null) {
      getFragmentManager().beginTransaction().remove(fragment).commit();
    }
  }
  public void onItemUpdated(int position, String id, ComparableOccurrence item) {
    occurrences.set(position, item);
    adapter.notifyItemChanged(position);
  }

  //Item click
  public void onItemClick(int position) {
    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    OccurrenceViewFragment fragment = new OccurrenceViewFragment();
    Bundle args = new Bundle();
    args.putInt("position", position);
    fragment.setArguments(args);
    fragmentTransaction.replace(R.id.day_fragment_container, fragment);
    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
    fragmentTransaction.commit();
  }
  public void removeFragment() {
    OccurrenceViewFragment fragment = (OccurrenceViewFragment) getFragmentManager().findFragmentById(R.id.day_fragment_container);
    if (fragment != null) {
      getFragmentManager().beginTransaction().remove(fragment).commit();
    }
  }
}
