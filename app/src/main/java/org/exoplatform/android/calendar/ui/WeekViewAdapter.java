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
import org.exoplatform.calendar.client.model.ComparableOccurrence;
import org.exoplatform.calendar.client.model.Event;
import org.exoplatform.calendar.client.model.ExoCalendar;
import org.exoplatform.calendar.client.model.ParsableList;
import org.exoplatform.calendar.client.rest.ExoCalendarConnector;
import org.exoplatform.commons.utils.ISO8601;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chautn on 8/20/15.
 */
public class WeekViewAdapter extends RecyclerView.Adapter<WeekViewAdapter.ViewHolder> {

  public static final int top_n = 3; //max num of occurrences displayed a day
  public Context context;
  public ExoCalendarConnector connector;
  public List<List<ComparableOccurrence>> occurrences;
  public List<Date> week;

  public WeekViewAdapter(Context context, ExoCalendarConnector connector, List<Date> week, List<List<ComparableOccurrence>> occurrences) {
    this.context = context;
    this.connector = connector;
    this.week = week;
    this.occurrences = occurrences;
  }

  public WeekViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    CardView itemLayoutView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.week_day_card, parent, false);
    itemLayoutView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.getHeight() / 7));
    ViewHolder holder = new ViewHolder(itemLayoutView);
    return holder;
  }

  public void onBindViewHolder(final ViewHolder holder, final int position) {
    Date date = week.get(position);
    List<ComparableOccurrence> list = occurrences.get(position);

    holder.day_of_week.setText((new SimpleDateFormat("EEE").format(date)));
    holder.day_of_month.setText((new SimpleDateFormat("dd").format(date)));

    //dynamically add occurrence item layout
    if ((list != null) && (list.size() > 0)) {
      int length = list.size();
      for (int i = 0; ((i < 3) && (i < length)); i++) {
        LinearLayout occurrence_layout = (LinearLayout) LayoutInflater.from(this.context).inflate(R.layout.w_occurrence_layout, null);
        TextView occurrence_start = (TextView) occurrence_layout.findViewById(R.id.occurrence_start);
        occurrence_start.setText(list.get(i).getStartAMPM());
        TextView occurrence_title = (TextView) occurrence_layout.findViewById(R.id.occurrence_title);
        occurrence_title.setText(list.get(i).getTitle());
        holder.wlayout_4.addView(occurrence_layout);
      }

      //view number of occurrences
      if (3 < length) {
        holder.occurrence_number_view.setText("+" + Integer.toString(length - 3));
      }
    }
  }

  public int getItemCount() {
    return 7;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public CardView week_day_card;
    public LinearLayout wlayout_1;
    public LinearLayout wlayout_2;
    public LinearLayout wlayout_3;
    public LinearLayout wlayout_4;
    public TextView day_of_week;
    public TextView day_of_month;
    public TextView occurrence_number_view;

    public ViewHolder(View itemLayoutView) {
      super(itemLayoutView);
      week_day_card = (CardView) itemLayoutView.findViewById(R.id.week_day_card);
      wlayout_1 = (LinearLayout) itemLayoutView.findViewById(R.id.wlayout_1);
      wlayout_2 = (LinearLayout) itemLayoutView.findViewById(R.id.wlayout_2);
      wlayout_3 = (LinearLayout) itemLayoutView.findViewById(R.id.wlayout_3);
      wlayout_4 = (LinearLayout) itemLayoutView.findViewById(R.id.wlayout_4);
      day_of_week = (TextView) itemLayoutView.findViewById(R.id.day_of_week);
      day_of_month = (TextView) itemLayoutView.findViewById(R.id.day_of_month);
      occurrence_number_view = (TextView) itemLayoutView.findViewById(R.id.occurrence_number_view);
    }
  }
}
