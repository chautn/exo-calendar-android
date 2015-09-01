package org.exoplatform.calendar.client.rest;

import org.exoplatform.calendar.client.model.Event;
import org.exoplatform.calendar.client.model.ExoCalendar;
import org.exoplatform.calendar.client.model.ParsableList;
import org.exoplatform.calendar.client.model.Task;

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

  // Calendar GET, GET, POST, PUT, DELETE
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

  // Event GET, POST, PUT, DELETE
  @GET("/calendars/{calendar_id}/events")
  void getEventsByCalendarId(@Query("returnSize") boolean returnSize,
                             @Query("offset") int offset,
                             @Query("startTime") String start,
                             @Query("endTime") String end,
                             @Path("calendar_id") String calendar_id,
                             Callback<ParsableList<Event>> callback);

  @POST("/events")
  void createEvent(@Body Event event, Callback<Response> callback);

  @PUT("/events/{event_id}")
  void updateEventById(@Body Event event, @Path("event_id") String id, Callback<Response> callback);

  @DELETE("/events/{event_id}")
  void deleteEventById(@Path("event_id") String id, Callback<Response> callback);

  // Task GET, PUT, DELETE
  @GET("/calendars/{calendar_id}/tasks")
  void getTasksByCalendarId(@Query("returnSize") boolean returnSize,
                             @Query("offset") int offset,
                             @Query("startTime") String start,
                             @Query("endTime") String end,
                             @Path("calendar_id") String calendar_id,
                             Callback<ParsableList<Task>> callback);

  @PUT("/tasks/{task_id}")
  void updateTaskById(@Body Task task, @Path("task_id") String id, Callback<Response> callback);

  @DELETE("/tasks/{task_id}")
  void deleteTaskById(@Path("task_id") String id, Callback<Response> callback);
}