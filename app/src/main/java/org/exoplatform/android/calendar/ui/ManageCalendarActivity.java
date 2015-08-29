package org.exoplatform.android.calendar.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.exoplatform.android.calendar.ExoCalendarApp;
import org.exoplatform.android.calendar.R;
import org.exoplatform.calendar.client.model.ExoCalendar;
import org.exoplatform.calendar.client.model.ParsableList;
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;
import org.exoplatform.calendar.client.rest.ExoCalendarRestService;

import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chautn on 8/17/15.
 */
public class ManageCalendarActivity extends AppCompatActivity {

  public RecyclerView calendar_list_view;
  public RecyclerView.LayoutManager layoutManager;
  public ParsableList<ExoCalendar> calendar_ds = new ParsableList<ExoCalendar>();
  int return_size = 0;
  public ExoCalendarConnector connector;
  public CalendarAdapter adapter;

  public ActionBar actionBar;
  public Toolbar toolbar;
  public TextView timezoneView;


  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.manage_calendar_activity);
    Toolbar toolbar = (Toolbar) findViewById(R.id.calendar_toolbar);
    timezoneView = (TextView) findViewById(R.id.calendar_timezone);
    setSupportActionBar(toolbar);
    ActionBar bar = getSupportActionBar();
    bar.setDisplayShowTitleEnabled(false);

    TimeZone timeZone = TimeZone.getDefault();
    timezoneView.setText(getString(R.string.your_timezone_is) + ": " + timeZone.getDisplayName(false, TimeZone.SHORT));

    calendar_list_view = (RecyclerView) findViewById(R.id.calendar_list_view);

    connector = ((ExoCalendarApp) getApplicationContext()).getConnector();
    final ExoCalendarRestService service = connector.getService();
    adapter = new CalendarAdapter(this, connector, calendar_ds);

    layoutManager = new LinearLayoutManager(this);
    calendar_list_view.setLayoutManager(layoutManager);
    calendar_list_view.setAdapter(adapter);

    final Callback<ParsableList<ExoCalendar>> callback = new Callback<ParsableList<ExoCalendar>>() {
      @Override
      public void success(ParsableList<ExoCalendar> exoCalendarParsableList, Response response) {
        calendar_ds.add(exoCalendarParsableList);
        return_size = exoCalendarParsableList.getSize();
        if (calendar_ds.data.length < return_size) {
          service.getCalendars(true, calendar_ds.data.length, this);
        }
        adapter.notifyDataSetChanged();
      }

      @Override
      public void failure(RetrofitError error) {
        //
      }
    };
    service.getCalendars(true, 0, callback);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu1, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case R.id.this_week:
        Intent intent_w = new Intent(this, WeekViewActivity.class);
        startActivity(intent_w);
        return true;
      case R.id.today:
        Intent intent_d = new Intent(this, DayViewActivity.class);
        startActivity(intent_d);
        return true;
      case R.id.create_calendar:
        createCalendar();
        return true;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  public void createCalendar() {
    //
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("New calendar");
    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.calendar_dialog, null);
    final TextView calendar_name = (TextView) layout.findViewById(R.id.calendar_dialog_name);
    final TextView calendar_description = (TextView) layout.findViewById(R.id.calendar_dialog_description);
    builder.setView(layout);

    final ExoCalendar to_be_created_calendar = new ExoCalendar();
    final ParsableList<ExoCalendar> another = new ParsableList<ExoCalendar>();
    another.data = new ExoCalendar[]{to_be_created_calendar};

    builder.setNegativeButton("Cancel", null);
    builder.setPositiveButton("Save", new AlertDialog.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int which) {
      to_be_created_calendar.setName(calendar_name.getText().toString());
      to_be_created_calendar.setDescription(calendar_description.getText().toString());
      Callback<Response> callback = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
          calendar_ds.add(another);
          adapter.notifyItemInserted(calendar_ds.data.length);
        }

        @Override
        public void failure(RetrofitError error) {
          //
        }
      };
      connector.getService().createCalendar(to_be_created_calendar, callback);
      }
    });
    builder.show();
  }
}
