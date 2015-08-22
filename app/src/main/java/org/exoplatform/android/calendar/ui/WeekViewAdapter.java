package org.exoplatform.android.calendar.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.exoplatform.android.calendar.R;
import org.exoplatform.calendar.client.model.Event;
import org.exoplatform.calendar.client.model.ExoCalendar;
import org.exoplatform.calendar.client.model.ParsableList;
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chautn on 8/20/15.
 */
public class WeekViewAdapter extends RecyclerView.Adapter<WeekViewAdapter.ViewHolder> {

  public static final int top_n = 3; //max num of events/tasks displayed a day
  private Context context;
  private ExoCalendarConnector connector;
  //private ParsableList<ExoCalendar> calendar_ds;
  //private ParsableList<Event> event_ds;
  public Calendar[] week;

  public WeekViewAdapter(Context context, ExoCalendarConnector connector, Calendar[] week) {
    this.context = context;
    this.connector = connector;
    this.week = week;
  }

  public WeekViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    CardView itemLayoutView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.week_day_card, parent, false);
    itemLayoutView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.getHeight()/7));
    ViewHolder holder = new ViewHolder(itemLayoutView);
    return holder;
  }

  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.event_name_view.setText("aaa");
    holder.day_of_week.setText(week[position].getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
    DateFormat dateFormat = new SimpleDateFormat("dd");
    holder.day_of_month.setText(dateFormat.format(week[position].getTime()));
  }

  public int getItemCount() {
    return 7;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public CardView week_day_card;
    public LinearLayout wlayout_1;
    public LinearLayout wlayout_2;
    public LinearLayout wlayout_3;
    public TextView day_of_week;
    public TextView day_of_month;
    public TextView event_name_view;

    public ViewHolder(View itemLayoutView) {
      super(itemLayoutView);
      week_day_card = (CardView) itemLayoutView.findViewById(R.id.week_day_card);
      wlayout_1 = (LinearLayout) itemLayoutView.findViewById(R.id.wlayout_1);
      wlayout_2 = (LinearLayout) itemLayoutView.findViewById(R.id.wlayout_2);
      wlayout_3 = (LinearLayout) itemLayoutView.findViewById(R.id.wlayout_3);
      day_of_week = (TextView) itemLayoutView.findViewById(R.id.day_of_week);
      day_of_month = (TextView) itemLayoutView.findViewById(R.id.day_of_month);
      event_name_view = (TextView) itemLayoutView.findViewById(R.id.event_name_view);
    }
  }
}
