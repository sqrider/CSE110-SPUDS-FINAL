package com.spuds.eventapp.Firebase;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.spuds.eventapp.CreateEvent.CreateEventForm;
import com.spuds.eventapp.CreateEvent.CreateEventRVAdapter;
import com.spuds.eventapp.Shared.Event;
import com.spuds.eventapp.Shared.EventsFeedRVAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tina on 5/2/16.
 */
public class EventsFirebase {

    public static final String tabNew = "New";
    public static final String tabHot = "Hot";
    public static final String tabNow = "Now";
    public static final String tabGoing = "Going";
    public static final String tabHosting = "Hosting";

    public static final String catAcademic = "Academic Category";
    public static final String catSports = "Sports Category";
    public static final String catSocial = "Social Category";
    public static final String catFree = "Free Category";
    public static final String catFood = "Food Category";
    public static final String catConcerts = "Concerts Category";
    public static final String catCampus = "Campus Category";


    ArrayList<Event> eventsList;
    String tabFilter;
    String catFilter;
    int loading;

    EventsFeedRVAdapter adapter;
    public EventsFirebase() {

    }

    public EventsFirebase(ArrayList<Event> eventsList, int loading, String tabFilter) {
        this.eventsList = eventsList;
        this.tabFilter = tabFilter;
        this.loading = loading;
        this.adapter = adapter;
    }

    public EventsFirebase(ArrayList<Event> eventsList, int loading, String tabFilter, String catFilter, EventsFeedRVAdapter adapter) {
        this.eventsList = eventsList;
        this.tabFilter = tabFilter;
        this.catFilter = catFilter;
        this.loading = loading;
    }

    ArrayList<String> categoryList;
    public void createEvent(CreateEventForm form, CreateEventRVAdapter adapter) {
        categoryList = adapter.getList();
        Log.d("fuck", String.valueOf(categoryList));

        final Firebase ref = new Firebase("https://eventory.firebaseio.com");

        SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd | HH:mm");
        Date dateobj = new Date();

        String originalString = form.getDate();
        char[] c = originalString.toCharArray();

        char temp = c[0];
        c[0] = c[6]; 
        c[6] = temp;

        char temp1 = c[1];
        c[1] = c[7];
        c[7] = temp1;

        char temp2 = c[3];
        c[3] = c[6];
        c[6] = temp2;

        char temp3 = c[4];
        c[4] = c[7];
        c[7] = temp3;
        String swappedString = new String(c);

        //change to 24 time

        String tempString = swappedString.substring(11, swappedString.length());
        int numb = 0;

        if(tempString.indexOf('A') == -1) {
            String sub = tempString.substring(0, tempString.indexOf('P'));
            numb = Integer.parseInt(sub);

            if(numb != 12) {
                numb += 12;
            }

        }
        else{
            String sub = tempString.substring(0, tempString.indexOf('A'));
            numb = Integer.parseInt(sub);

            if(numb == 12) {
                numb = 0;
            }
        }

        if(swappedString.indexOf(':') == -1) {
            swappedString = swappedString.substring(0, 11) + numb + ":00";
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", UserFirebase.uId);
        map.put("event_name", form.getName());
        map.put("description", form.getDescription());
        map.put("location", form.getLocation());
        map.put("date", swappedString);
        map.put("number_going", "1");
        map.put("picture", form.getPicture());
        map.put("created_at", df.format(dateobj) );

        for(int i=0; i < categoryList.size(); i++) {
            if (categoryList.get(i) == "Academic") {
                map.put("catAcademic", "true");
            }
            if (categoryList.get(i) == "Campus") {
                map.put("catCampus", "true");
            }
            if (categoryList.get(i) == "Concerts") {
                map.put("catConcerts", "true");
            }
            if (categoryList.get(i) == "Food") {
                map.put("catFood", "true");
            }
            if (categoryList.get(i) == "Free") {
                map.put("catFree", "true");
            }
            if (categoryList.get(i) == "Social") {
                map.put("catSocial", "true");
            }
            if (categoryList.get(i) == "Sports") {
                map.put("catSports", "true");
            }
        }

        ref.child("events").push().setValue(map);
    }

    public void editEvent() {

    }

    Event item;

    public Event createEL() {
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com/events");
        Query queryRef = myFirebaseRef.orderByKey();

        switch (tabFilter) {
            case tabNew:
                queryRef = myFirebaseRef.orderByChild("created_at");
                break;
            case tabHot:
                queryRef = myFirebaseRef.orderByChild("number_going");
                break;
            case tabNow:
                queryRef = myFirebaseRef.orderByChild("date");
                break;
            case tabGoing:
                break;
            case tabHosting:
                break;
        }

        if(catFilter != null) {
            switch (catFilter) {
                case catAcademic:
                    queryRef = myFirebaseRef.orderByChild("catAcademic").equalTo("true");
                    break;
                case catCampus:
                    queryRef = myFirebaseRef.orderByChild("catCampus").equalTo("true");
                    break;
                case catConcerts:
                    queryRef = myFirebaseRef.orderByChild("catConcerts").equalTo("true");
                    break;
                case catFood:
                    queryRef = myFirebaseRef.orderByChild("catFood").equalTo("true");
                    break;
                case catFree:
                    queryRef = myFirebaseRef.orderByChild("catFree").equalTo("true");
                    break;
                case catSocial:
                    queryRef = myFirebaseRef.orderByChild("catSocial").equalTo("true");
                    break;
                case catSports:
                    queryRef = myFirebaseRef.orderByChild("catSports").equalTo("true");
                    break;
            }
        }

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                Event newEvent = new Event();

                for (DataSnapshot child : snapshot.getChildren()) {
                    switch (child.getKey()) {
                        case "date":
                            newEvent.setDate(String.valueOf(child.getValue()));
                            break;
                        case "description":
                            newEvent.setDescription(String.valueOf(child.getValue()));
                            break;
                        case "event_name":
                            newEvent.setEventName(String.valueOf(child.getValue()));
                            break;
                        case "location":
                            newEvent.setLocation(String.valueOf(child.getValue()));
                            break;
                        case "number_going":
                            newEvent.setAttendees(Integer.parseInt((String) child.getValue()));
                            break;
                        case "picture_file_name":
                            newEvent.setPicFileName(String.valueOf(child.getValue()));
                            break;
                        case "host_id":
                            newEvent.setHostId(String.valueOf(child.getValue()));
                            break;
                    }
                    newEvent.setCategories(categoryList);

                    //Log.d("asdf", String.valueOf(snapshot.getKey()));

                }

                //newEvent.setCategories(categoryList);

                if(tabFilter.equals(tabHot) || tabFilter.equals(tabNew)) {
                    eventsList.add(0, newEvent);
                }
                else {
                    eventsList.add(newEvent);
                }

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return item;
    }

    public Event getEventDetails(final String eventID) {
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com/events");
        Query queryRef = myFirebaseRef.orderByKey();
        queryRef.addChildEventListener(new ChildEventListener() {
            Event newEvent = new Event();

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getValue() == eventID) {
                        newEvent.setDate(String.valueOf(child.getValue()));
                        newEvent.setDescription(String.valueOf(child.getValue()));
                        newEvent.setEventName(String.valueOf(child.getValue()));
                        newEvent.setLocation(String.valueOf(child.getValue()));
                        newEvent.setAttendees(Integer.parseInt((String) child.getValue()));
                        newEvent.setPicFileName(String.valueOf(child.getValue()));
                        newEvent.setHostId(String.valueOf(child.getValue()));
                        newEvent.setCategories(categoryList);
                        item = newEvent;
                        break;
                    }

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
        return item;
    }


}
