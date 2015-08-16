package org.exoplatform.calendar.client.model;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by chautn on 8/17/15.
 */
public class ParsableList<T> {
  public int limit;
  public int size;
  public int offset;
  public T[] data;


  public int getLimit() {
    return limit;
  }
  public void setLimit(int limit) {
    this.limit = limit;
  }
  public int getSize() {
    return size;
  }
  public void setSize(int size) {
    this.size = size;
  }
  public int getOffset() {
    return offset;
  }
  public void setOffset(int offset) {
    this.offset = offset;
  }
  public T[] getData() {
    return data;
  }
  public void setData(T[] data) {
    this.data = data;
  }

  public void reset() {
    this.size = 0;
    this.data = null;
  }

  public void add(ParsableList<T> another) {
    if ((another == null) || (another.data == null)) {
      return;
    }
    if (data ==  null) {
      data = Arrays.copyOf(another.data, another.data.length);
      return;
    }
    T[] arr = Arrays.copyOf(data, data.length + another.data.length);
    System.arraycopy(another.data, 0, arr, data.length, another.data.length);
    data = arr;
  }

  public T copyValueAt(int position) {
    if ((data == null) || (position < 0)){
      return null;
    }
    if (data.length < position + 1) {
      return null;
    }
    T[] arr = Arrays.copyOf(data, 1);
    System.arraycopy(data, position, arr, 0, 1);
    return arr[0];
  }

  private void update(T new_element, int position) {
    if ((data == null) || (new_element == null) || (position < 0)) {
      return;
    }
    if (data.length < position +1) {
      return;
    }
    data[position] = new_element;
  }

  public void update(T new_element) {
    if ((data == null) || (new_element == null)) {
      return;
    }
    if (new_element instanceof Identifiable) {
      int length = data.length;
      for (int i=0; i < length; i++) {
        if ((data[i] instanceof Identifiable) && (((Identifiable)data[i]).getId().equals(((Identifiable) new_element).getId()))) {
          data[i] = new_element;
          return;
        }
      }
    }
  }

  private void remove(int position) {
    if (data == null) {
      return;
    }
    if ((data.length == 0) || (data.length < (position +1))) {
      return;
    }
    if (data.length == (position +1)) {
      T[] arr = Arrays.copyOf(data, data.length -1);
      data = arr;
      return;
    }
    T[] arr = Arrays.copyOf(data, data.length -1);
    System.arraycopy(data, 0, arr, 0, position);
    System.arraycopy(data, position +1, arr, position, data.length - position -1);
    data = arr;
  }

  public void remove(String id) {
    if (data == null) {
      return;
    }
    if (data.length == 0) {
      return;
    }
    int length = data.length;
    for (int i=0; i < length; i++) {
      if ((data[i] instanceof Identifiable) && (((Identifiable) data[i]).getId().equals(id))) {
        remove(i);
        return;
      }
    }
  }
}
