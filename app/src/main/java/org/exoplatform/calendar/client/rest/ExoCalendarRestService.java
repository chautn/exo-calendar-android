package org.exoplatform.calendar.client.rest;

import org.exoplatform.calendar.client.model.Event;
import org.exoplatform.calendar.client.model.ExoCalendar;
import org.exoplatform.calendar.client.model.ParsableList;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by chautn on 8/17/15.
 */
public interface ExoCalendarRestService {

  @GET("/calendars")
  void getCalendars(@Query("returnSize") boolean returnSize, @Query("offset") int offset, Callback<ParsableList<ExoCalendar>> callback);

  @GET("/calendars/{calendar_id}")
  void getCalendarById(@Path("calendar_id") String calendar_id, Callback<ExoCalendar> callback);

  @POST("/calendars")
  void createCalendar(@Body ExoCalendar calendar, Callback<Response> callback);

  @PUT("/calendars/{calendar_id}")
  void updateCalendarById(@Body ExoCalendar calendar, @Path("calendar_id") String calendar_id, Callback<Response> callback);

  @DELETE("/calendars/{calendar_id}")
  void deleteCalendarById(@Path("calendar_id") String calendar_id, Callback<Response> callback);

  @GET("/calendars/{calendar_id}/events")
  void getEventsByCalendarId(@Query("returnSize") boolean returnSize,
                             @Query("offset") int offset,
                             @Query("startTime") String start,
                             @Query("endTime") String end,
                             @Path("calendar_id") String calendar_id,
                             Callback<ParsableList<Event>> callback);


}