package org.exoplatform.calendar.client.rest;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by chautn on 8/17/15.
 */
public class ExoCalendarConnector {

  private String base_url;

  public static final Gson gson = new GsonBuilder().create();

  private static ExoCalendarRestService service;

  // Constructor
  public ExoCalendarConnector(String base_url, String username, String password) {
    String credentials = username + ":" + password;
    final String s = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    RequestInterceptor interceptor = new RequestInterceptor() {
      @Override
      public void intercept(RequestFacade request) {
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", s);
      }
    };
    RestAdapter adapter = new RestAdapter.Builder().setEndpoint(base_url)
        .setClient(new OkClient(new OkHttpClient()))
        .setConverter(new GsonConverter(gson))
        .setRequestInterceptor(interceptor)
        .build();
    service = adapter.create(ExoCalendarRestService.class);
  }

  public ExoCalendarRestService getService() {
    return service;
  }
}
