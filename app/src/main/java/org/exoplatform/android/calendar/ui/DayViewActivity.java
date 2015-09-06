package org.exoplatform.android.calendar.ui;

import android.app.AlertDialog;
import android.app.Fragment;
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

import com.google.gson.Gson;

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
import java.util.Arrays;
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
public class DayViewActivity extends AppCompatActivity implements DetailFragmentCommunication {

  public static final int CREATE_EVENT = 1;
  public static final int CREATE_TASK = 2;

  private Date date; //this object must be assured to always have hour = minute = second = millisecond = 0;
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
    actionBar.getThemedContext().setTheme(R.style.ActionBarTheme);
    recyclerView = (RecyclerView) findViewById(R.id.day_recyclerview);
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    init();
    caption.setText((new SimpleDateFormat("MMM dd ''yy")).format(date));
    connector = ((ExoCalendarApp) getApplicationContext()).getConnector();
    adapter = new DayViewAdapter(this, calendar_ds, connector, date, occurrences);
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
    Long timeInMillis = Calendar.getInstance().getTimeInMillis();
    if (intent.hasExtra("date")) {
      timeInMillis = intent.getLongExtra("date", timeInMillis);
    }
    setDate(timeInMillis);

    calendar_ds = new ParsableList<>();
    event_ds = new ParsableList<>();
    task_ds = new ParsableList<>();
    occurrences = new ArrayList<>();
  }

  public void reset() {
    calendar_ds = new ParsableList<>();
    event_ds = new ParsableList<>();
    task_ds = new ParsableList<>();
    occurrences = new ArrayList<>();
    adapter = new DayViewAdapter(this, calendar_ds, connector, date, occurrences);
    recyclerView.setAdapter(adapter);
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
                  }
                }
                Collections.sort(occurrences);
                adapter.notifyDataSetChanged();
                if (event_ds.data.length < eventParsableList.getSize()) {
                  connector.getService().getEventsByCalendarId(true, event_ds.data.length, start, end, calendar.getId(), this);
                } else {
                  Collections.sort(occurrences);
                  adapter.notifyDataSetChanged();
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
                Collections.sort(occurrences);
                adapter.notifyDataSetChanged();
                if (task_ds.data.length < taskParsableList.getSize()) {
                  connector.getService().getTasksByCalendarId(true, task_ds.data.length, start, end, calendar.getId(), this);
                } else {
                  Collections.sort(occurrences);
                  adapter.notifyDataSetChanged();
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
    removeFragment();
    reset();
    download();
  }
  public void onClickPrev() {
    date = new Date(date.getTime() - 1000*60*60*24);
    caption.setText((new SimpleDateFormat("MMM dd ''yy")).format(date));
    removeFragment();
    reset();
    download();
  }
  public void today() {
    setDate(Calendar.getInstance().getTimeInMillis());
    caption.setText((new SimpleDateFormat("MMM dd ''yy")).format(date));
    removeFragment();
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
      builder.setNeutralButton("Go to calendar view", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          Intent intent = new Intent(DayViewActivity.this, ManageCalendarActivity.class);
          startActivity(intent);
        }
      });
      builder.show();
    } else {
      Intent intent = new Intent(DayViewActivity.this, NewEventActivity.class);
      intent.putExtra(NewEventActivity.RECEIVED_INTENT_KEY_DATE, date.getTime());
      ArrayList<String> calendarJsonList = new ArrayList<>();
      for (ExoCalendar exoCalendar : calendar_ds.data) {
        calendarJsonList.add(connector.gson.toJson(exoCalendar));
      }
      intent.putStringArrayListExtra(NewEventActivity.RECEIVED_INTENT_KEY_CALENDAR_JSON_LIST, calendarJsonList);
      startActivityForResult(intent, CREATE_EVENT);
    }
  }

  public void createTask() {
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
      Intent intent = new Intent(DayViewActivity.this, NewTaskActivity.class);
      intent.putExtra(NewTaskActivity.RECEIVED_INTENT_KEY_DATE, date.getTime());
      ArrayList<String> calendarJsonList = new ArrayList<>();
      for (ExoCalendar exoCalendar : calendar_ds.data) {
        calendarJsonList.add(connector.gson.toJson(exoCalendar));
      }
      intent.putStringArrayListExtra(NewTaskActivity.RECEIVED_INTENT_KEY_CALENDAR_JSON_LIST, calendarJsonList);
      startActivityForResult(intent, CREATE_TASK);
    }
  }

  public void createCalendar() {
    //TODO
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      Long returned_date = 0L;
      if (requestCode == CREATE_EVENT) {
        returned_date = data.getLongExtra(NewEventActivity.RETURNED_INTENT_KEY_DATE, 0L);
      }
      if (requestCode == CREATE_TASK) {
        returned_date = data.getLongExtra(NewTaskActivity.RETURNED_INTENT_KEY_DATE, 0L);
      }
      if (returned_date != 0L) {
        //refresh the view with the returned date -- no matter it is the same as or different from the current date
        setDate(returned_date);
        caption.setText((new SimpleDateFormat("MMM dd ''yy")).format(date));
        removeFragment();
        reset();
        download();
      }
    }
  }

  //Implements DetailFragmentCommunication
  public void onItemDeleted(String itemId) {
    int length = occurrences.size();
    for (int i=0; i < length; i++) {
      if (itemId.equals(occurrences.get(i).getId())) {
        occurrences.remove(i);
        adapter.notifyItemRemoved(i);
        break;
      }
    }
    Fragment fragment = getFragmentManager().findFragmentById(R.id.day_fragment_container);
    if (fragment != null) {
      getFragmentManager().beginTransaction().remove(fragment).commit();
    }
  }

  public void onItemUpdated(String itemId, ComparableOccurrence item) {
    int length = occurrences.size();
    for (int i=0; i < length; i++) {
      if (itemId.equals(occurrences.get(i).getId())) {
        occurrences.set(i, item);
        Collections.sort(occurrences);
        adapter.notifyDataSetChanged();
        break;
      }
    }
  }

  public int getItemColor(String calendarId) {
    String color_name = "";
    int length = calendar_ds.data.length;
    for (int i=0; i < length; i++) {
      if (calendarId.equals(calendar_ds.data[i].getId())) {
        color_name = calendar_ds.data[i].getColor();
        break;
      }
    }
    return getResources().getIdentifier(color_name, "color", getPackageName());
  }

  public String getCalendarName(String calendarId) {
    String calendar_name = "";
    int length = calendar_ds.data.length;
    for (int i=0; i < length; i++) {
      if (calendarId.equals(calendar_ds.data[i].getId())) {
        calendar_name = calendar_ds.data[i].getName();
        break;
      }
    }
    return calendar_name;
  }

  public ArrayList<String> getCalendarJsonList() {
    ArrayList<String> calendarJsonList = new ArrayList<>();
    Gson gson = connector.gson;
    int length = calendar_ds.data.length;
    for (int i=0; i < length; i++) {
      String calendarJson = gson.toJson(calendar_ds.data[i]);
      calendarJsonList.add(calendarJson);
    }
    return calendarJsonList;
  }

  //Item click (show item in fragment)
  public void onItemClick(int position) {
    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    ComparableOccurrence item = occurrences.get(position);
    String itemJson = connector.gson.toJson(item);
    if (item instanceof Event) {
      EventFragment fragment = new EventFragment();
      Bundle args = new Bundle();
      args.putString(EventFragment.ARGUMENT_BUNDLE_KEY_ITEM_JSON, itemJson);
      args.putLong(EventFragment.ARGUMENT_BUNDLE_KEY_DATE, date.getTime());
      fragment.setArguments(args);
      fragmentTransaction.replace(R.id.day_fragment_container, fragment);
      fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
      fragmentTransaction.commit();
    } else if (item instanceof Task) {
      TaskFragment fragment = new TaskFragment();
      Bundle args = new Bundle();
      args.putString(TaskFragment.ARGUMENT_BUNDLE_KEY_ITEM_JSON, itemJson);
      args.putLong(TaskFragment.ARGUMENT_BUNDLE_KEY_DATE, date.getTime());
      fragment.setArguments(args);
      fragmentTransaction.replace(R.id.day_fragment_container, fragment);
      fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
      fragmentTransaction.commit();
    }
  }
  public void removeFragment() {
    Fragment fragment = getFragmentManager().findFragmentById(R.id.day_fragment_container);
    if (fragment != null) {
      getFragmentManager().beginTransaction().remove(fragment).commit();
    }
  }

  // use this method to set date to assure its time is always 00:00:00.000
  public void setDate(Long timeInMillis) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(timeInMillis);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.clear(Calendar.MINUTE);
    cal.clear(Calendar.SECOND);
    cal.clear(Calendar.MILLISECOND);
    this.date = cal.getTime();
  }
  public Date getDate() {
    return date;
  }
}
