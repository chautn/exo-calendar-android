package org.exoplatform.android.calendar.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.exoplatform.android.calendar.ExoCalendarApp;
import org.exoplatform.android.calendar.R;
import org.exoplatform.calendar.client.model.ExoCalendar;
import org.exoplatform.calendar.client.model.ParsableList;
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;
import org.exoplatform.calendar.client.rest.ExoCalendarRestService;
import org.w3c.dom.Text;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chautn on 8/17/15.
 */
public class ManageCalendarActivity extends Activity {

  public RecyclerView calendar_list_view;
  public RecyclerView.LayoutManager layoutManager;
  public Button create_calendar_btn;
  ParsableList<ExoCalendar> calendar_ds = new ParsableList<ExoCalendar>();
  int return_size = 0;
  ExoCalendarConnector connector;
  CalendarAdapter adapter;


  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.manage_calendar_activity);
    calendar_list_view = (RecyclerView) findViewById(R.id.calendar_list_view);
    create_calendar_btn = (Button) findViewById(R.id.create_calendar_btn);

    connector = ((ExoCalendarApp) getApplicationContext()).getConnector();
    final ExoCalendarRestService service = connector.getService();
    adapter = new CalendarAdapter(this, connector, calendar_ds);

    layoutManager = new LinearLayoutManager(this);
    calendar_list_view.setLayoutManager(layoutManager);
    calendar_list_view.setAdapter(adapter);

    create_calendar_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View caller) {
        AlertDialog.Builder builder = new AlertDialog.Builder(caller.getContext());
        builder.setTitle("New calendar");
        LayoutInflater inflater = (LayoutInflater) caller.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            service.createCalendar(to_be_created_calendar, callback);
          }
        });
        builder.show();
      }
    });

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
}
