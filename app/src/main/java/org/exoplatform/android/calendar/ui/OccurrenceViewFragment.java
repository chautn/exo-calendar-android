package org.exoplatform.android.calendar.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.exoplatform.android.calendar.R;
import org.exoplatform.calendar.client.model.ComparableOccurrence;
import org.exoplatform.calendar.client.model.Event;
import org.exoplatform.calendar.client.model.Task;
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;
import org.exoplatform.calendar.client.rest.ExoCalendarRestService;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chautn on 8/31/15.
 */
public class OccurrenceViewFragment extends Fragment {

  public static final int EDIT_EVENT = 1;
  public static final int EDIT_TASK = 2;

  public TextView textView;
  Button edit, delete;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View fragmentLayout = inflater.inflate(R.layout.fragment_occurrence, container, false);
    textView = (TextView) fragmentLayout.findViewById(R.id.fragment_occurrence_title);
    edit = (Button) fragmentLayout.findViewById(R.id.fragment_occurrence_edit);
    delete = (Button) fragmentLayout.findViewById(R.id.fragment_occurrence_delete);

    final List<ComparableOccurrence> list = ((CommunicationInterface) getActivity()).getOccurrenceList();
    final int position = getArguments().getInt("position");
    final ComparableOccurrence occurrence = list.get(position);
    final String id = occurrence.getId();

    //updateView(fragmentLayout, occurrence);
    textView.setText(occurrence.getTitle());

    delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String msg = (occurrence instanceof Task) ? "Delete this task?" : "Delete this event?";
        builder.setMessage(msg);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            //
          }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            ExoCalendarRestService service = ((CommunicationInterface) getActivity()).getConnector().getService();
            Callback<Response> callback = new Callback<Response>() {
              @Override
              public void success(Response response, Response response2) {
                ((CommunicationInterface) getActivity()).onItemDeleted(position, id);
              }

              @Override
              public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Deletion not done at the moment, please try again later!", Toast.LENGTH_SHORT).show();
              }
            };
            if (occurrence instanceof Task) {
              service.deleteTaskById(id, callback);
            } else {
              service.deleteEventById(id, callback);
            }
          }
        });

        builder.show();
      }
    });

    edit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Gson gson = ((CommunicationInterface) getActivity()).getConnector().gson;
        String occurrenceJson = gson.toJson(occurrence);
        if (occurrence instanceof Task) {
          Intent intent = new Intent(getActivity(), EditTaskActivity.class);
          intent.putExtra("itemJson", occurrenceJson);
          intent.putExtra("position", position);
          intent.putExtra("id", id);
          startActivityForResult(intent, EDIT_TASK);
        } else {
          Intent intent = new Intent(getActivity(), EditEventActivity.class);
          intent.putExtra("itemJson", occurrenceJson);
          intent.putExtra("position", position);
          intent.putExtra("id", id);
          startActivityForResult(intent, EDIT_EVENT);
        }
      }
    });

    return fragmentLayout;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
      String itemJson = data.getStringExtra("itemJson");
      Gson gson = ((CommunicationInterface) getActivity()).getConnector().gson;
      ComparableOccurrence item = (requestCode == EDIT_EVENT) ? gson.fromJson(itemJson, Event.class) : gson.fromJson(itemJson, Task.class);
      updateView(getView(), item);
      ((CommunicationInterface) getActivity()).onItemUpdated(getArguments().getInt("position"), item.getId(), item);
    }
  }

  public void updateView(View fragmentLayout, ComparableOccurrence item) {
    TextView textView = (TextView) fragmentLayout.findViewById(R.id.fragment_occurrence_title);
    textView.setText(item.getTitle());
  }

  public interface CommunicationInterface {

    public ExoCalendarConnector getConnector();

    public List<ComparableOccurrence> getOccurrenceList();

    public void onItemDeleted(int position, String id);

    public void onItemUpdated(int position, String id, ComparableOccurrence item);

  }
}
