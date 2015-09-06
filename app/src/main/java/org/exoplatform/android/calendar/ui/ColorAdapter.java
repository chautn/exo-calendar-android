package org.exoplatform.android.calendar.ui;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import org.exoplatform.android.calendar.R;

import java.util.ArrayList;

/**
 * Created by chautn on 9/5/15.
 */
public class ColorAdapter extends BaseAdapter {

  public static String[] colors = { "asparagus", "munsell_blue", "navy_blue", "purple", "red", "brown",
                                    "laurel_green", "sky_blue", "blue_gray", "light_purple", "hot_pink", "light_brown",
                                    "moss_green", "powder_blue", "light_blue", "pink", "orange", "gray",
                                    "green", "baby_blue", "light_gray", "beige", "yellow", "plum"
                                  };
  public Context context;

  public int selected;

  public ColorAdapter(Context context) {
    this.context = context;
    selected = 0;
  }

  public int getCount() {
    return colors.length;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    TextView textView;
    if (convertView == null) {
      textView = new TextView(context);
      textView.setLayoutParams(new GridView.LayoutParams(60, 60));
    } else {
      textView = (TextView) convertView;
    }
    String color_name = colors[position];
    int color = context.getResources().getIdentifier(color_name, "color", context.getPackageName());
    textView.setBackgroundColor(context.getResources().getColor(color));
    textView.setGravity(Gravity.CENTER);
    textView.setTextColor(context.getResources().getColor(R.color.white));
    if (position == selected) {
      textView.setText("V");
    } else {
      textView.setText("");
    }
    return textView;
  }

  public long getItemId(int position) {
    return position;
  }
  public Object getItem(int position) {
    return null;
  }
  public void setSelected(int selected) {
    if ((-1 < selected) && (selected < colors.length)) {
      this.selected = selected;
    }
  }
  public int getSelected() {
    return selected;
  }
  public void setSelected(String color_name) {
    int length = colors.length;
    for (int i=0; i < length; i++) {
      if (color_name.equals(colors[i])) {
        this.selected = i;
      }
    }
  }
  public String getSelectedColor() {
    return colors[selected];
  }
}
