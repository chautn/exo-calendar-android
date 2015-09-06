package org.exoplatform.android.calendar.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.exoplatform.android.calendar.R;
import org.exoplatform.calendar.client.model.ComparableOccurrence;
import org.exoplatform.calendar.client.model.ExoCalendar;
import org.exoplatform.calendar.client.model.ParsableList;
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chautn on 8/28/15.
 */
public class DayViewAdapter extends RecyclerView.Adapter<DayViewAdapter.ViewHolder> {

  public Date date;
  public List<ComparableOccurrence> occurrences;
  public ParsableList<ExoCalendar> calendar_ds;
  public ExoCalendarConnector connector;
  public Context context;

  public DayViewAdapter(Context context, ParsableList<ExoCalendar> calendar_ds, ExoCalendarConnector connector, Date date, List<ComparableOccurrence> occurrences) {
    this.context = context;
    this.connector = connector;
    this.date = date;
    this.occurrences = occurrences;
    this.calendar_ds = calendar_ds;
  }

  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.day_card, parent, false);
    final ViewHolder holder = new ViewHolder(cardView);
    cardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ((DayViewActivity) context).onItemClick(holder.getPosition());
      }
    });
    return holder;
  }

  public void onBindViewHolder(ViewHolder holder, final int position) {
    ComparableOccurrence item = occurrences.get(position);
    holder.startView.setText(item.getStart24());
    //duration
    double duration = item.getEndDate().getTime() - item.getStartDate().getTime();
    double hours = duration / (1000*60*60);
    holder.durationView.setText((new DecimalFormat("0.0")).format(hours) +"h");
    if (occurrences.get(position).getTitle().length() > 50) {
      holder.titleView.setText(occurrences.get(position).getTitle().toCharArray(), 0, 50);
    } else {
      holder.titleView.setText(occurrences.get(position).getTitle());
    }
    //color
    String calendar_id = item.getCalendarId();
    for (ExoCalendar calendar : calendar_ds.data) {
      if (calendar_id.equals(calendar.getId())) {
        String calendar_colour = calendar.getColor();
        int c = context.getResources().getIdentifier(calendar_colour, "color", context.getPackageName());
        holder.titleView.setTextColor(context.getResources().getColor(c));
        break;
      }
    }

  }

  public int getItemCount() {
    return occurrences.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public CardView cardView;
    public TextView startView;
    public TextView durationView;
    public TextView titleView;

    public ViewHolder(View itemLayoutView) {
      super(itemLayoutView);
      cardView = (CardView) itemLayoutView;
      startView = (TextView) itemLayoutView.findViewById(R.id.day_card_start);
      durationView = (TextView) itemLayoutView.findViewById(R.id.day_card_duration);
      titleView = (TextView) itemLayoutView.findViewById(R.id.day_card_title);
    }
  }
}
