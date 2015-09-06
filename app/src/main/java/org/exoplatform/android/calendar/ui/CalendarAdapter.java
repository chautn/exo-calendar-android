package org.exoplatform.android.calendar.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.exoplatform.android.calendar.R;
import org.exoplatform.calendar.client.model.ExoCalendar;
import org.exoplatform.calendar.client.model.ParsableList;
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chautn on 8/17/15.
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

  private Context context;
  private ExoCalendarConnector connector;
  private ParsableList<ExoCalendar> calendar_ds;

  public CalendarAdapter(Context context, ExoCalendarConnector connector, ParsableList<ExoCalendar> calendar_ds) {
    this.context = context;
    this.connector = connector;
    this.calendar_ds = calendar_ds;
  }

  public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    CardView itemLayoutView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_card, parent, false);
    ViewHolder holder = new ViewHolder(itemLayoutView, new ViewHolder.CalendarClickListener() {
      @Override
      public void editCalendar(View caller, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(caller.getContext());
        builder.setTitle("Edit calendar");
        LayoutInflater inflater = (LayoutInflater) caller.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.calendar_dialog, null);
        final TextView calendar_name_view = (TextView) layout.findViewById(R.id.calendar_dialog_name);
        final TextView calendar_description_view = (TextView) layout.findViewById(R.id.calendar_dialog_description);
        final ExoCalendar calendar_copy = CalendarAdapter.this.calendar_ds.copyValueAt(position);
        final GridView gridView = (GridView) layout.findViewById(R.id.calendar_dialog_color);
        final ColorAdapter colorAdapter = new ColorAdapter(context);
        colorAdapter.setSelected(calendar_copy.getColor());
        gridView.setAdapter(colorAdapter);
        gridView.setNumColumns(6);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            colorAdapter.setSelected(position);
            colorAdapter.notifyDataSetChanged();
          }
        });
        calendar_name_view.setText(calendar_copy.getName());
        calendar_description_view.setText(calendar_copy.getDescription());
        builder.setView(layout);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Save", new AlertDialog.OnClickListener() {
          public void onClick(DialogInterface dialogInterface, int which) {
            calendar_copy.setName(calendar_name_view.getText().toString());
            calendar_copy.setDescription(calendar_description_view.getText().toString());
            calendar_copy.setColor(colorAdapter.getSelectedColor());
            Callback<Response> callback = new Callback<Response>() {
              @Override
              public void success(Response response, Response response2) {
                CalendarAdapter.this.calendar_ds.update(calendar_copy);
                CalendarAdapter.this.notifyItemChanged(position);
              }

              @Override
              public void failure(RetrofitError error) {
                //
              }
            };
            CalendarAdapter.this.connector.getService().updateCalendarById(calendar_copy, calendar_copy.getId(), callback);
          }
        });
        builder.show();
      }

      @Override
      public void deleteCalendar(View caller, final int position) {
        final String id = CalendarAdapter.this.calendar_ds.data[position].getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(caller.getContext());
        builder.setMessage("Delete calendar " + CalendarAdapter.this.calendar_ds.data[position].getName() + "?");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Delete", new AlertDialog.OnClickListener() {
          public void onClick(DialogInterface dialogInterface, int which) {
          Callback<retrofit.client.Response> callback = new Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
              CalendarAdapter.this.calendar_ds.remove(id);
              CalendarAdapter.this.notifyItemRemoved(position);
            }

            @Override
            public void failure(RetrofitError error) {
              //
            }
          };
          CalendarAdapter.this.connector.getService().deleteCalendarById(id, callback);
          }
        });
        builder.show();
      }
    });
    return holder;
  }

  public void onBindViewHolder(ViewHolder holder, final int position) {
    ExoCalendar item = calendar_ds.data[position];
    holder.calendar_name_view.setText(item.getName());
    int color = context.getResources().getIdentifier(item.getColor(), "color", context.getPackageName());
    holder.calendar_card.setCardBackgroundColor(context.getResources().getColor(color));
  }

  public int getItemCount() {
    if (calendar_ds.data == null) {
      return 0;
    } else {
      return calendar_ds.data.length;
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public CardView calendar_card;
    public TextView calendar_name_view;
    public Button delete_calendar_btn;
    public CalendarClickListener listener;

    public ViewHolder(View itemLayoutView, CalendarClickListener listener) {
      super(itemLayoutView);
      this.listener = listener;
      calendar_card = (CardView) itemLayoutView.findViewById(R.id.calendar_card);
      calendar_name_view = (TextView) itemLayoutView.findViewById(R.id.calendar_name_view);
      delete_calendar_btn = (Button) itemLayoutView.findViewById(R.id.delete_calendar_btn);
      calendar_name_view.setOnClickListener(this);
      delete_calendar_btn.setOnClickListener(this);
    }

    // call editCalendar if user clicks on text views, call deleteCalendar on delete button.
    public void onClick(View v) {
      if (v.getId() == delete_calendar_btn.getId()) {
        listener.deleteCalendar(v, getPosition());
      } else {
        listener.editCalendar(v, getPosition());
      }
    }

    public static interface CalendarClickListener {
      public void editCalendar(View v, int position);

      public void deleteCalendar(View v, int position);
    }
  }
}
