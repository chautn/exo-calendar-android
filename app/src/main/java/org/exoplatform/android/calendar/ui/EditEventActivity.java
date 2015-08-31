package org.exoplatform.android.calendar.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.exoplatform.android.calendar.ExoCalendarApp;
import org.exoplatform.android.calendar.R;
import org.exoplatform.calendar.client.model.Event;
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chautn on 8/31/15.
 */
public class EditEventActivity extends AppCompatActivity {

  public TextView textView;
  public Button cancel, save;

  public Event event;
  public String id;
  public int position;

  public ExoCalendarConnector connector;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event);
    setView();
    cancel = (Button) findViewById(R.id.event_cancel);
    save = (Button) findViewById(R.id.event_save);

    connector = ((ExoCalendarApp) getApplicationContext()).getConnector();
    String itemJson = getIntent().getStringExtra("itemJson");
    event = connector.gson.fromJson(itemJson, Event.class);
    id = event.getId();
    position = getIntent().getIntExtra("position", -1);

    updateViewFromItem(event);

    cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setResult(Activity.RESULT_CANCELED);
        finish();
      }
    });
    save.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateItemFromView(event);
        Callback<Response> callback = new Callback<Response>() {
          @Override
          public void success(Response response, Response response2) {
            Intent intent = new Intent();
            intent.putExtra("itemJson", connector.gson.toJson(event));
            intent.putExtra("position", position);
            intent.putExtra("id", id);
            setResult(Activity.RESULT_OK, intent);
            finish();
          }

          @Override
          public void failure(RetrofitError error) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditEventActivity.this);
            builder.setMessage("Update can't be done at the moment, please try again later!");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_CANCELED);
                finish();
              }
            });
            builder.show();
          }
        };
        connector.getService().updateEventById(event, id, callback);
      }
    });
  }

  public void setView() {
    textView = (TextView) findViewById(R.id.event_subject);
  }

  public void updateViewFromItem(Event event) {
    textView.setText(event.getSubject());
  }

  public void updateItemFromView(Event event) {
    event.setSubject(textView.getText().toString());
  }
}
