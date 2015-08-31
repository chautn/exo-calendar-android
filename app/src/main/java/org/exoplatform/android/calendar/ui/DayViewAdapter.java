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
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;

import java.util.Date;
import java.util.List;

/**
 * Created by chautn on 8/28/15.
 */
public class DayViewAdapter extends RecyclerView.Adapter<DayViewAdapter.ViewHolder> {

  public Date date;
  public List<ComparableOccurrence> occurrences;
  public ExoCalendarConnector connector;
  public Context context;

  public DayViewAdapter(Context context, ExoCalendarConnector connector, Date date, List<ComparableOccurrence> occurrences) {
    this.context = context;
    this.connector = connector;
    this.date = date;
    this.occurrences = occurrences;
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
    holder.startView.setText(occurrences.get(position).getStart24());
    holder.endView.setText(occurrences.get(position).getEnd24());
    if (occurrences.get(position).getTitle().length() > 50) {
      holder.titleView.setText(occurrences.get(position).getTitle().toCharArray(), 0, 50);
    } else {
      holder.titleView.setText(occurrences.get(position).getTitle());
    }
  }

  public int getItemCount() {
    return occurrences.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public CardView cardView;
    public TextView startView;
    public TextView endView;
    public TextView titleView;

    public ViewHolder(View itemLayoutView) {
      super(itemLayoutView);
      cardView = (CardView) itemLayoutView;
      startView = (TextView) itemLayoutView.findViewById(R.id.day_card_start);
      endView = (TextView) itemLayoutView.findViewById(R.id.day_card_end);
      titleView = (TextView) itemLayoutView.findViewById(R.id.day_card_title);
    }
  }
}
