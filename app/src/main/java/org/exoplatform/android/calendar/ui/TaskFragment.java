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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.exoplatform.android.calendar.ExoCalendarApp;
import org.exoplatform.android.calendar.R;
import org.exoplatform.calendar.client.model.Task;
import org.exoplatform.calendar.client.rest.ExoCalendarRestService;
import org.exoplatform.commons.utils.ISO8601;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chautn on 9/7/15.
 */
public class TaskFragment extends Fragment {
  
  public static final int REQUEST_CODE_EDIT_TASK = 1;
  public static final String ARGUMENT_BUNDLE_KEY_ITEM_JSON = "itemJson";
  public static final String ARGUMENT_BUNDLE_KEY_DATE = "date";
  
  public Task item;
  public String itemJson;
  public String itemId;
  
  public TextView textViewTitle, textViewDescription;
  public TextView textViewFrom, textViewTo;
  public TextView textViewPriority, textViewCalendar, textViewCategory;
  public TextView textViewStatus;
  public TextView edit, delete;
  public TableRow row1;
  
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View fragmentLayout = inflater.inflate(R.layout.fragment_task, container, false);
    row1 = (TableRow) fragmentLayout.findViewById(R.id.fragment_task_row1);
    textViewTitle = (TextView) fragmentLayout.findViewById(R.id.fragment_task_subject);
    textViewDescription = (TextView) fragmentLayout.findViewById(R.id.fragment_task_description);
    textViewFrom = (TextView) fragmentLayout.findViewById(R.id.fragment_task_from);
    textViewTo = (TextView) fragmentLayout.findViewById(R.id.fragment_task_to);
    textViewPriority = (TextView) fragmentLayout.findViewById(R.id.fragment_task_priority);
    textViewCalendar = (TextView) fragmentLayout.findViewById(R.id.fragment_task_calendar_name);
    textViewStatus = (TextView) fragmentLayout.findViewById(R.id.fragment_task_status);
    textViewCategory = (TextView) fragmentLayout.findViewById(R.id.fragment_task_category);
    edit = (TextView) fragmentLayout.findViewById(R.id.fragment_task_edit);
    delete = (TextView) fragmentLayout.findViewById(R.id.fragment_task_delete);

    itemJson = getArguments().getString(ARGUMENT_BUNDLE_KEY_ITEM_JSON);
    item = ((ExoCalendarApp) getActivity().getApplicationContext()).getConnector().gson.fromJson(itemJson, Task.class);
    itemId = item.getId();

    updateView();

    delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete this task?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            //
          }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            ExoCalendarRestService service = ((ExoCalendarApp) getActivity().getApplicationContext()).getConnector().getService();
            Callback<Response> callback = new Callback<Response>() {
              @Override
              public void success(Response response, Response response2) {
                ((DetailFragmentCommunication) getActivity()).onItemDeleted(itemId);
              }

              @Override
              public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Deletion can't be done at the moment, please try again later!", Toast.LENGTH_SHORT).show();
              }
            };
            service.deleteTaskById(itemId, callback);
          }
        });

        builder.show();
      }
    });

    edit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ArrayList<String> calendarJsonList = ((DetailFragmentCommunication)getActivity()).getCalendarJsonList();
        Intent intent = new Intent(getActivity(), EditTaskActivity.class);
        intent.putStringArrayListExtra(EditTaskActivity.RECEIVED_INTENT_KEY_CALENDAR_JSON_LIST, calendarJsonList);
        intent.putExtra(EditTaskActivity.RECEIVED_INTENT_KEY_TASK_JSON, itemJson);
        intent.putExtra(EditTaskActivity.RECEIVED_INTENT_KEY_TASK_ID, itemId);
        intent.putExtra(EditTaskActivity.RECEIVED_INTENT_KEY_DATE, item.getStartDate().getTime());
        startActivityForResult(intent, REQUEST_CODE_EDIT_TASK);
      }
    });

    return fragmentLayout;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if ((resultCode == Activity.RESULT_OK) && (requestCode == REQUEST_CODE_EDIT_TASK)) {
      // if the edited item moved out of the day, call onItemDeleted()
      // otherwise update the fragment with new data and call onItemUpdated()
      Gson gson = ((ExoCalendarApp) getActivity().getApplicationContext()).getConnector().gson;
      Long viewedDate = getArguments().getLong(ARGUMENT_BUNDLE_KEY_DATE);
      Long returnedDate = data.getLongExtra(EditTaskActivity.RETURNED_INTENT_KEY_NEW_DATE, 0L);
      if (!(returnedDate < viewedDate) && !(returnedDate > (viewedDate + 1000*60*60*24 -1))) {
        itemJson = data.getStringExtra(EditTaskActivity.RETURNED_INTENT_KEY_TASK_JSON);
        item = gson.fromJson(itemJson, Task.class);
        updateView();
        ((DetailFragmentCommunication)getActivity()).onItemUpdated(itemId, item);
      } else {
        ((DetailFragmentCommunication)getActivity()).onItemDeleted(itemId);
      }
    }
  }

  public void updateView() {
    int itemColor = ((DetailFragmentCommunication)getActivity()).getItemColor(item.getCalendarId());
    row1.setBackgroundColor(getResources().getColor(itemColor));
    textViewTitle.setText(item.getName());
    textViewDescription.setText(item.getNote());
    Date from = ISO8601.parse(item.getFrom()).getTime();
    Date to = ISO8601.parse(item.getTo()).getTime();
    textViewFrom.setText((new SimpleDateFormat("MM/dd/yyyy HH:mm")).format(from));
    textViewTo.setText((new SimpleDateFormat("MM/dd/yyyy HH:mm")).format(to));
    String[] priority_value = getResources().getStringArray(R.array.priority_value);
    String[] priority_name = getResources().getStringArray(R.array.priority_name);
    for (int i=0; i < priority_value.length; i++) {
      if (item.getPriority().equals(priority_value[i])) {
        textViewPriority.setText(priority_name[i]);
        break;
      }
    }
    String calendar_id = item.getCalendarId();
    String calendar_name = ((DetailFragmentCommunication)getActivity()).getCalendarName(calendar_id);
    textViewCalendar.setText(calendar_name);
    String[] status_value = getResources().getStringArray(R.array.task_status_value);
    String[] status_name = getResources().getStringArray(R.array.task_status_name);
    for (int i=0; i < status_value.length; i++) {
      if (item.getStatus().equals(status_value[i])) {
        textViewStatus.setText(status_name[i]);
        break;
      }
    }
    textViewCategory.setText(item.getCategoryId());
  }
}
