package org.exoplatform.android.calendar.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.exoplatform.android.calendar.ExoCalendarApp;
import org.exoplatform.android.calendar.R;
import org.exoplatform.calendar.client.model.ComparableOccurrence;
import org.exoplatform.calendar.client.model.Event;
import org.exoplatform.calendar.client.model.ExoCalendar;
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chautn on 8/31/15.
 */
public class EditEventActivity extends AppCompatActivity {

  public static final String RECEIVED_INTENT_KEY_CALENDAR_JSON_LIST = "calendarJsonList";
  public static final String RECEIVED_INTENT_KEY_EVENT_JSON = "itemJson";
  public static final String RECEIVED_INTENT_KEY_EVENT_ID = "itemId";
  public static final String RECEIVED_INTENT_KEY_POSITION = "itemPosition";
  public static final String RECEIVED_INTENT_KEY_DATE = "date";
  public static final String RETURN_INTENT_KEY_EVENT_JSON = "itemJson";
  public static final String RETURN_INTENT_KEY_EVENT_ID = "itemId";
  public static final String RETURN_INTENT_KEY_POSITION = "itemPosition";
  public static final String RETURN_INTENT_KEY_OLD_DATE = "oldDate";
  public static final String RETURN_INTENT_KEY_NEW_DATE = "newDate";

  public EditText editTextTitle, editTextDescription;
  public TextView cancel, save;
  public EditText editTextFromDate, editTextToDate;
  public Spinner spinnerFromTime, spinnerToTime;
  public Spinner spinnerCalendarName;

  public Event event;
  public Date date;
  public boolean isValidatedOnView;

  public ExoCalendarConnector connector;
  public ArrayList<String> calendarJsonList, calendarIdList, calendarNameList;

  public DatePickerDialog fromDatePickerDialog, toDatePickerDialog;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event);
    isValidatedOnView = false;
    connector = ((ExoCalendarApp) getApplicationContext()).getConnector();
    /**
     * This implementation uses offline Calendar list which must be passed from calling activities (e.g DayViewActivity).
     */
    calendarJsonList = getIntent().getStringArrayListExtra(EditEventActivity.RECEIVED_INTENT_KEY_CALENDAR_JSON_LIST);
    calendarNameList = new ArrayList<String>();
    calendarIdList = new ArrayList<String>();
    for (String calendarJson : calendarJsonList) {
      calendarIdList.add(connector.gson.fromJson(calendarJson, ExoCalendar.class).getId());
      calendarNameList.add(connector.gson.fromJson(calendarJson, ExoCalendar.class).getName());
    }
    setView();
    createItemFromJson();
    updateViewFromItem(event);
  }

  public void createItemFromJson() {
    String itemJson = getIntent().getStringExtra(EditEventActivity.RECEIVED_INTENT_KEY_EVENT_JSON);
    event = connector.gson.fromJson(itemJson, Event.class);
  }

  public void setView() {
    editTextTitle = (EditText) findViewById(R.id.event_subject);
    editTextDescription = (EditText) findViewById(R.id.event_description);
    cancel = (TextView) findViewById(R.id.event_cancel);
    save = (TextView) findViewById(R.id.event_save);
    editTextFromDate = (EditText) findViewById(R.id.event_from_date);
    editTextToDate = (EditText) findViewById(R.id.event_to_date);
    spinnerFromTime = (Spinner) findViewById(R.id.event_from_time);
    spinnerToTime = (Spinner) findViewById(R.id.event_to_time);
    spinnerCalendarName = (Spinner) findViewById(R.id.event_calendar_name);

    editTextFromDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Calendar cal = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(EditEventActivity.this,
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal_ = Calendar.getInstance();
                cal_.set(year, monthOfYear, dayOfMonth);
                editTextFromDate.setText((new SimpleDateFormat("MM/dd/yyyy")).format(cal_.getTime()));
              }
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setCalendarViewShown(false); // doesn't work???
        fromDatePickerDialog.getDatePicker().setSpinnersShown(true); // doesn't work???
        fromDatePickerDialog.show();
      }
    });
    editTextToDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Calendar cal = Calendar.getInstance();
        toDatePickerDialog = new DatePickerDialog(EditEventActivity.this,
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal_ = Calendar.getInstance();
                cal_.set(year, monthOfYear, dayOfMonth);
                editTextToDate.setText((new SimpleDateFormat("MM/dd/yyyy")).format(cal_.getTime()));
              }
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        toDatePickerDialog.getDatePicker().setCalendarViewShown(false); // doesn't work???
        toDatePickerDialog.getDatePicker().setSpinnersShown(true); // doesn't work???
        toDatePickerDialog.show();
      }
    });

    ArrayAdapter<CharSequence> time_spinner_adapter = ArrayAdapter.createFromResource(this,
        R.array.time, android.R.layout.simple_spinner_item);
    spinnerFromTime.setAdapter(time_spinner_adapter);
    spinnerToTime.setAdapter(time_spinner_adapter); //is it safe to use the same adapter?

    /**
     * Binds calendar list to UI.
     * This implementation uses offline Calendar list which must be passed from calling activities (e.g DayViewActivity).
     */
    ArrayAdapter<String> calendar_spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, calendarNameList);
    spinnerCalendarName.setAdapter(calendar_spinner_adapter);

    cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onCancel();
      }
    });
    save.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onSave();
      }
    });
  }

  public void updateViewFromItem(Event event) {
    editTextTitle.setText(event.getSubject());
    editTextDescription.setText(event.getDescription());
    editTextFromDate.setText((new SimpleDateFormat("MM/dd/yyyy")).format(event.getStartDate()));
    editTextToDate.setText((new SimpleDateFormat("MM/dd/yyyy")).format(event.getEndDate()));

    //TODO : need to prevent invalid data (like 00:11) from causing problem
    String from_ = (new SimpleDateFormat("HH:mm")).format(event.getStartDate());
    String to_ = (new SimpleDateFormat("HH:mm")).format(event.getEndDate());
    spinnerFromTime.setSelection(((ArrayAdapter<CharSequence>) spinnerFromTime.getAdapter()).getPosition(from_));
    spinnerToTime.setSelection(((ArrayAdapter<CharSequence>) spinnerFromTime.getAdapter()).getPosition(to_));

    String calendar_id = event.getCalendarId();
    spinnerCalendarName.setSelection(((ArrayAdapter<CharSequence>) spinnerCalendarName.getAdapter()).getPosition(calendar_id));
  }

  public void updateItemFromView(Event event) {
    event.setSubject(editTextTitle.getText().toString());
    event.setDescription(editTextDescription.getText().toString());
    String toDateTime = editTextToDate.getText().toString() + "T" + spinnerToTime.getSelectedItem().toString();
    String fromDateTime = editTextFromDate.getText().toString() + "T" + spinnerFromTime.getSelectedItem().toString();
    Date from_, to_;
    try {
      from_ = (new SimpleDateFormat("MM/dd/yyyy'T'HH:mm")).parse(fromDateTime);
      to_ = (new SimpleDateFormat("MM/dd/yyyy'T'HH:mm")).parse(toDateTime);
      event.setFrom((new SimpleDateFormat(ComparableOccurrence.iso8601dateformat)).format(from_));
      event.setTo((new SimpleDateFormat(ComparableOccurrence.iso8601dateformat)).format(to_));
    } catch (Exception e) {}
  }

  public void validateOnView() {
    //rule 1 : empty title is invalid.
    String title = editTextTitle.getText().toString();
    if (title.isEmpty()) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Please input a title!");
      builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          //
        }
      });
      builder.show();
      return; //stop on any rule violation.
    }
    //rule 2: fromDate mustn't be empty and must comply "MM/dd/yyyy" format.
    String fromDate = editTextFromDate.getText().toString();
    try {
      Date date = (new SimpleDateFormat("MM/dd/yyyy")).parse(fromDate);
    } catch (Exception e) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Please input From Date field in format \"MM/dd/yyyy\"");
      builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
      });
      builder.show();
      return;
    }
    //rule 3: toDate mustn't be empty and must comply "MM/dd/yyyy" format.
    String toDate = editTextFromDate.getText().toString();
    try {
      Date date = (new SimpleDateFormat("MM/dd/yyyy")).parse(toDate);
    } catch (Exception e) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Please input To Date field in format \"MM/dd/yyyy\"");
      builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
      });
      builder.show();
      return;
    }
    //rule 4: toDateTime must be later than fromDateTime.
    String toDateTime = editTextToDate.getText().toString() + "T" + spinnerToTime.getSelectedItem().toString();
    String fromDateTime = editTextFromDate.getText().toString() + "T" + spinnerFromTime.getSelectedItem().toString();
    try {
      Date from = (new SimpleDateFormat("MM/dd/yyyy'T'HH:mm")).parse(fromDateTime);
      Date to = (new SimpleDateFormat("MM/dd/yyyy'T'HH:mm")).parse(toDateTime);
      if (!from.before(to)) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("'To' must be later than 'From'");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        });
        builder.show();
        return;
      }
    } catch (Exception e) {
      return;
    }
    //rule 5: calendar id mustn't be empty.
    //though current implementation assures calendar list always is pre-filled and cannot be non-selected, better check than sorry.
    try {
      String calendar_id = calendarIdList.get(spinnerCalendarName.getSelectedItemPosition());
      if ((calendar_id == null) || (calendar_id.isEmpty())) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please select a calendar!'");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        });
        builder.show();
        return;
      }
    } catch (Exception e) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Please select a calendar!'");
      builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
      });
      builder.show();
      return;
    }
    //this is called only if all rules passed.
    isValidatedOnView = true;
  }

  public void onCancel() {
    setResult(RESULT_CANCELED);
    finish();
  }
  public void onSave() {
    validateOnView();
    updateItemFromView(event);
    if (isValidatedOnView) {
      final String event_id = getIntent().getStringExtra(EditEventActivity.RECEIVED_INTENT_KEY_EVENT_ID);
      final Long oldDate = getIntent().getLongExtra(EditEventActivity.RECEIVED_INTENT_KEY_DATE, -1);
      final int itemPosition = getIntent().getIntExtra(EditEventActivity.RECEIVED_INTENT_KEY_POSITION, -1);
      Callback<Response> callback = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
          Intent intent = new Intent();
          intent.putExtra(EditEventActivity.RETURN_INTENT_KEY_EVENT_JSON, connector.gson.toJson(event));
          intent.putExtra(EditEventActivity.RETURN_INTENT_KEY_EVENT_ID, event_id);
          intent.putExtra(EditEventActivity.RETURN_INTENT_KEY_POSITION, itemPosition);
          intent.putExtra(EditEventActivity.RETURN_INTENT_KEY_OLD_DATE, oldDate);
          intent.putExtra(EditEventActivity.RETURN_INTENT_KEY_NEW_DATE, event.getStartDate().getTime());

          setResult(RESULT_OK, intent);
          finish();
        }

        @Override
        public void failure(RetrofitError error) {
          Toast.makeText(EditEventActivity.this, "Save can't be done in the moment, please try again later!", Toast.LENGTH_SHORT).show();
        }
      };
      connector.getService().updateEventById(event, event_id, callback);
    }
  }

  public void test(View view) {
    System.out.println(event.getEndDate());
    System.out.println(event.getEnd24());
    System.out.println(event.getEndAMPM());
  }
}
