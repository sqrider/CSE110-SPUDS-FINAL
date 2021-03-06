package com.spuds.eventapp.Firebase;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.spuds.eventapp.CreateEvent.CreateEventCategoryRVAdapter;
import com.spuds.eventapp.CreateEvent.CreateEventForm;
import com.spuds.eventapp.EditEvent.EditEventForm;
import com.spuds.eventapp.EditEvent.EditEventCategoryRVAdapter;
import com.spuds.eventapp.Shared.Event;
import com.spuds.eventapp.Shared.EventsFeedRVAdapter;
import com.spuds.eventapp.Shared.SubEvent;
import com.spuds.eventapp.Shared.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tina on 5/2/16.
 */

/*---------------------------------------------------------------------------
   Class Name: EventsFirebase
   Description: Contains all methods that interact with the database, and
                that have to do with events functionality.
---------------------------------------------------------------------------*/
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
    public static final String catCampus = "Student Orgs Category";

    ArrayList<Event> eventsList;
    ArrayList<String> a = new ArrayList<>();
    String tabFilter;
    String catFilter;
    String id;
    public int idIsGoing = 0;
    int loading;
    public static Event eventDetailsEvent;

    public EventsFirebase() {
        //required empty constructor
    }

    //constructor adding variables other than categories filter
    public EventsFirebase(ArrayList<Event> eventsList, int loading, String tabFilter) {
        this.eventsList = eventsList;
        this.tabFilter = tabFilter;
        this.loading = loading;
    }

    //constructor adding variables including categories filter
    public EventsFirebase(ArrayList<Event> eventsList, int loading, String tabFilter, String catFilter, EventsFeedRVAdapter adapter) {
        this.eventsList = eventsList;
        this.tabFilter = tabFilter;
        this.catFilter = catFilter;
        this.loading = loading;
    }

    ArrayList<String> categoryList;

    /*---------------------------------------------------------------------------
     Function Name: createEvent
     Description: creates an event and pushes it to the firebase
     Input: CreateEventForm form - form containing all fields of an event
            CreateEventCategoryRVAdapter adapter - RVAdapter for selecting categories
     Output: Key of the ref of event pushed
    ---------------------------------------------------------------------------*/
    public String createEvent(CreateEventForm form, CreateEventCategoryRVAdapter adapter) {

        categoryList = adapter.getList();

        final Firebase ref = new Firebase("https://eventory.firebaseio.com");
        final Firebase ref2 = new Firebase("https://eventory.firebaseio.com");

        SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd | HH:mm");
        Date dateobj = new Date();

        //original date
        String originalString = form.getDate();

        //used to parse the string
        char[] c = originalString.toCharArray();

        //switch dd and yy
        char temp = c[0];
        c[0] = c[6];
        c[6] = temp;
        char temp1 = c[1];
        c[1] = c[7];
        c[7] = temp1;
        //now we at yy/dd/mm

        //switch mm, dd
        char temp2 = c[3];
        c[3] = c[6];
        c[6] = temp2;
        char temp3 = c[4];
        c[4] = c[7];
        c[7] = temp3;
        String swappedString = new String(c);
        // is now yy/mm/dd - this is the date format pushed to database

        //change to 24 time
        String tempString = swappedString.substring(11, swappedString.length());
        int numb = 0;
        String sub = "";

        if(tempString.indexOf('A') == -1) {
            if(swappedString.indexOf(':') != -1) {
                sub = tempString.substring(0, tempString.indexOf(':'));
            }
            else{
                sub = tempString.substring(0, tempString.indexOf('P'));
            }

            numb = Integer.parseInt(sub);

            if(numb != 12) {
                numb += 12;
            }

        }
        else{
            if(swappedString.indexOf(':') != -1) {
                sub = tempString.substring(0, tempString.indexOf(':'));
            }
            else{
                sub = tempString.substring(0, tempString.indexOf('A'));
            }

            numb = Integer.parseInt(sub);

            if(numb == 12) {
                numb = 0;
            }
        }

        if(String.valueOf(numb).length() == 1) {
            if (swappedString.indexOf(':') == -1) {
                swappedString = swappedString.substring(0, 11) + "0" + numb + ":00";
            }
            else {
                swappedString = swappedString.substring(0, 11) + "0" +numb + tempString.substring(tempString.indexOf(':'), tempString.length() - 2);
            }
        }
        else {
            if (swappedString.indexOf(':') == -1) {
                swappedString = swappedString.substring(0, 11) + numb + ":00";
            }
            else {
                swappedString = swappedString.substring(0, 11) + numb + tempString.substring(tempString.indexOf(':'), tempString.length() - 2);
            }
        }

        //map to push to database
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("host_id", UserFirebase.uId);
        map.put("host_name", UserFirebase.thisUser.getName());
        map.put("event_name", form.getName());
        map.put("description", form.getDescription());
        map.put("location", form.getLocation());
        map.put("date", swappedString);
        map.put("number_going", 1);
        map.put("picture", form.getPicture());
        map.put("created_at", df.format(dateobj) );

        //look through the categoryList
        for(int i=0; i < categoryList.size(); i++) {
            if (categoryList.get(i) == "Academic") {
                map.put("catAcademic", "true");
            }
            if (categoryList.get(i) == "Student Orgs") {
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

        //push to database
        Firebase pushRef = ref.child("events").push();
        pushRef.setValue(map);

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("user_id", UserFirebase.uId);
        map2.put("event_id", pushRef.getKey());
        ref2.child("events_registrations").push().setValue(map2);

        UserFirebase userFirebase = new UserFirebase();
        userFirebase.updateNumberHosting();

        return pushRef.getKey();
    }

    Event item;

    /*---------------------------------------------------------------------------
     Function Name: createEL
     Description: Creates the list of events based on the database
     Input: None
     Output: Event
    ---------------------------------------------------------------------------*/
    public Event createEL() {
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com");
        final Query[] queries = new Query[1];
        Query queryRef = myFirebaseRef.orderByKey();
        Query queryRef2 = myFirebaseRef.orderByKey();
        queries[0] = queryRef;

        //which tab are we sorting by
        if (tabFilter != null) {
            switch (tabFilter) {
                case tabNew:
                    queryRef = myFirebaseRef.child("events").orderByChild("created_at");
                    break;
                case tabHot:
                    queryRef = myFirebaseRef.child("events").orderByChild("number_going");
                    break;
                case tabNow:
                    queryRef = myFirebaseRef.child("events").orderByChild("date");
                    break;
                case tabGoing:
                    queryRef2 = myFirebaseRef.child("events_registrations").orderByChild("user_id").equalTo(UserFirebase.uId);
                    break;
                case tabHosting:
                    queryRef = myFirebaseRef.child("events").orderByChild("host_id").equalTo(UserFirebase.uId);
                    break;
            }
        }

        //what categories are there
        if(catFilter != null) {
            switch (catFilter) {
                case catAcademic:
                    queryRef = myFirebaseRef.child("events").orderByChild("catAcademic").equalTo("true");
                    break;
                case catCampus:
                    queryRef = myFirebaseRef.child("events").orderByChild("catCampus").equalTo("true");
                    break;
                case catConcerts:
                    queryRef = myFirebaseRef.child("events").orderByChild("catConcerts").equalTo("true");
                    break;
                case catFood:
                    queryRef = myFirebaseRef.child("events").orderByChild("catFood").equalTo("true");
                    break;
                case catFree:
                    queryRef = myFirebaseRef.child("events").orderByChild("catFree").equalTo("true");
                    break;
                case catSocial:
                    queryRef = myFirebaseRef.child("events").orderByChild("catSocial").equalTo("true");
                    break;
                case catSports:
                    queryRef = myFirebaseRef.child("events").orderByChild("catSports").equalTo("true");
                    break;
            }
        }

        //tab is going
        if(tabFilter != null && tabFilter.equals(tabGoing)){
            queryRef2.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        if(child.getKey().equals("event_id")) {
                            queries[0] = myFirebaseRef.child("events").orderByKey().equalTo(String.valueOf(child.getValue()));
                            queries[0].addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                                    Event newEvent  = new Event();
                                    //goes through each field and sets the data
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
                                                newEvent.setAttendees(Integer.parseInt(String.valueOf(child.getValue())));
                                                break;
                                            case "picture":
                                                newEvent.setPicture(String.valueOf(child.getValue()));
                                                break;
                                            case "host_id":
                                                newEvent.setHostId(String.valueOf(child.getValue()));
                                                break;
                                            case "host_name":
                                                newEvent.setHostName(String.valueOf(child.getValue()));
                                                break;
                                            case "catAcademic":
                                                a.add("Academic");
                                                break;
                                            case "catCampus":
                                                a.add("Student Orgs");
                                                break;
                                            case "catConcerts":
                                                a.add("Concerts");
                                                break;
                                            case "catFood":
                                                a.add("Food");
                                                break;
                                            case "catFree":
                                                a.add("Free");
                                                break;
                                            case "catSocial":
                                                a.add("Social");
                                                break;
                                            case "catSports":
                                                a.add("Sports");
                                                break;
                                        }

                                    }
                                    newEvent.setEventId(snapshot.getKey());
                                    newEvent.setCategories(a);
                                    //clear the array list for next entry
                                    a = new ArrayList<String>();

                                    // used to get the current time
                                    String currentDate;
                                    SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd | HH:mm");
                                    Date dateobj = new Date();
                                    currentDate = df.format(dateobj);


                                    if(tabFilter != null && (tabFilter.equals(tabHot) || tabFilter.equals(tabNew))) {
                                        if (currentDate.compareTo(newEvent.getDate()) < 0){
                                            eventsList.add(0, newEvent);
                                        }
                                    }
                                    else {
                                        //current date and time is "earlier" than the event. Aka the event has not happened yet.
                                        if (currentDate.compareTo(newEvent.getDate()) < 0){
                                            eventsList.add(newEvent);
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
        }
        else {
            queryRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                    Event newEvent = new Event();

                    for (DataSnapshot child : snapshot.getChildren()) {
                        switch (child.getKey()) {   //parse through each datafield
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
                                newEvent.setAttendees(Integer.parseInt(String.valueOf(child.getValue())));
                                break;
                            case "picture":
                                newEvent.setPicture(String.valueOf(child.getValue()));
                                break;
                            case "host_id":
                                newEvent.setHostId(String.valueOf(child.getValue()));
                                break;
                            case "host_name":
                                newEvent.setHostName(String.valueOf(child.getValue()));
                                break;
                            case "catAcademic":
                                a.add("Academic");
                                break;
                            case "catCampus":
                                a.add("Student Orgs");
                                break;
                            case "catConcerts":
                                a.add("Concerts");
                                break;
                            case "catFood":
                                a.add("Food");
                                break;
                            case "catFree":
                                a.add("Free");
                                break;
                            case "catSocial":
                                a.add("Social");
                                break;
                            case "catSports":
                                a.add("Sports");
                                break;
                        }
                    }
                    newEvent.setEventId(snapshot.getKey());
                    newEvent.setCategories(a);
                    a = new ArrayList<String>();    //clear list for next entry

                    // used to get the current time
                    String currentDate;
                    SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd | HH:mm");
                    Date dateobj = new Date();
                    currentDate = df.format(dateobj);

                    if (tabFilter != null && (tabFilter.equals(tabHot) || tabFilter.equals(tabNew))) {
                        if (currentDate.compareTo(newEvent.getDate()) < 0) {
                            eventsList.add(0, newEvent);
                        }
                    } else {
                        //current date and time is "earlier" than the event. Aka the event has not happened yet.
                        if (currentDate.compareTo(newEvent.getDate()) < 0) {
                            eventsList.add(newEvent);
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
        }
        return item;
    }

    public static ArrayList<SubEvent> myEventsSubEventList;

    /*---------------------------------------------------------------------------
     Function Name: getMyEventsList
     Description: gets the list for MyEvents
     Input: None
     Output: None
    ---------------------------------------------------------------------------*/
    public void getMyEventsList() {
        myEventsSubEventList = new ArrayList<>();
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com");
        final Query[] queries = new Query[1];
        Query queryRef = myFirebaseRef.orderByKey();
        Query queryRef2 = myFirebaseRef.orderByKey();
        queries[0] = queryRef;

        queryRef2 = myFirebaseRef.child("events_registrations").orderByChild("user_id").equalTo(UserFirebase.uId);
        queryRef = myFirebaseRef.child("events").orderByChild("host_id").equalTo(UserFirebase.uId);

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                SubEvent newEvent = new SubEvent();

                for (DataSnapshot child : snapshot.getChildren()) {
                    switch (child.getKey()) {   //get name of entry
                        case "event_name":
                            newEvent.setEventName(String.valueOf(child.getValue()));
                            break;
                    }
                    newEvent.setEventId(snapshot.getKey());
                }
                myEventsSubEventList.add(0, newEvent);
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

        queryRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if(child.getKey().equals("event_id")) {
                        queries[0] = myFirebaseRef.child("events").orderByKey().equalTo(String.valueOf(child.getValue()));
                        queries[0].addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                                SubEvent newEvent = new SubEvent();

                                for (DataSnapshot child : snapshot.getChildren()) {
                                    switch (child.getKey()) {
                                        case "event_name":
                                            newEvent.setEventName(String.valueOf(child.getValue()));
                                            break;
                                    }

                                    newEvent.setEventId(snapshot.getKey());
                                }
                                myEventsSubEventList.add(newEvent);
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
    }

    public static boolean detailsThreadCheck;
    /*---------------------------------------------------------------------------
     Function Name: getEventDetails
     Description: gets the datafields of an Event
     Input: String eventId - id of the event
     Output: Event - an object with all details of the event
    ---------------------------------------------------------------------------*/
    public Event getEventDetails(final String eventId) {
        detailsThreadCheck = false; //determine when query is done
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com/events");
        Query queryRef = myFirebaseRef.child(eventId);
        a = new ArrayList<String>();
        queryRef.addChildEventListener(new ChildEventListener() {
            Event newEvent = new Event();
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                switch (snapshot.getKey()) {
                    case "date":
                        newEvent.setDate(String.valueOf(snapshot.getValue()));
                        break;
                    case "description":
                        newEvent.setDescription(String.valueOf(snapshot.getValue()));
                        break;
                    case "event_name":
                        newEvent.setEventName(String.valueOf(snapshot.getValue()));
                        break;
                    case "location":
                        newEvent.setLocation(String.valueOf(snapshot.getValue()));
                        break;
                    case "number_going":
                        newEvent.setAttendees(Integer.parseInt(String.valueOf(snapshot.getValue())));
                        break;
                    case "picture":
                        newEvent.setPicture(String.valueOf(snapshot.getValue()));
                        break;
                    case "host_id":
                        newEvent.setHostId(String.valueOf(snapshot.getValue()));
                        break;
                    case "host_name":
                        newEvent.setHostName(String.valueOf(snapshot.getValue()));
                        break;
                    case "catAcademic":
                        a.add("Academic");
                        break;
                    case "catCampus":
                        a.add("Student Orgs");
                        break;
                    case "catConcerts":
                        a.add("Concerts");
                        break;
                    case "catFood":
                        a.add("Food");
                        break;
                    case "catFree":
                        a.add("Free");
                        break;
                    case "catSocial":
                        a.add("Social");
                        break;
                    case "catSports":
                        a.add("Sports");
                        break;
                }
                newEvent.setEventId(eventId);

                newEvent.setCategories(a);

                // used to get the current time
                String currentDate;
                SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd | HH:mm");
                Date dateobj = new Date();
                currentDate = df.format(dateobj);

                eventDetailsEvent = newEvent;
                detailsThreadCheck = true;
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

    int attendees = 0;

    public static boolean goingToEventThreadCheck;
    /*Purpose: Increases the amount going by 1 */
    /*---------------------------------------------------------------------------
     Function Name: goingToAnEvent
     Description: toggles a user as going to an event
     Input: String eventId - the id of the event
     Output: None
    ---------------------------------------------------------------------------*/
    public void goingToAnEvent(final String eventId) {
        goingToEventThreadCheck = false;
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com/events");
        final Firebase ref = new Firebase("https://eventory.firebaseio.com");
        Query queryRef = myFirebaseRef.child(eventId);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Checks if it is number_going that we are changing
                if (dataSnapshot.getKey().equals("number_going")) {
                    //gets the string and changes it to an int
                    attendees = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                    attendees++;
                    myFirebaseRef.child(eventId).child("number_going").setValue(attendees);

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("user_id", UserFirebase.uId);
                    map.put("event_id", eventId);
                    ref.child("events_registrations").push().setValue(map); //push to eventregs table
                }
                goingToEventThreadCheck = true;
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
    }

    public boolean notGoingThreadCheck;
    /*---------------------------------------------------------------------------
     Function Name: notGoingToAnEvent
     Description: toggles a user as not going to an event
     Input: String eventId - the id of the event
     Output: None
    ---------------------------------------------------------------------------*/
    public void notGoingToAnEvent(final String eventId) {
        notGoingThreadCheck = false;    //to check if query is finished
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com/events");
        Query queryRef = myFirebaseRef.child(eventId);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("number_going")) {
                    attendees = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                    attendees--;
                    myFirebaseRef.child(eventId).child("number_going").setValue(attendees);
                }
                notGoingThreadCheck = true;
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
    }

    static boolean isGoingThreadCheck = false;

    /*---------------------------------------------------------------------------
     Function Name: isGoing
     Description: Checks if a user is going to an event
     Input: String eventId - the id of the event
     Output: None
    ---------------------------------------------------------------------------*/
    public void isGoing(final String eventId) {
        isGoingThreadCheck = false; //check if query is done
        idIsGoing = 0;
        id = null;
        final Firebase ref = new Firebase("https://eventory.firebaseio.com/events_registrations");
        final ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                HashMap<String, Object> values = (HashMap<String, Object>) snapshot.getValue();
                if (values != null) {
                    for (Map.Entry<String, Object> entry : values.entrySet()) {
                        boolean first = false;
                        for (Map.Entry<String, Object> entry2 : ((HashMap<String, Object>) entry.getValue()).entrySet()) {
                            if (entry2.getKey().equals("event_id")) {
                                if (entry2.getValue().equals(eventId)) {
                                    first = true;
                                }
                            }

                            if (entry2.getKey().equals("user_id") && first) {
                                if (entry2.getValue().equals(UserFirebase.uId)) {
                                    id = entry.getKey();
                                }
                            }
                        }
                    }

                    if (id == null) {
                        idIsGoing = 1;
                    } else {
                        idIsGoing = 2;
                    }
                    isGoingThreadCheck = true;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        ref.addValueEventListener(valueEventListener);

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!isGoingThreadCheck) {
                    try {
                        Thread.sleep(70);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ref.removeEventListener(valueEventListener);
                isGoingThreadCheck = false;
            }
        }).start();
    }

    public static boolean deleteThreadCheck = false;

    /*---------------------------------------------------------------------------
     Function Name: deleteEventRegistration
     Description: gets rid of all eventRegistrations that contain the eventId
     Input: String eventId - the id of the event to delete
     Output: None
    ---------------------------------------------------------------------------*/
    public void deleteEventRegistration(final String eventId){
        deleteThreadCheck = false;  //to check when query is finished
        final Firebase ref = new Firebase("https://eventory.firebaseio.com/events_registrations");
        final ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                HashMap<String, Object> values = (HashMap<String, Object>) snapshot.getValue();
                if (values != null) {
                    for (Map.Entry<String, Object> entry : values.entrySet()) {
                        boolean first = false;
                        for (Map.Entry<String, Object> entry2 : ((HashMap<String, Object>) entry.getValue()).entrySet()) {
                            if (entry2.getKey().equals("event_id")) {
                                if (entry2.getValue().equals(eventId)) {
                                    first = true;
                                }
                            }
                            if (entry2.getKey().equals("user_id") && first) {
                                if (entry2.getValue().equals(UserFirebase.uId)) {
                                    id = entry.getKey();
                                }
                            }
                        }
                    }
                    if (id != null && id != "")
                        ref.child(id).removeValue();
                    deleteThreadCheck = true;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        ref.addValueEventListener(valueEventListener);

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!deleteThreadCheck) {
                    try {
                        Thread.sleep(70);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ref.removeEventListener(valueEventListener);
            }
        }).start();
    }

    public static int going = 0;

    /*---------------------------------------------------------------------------
     Function Name: updateEvent
     Description: updates the datafields of an event
     Input: EditEventForm form - a form containing the fields of the event
            EditEventCategoryRVAdapter - RVAdapter for the categories of event
     Output: None
    ---------------------------------------------------------------------------*/
    public void updateEvent(final EditEventForm form, EditEventCategoryRVAdapter adapter) {
        if (a != null) {
            for (int i = 0; i < a.size(); i++) {
            }
        }
        categoryList = adapter.getList();

        final Firebase ref = new Firebase("https://eventory.firebaseio.com");
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com/events");
        EventsFirebase eventsFirebase = new EventsFirebase();
        eventsFirebase.getNum_going(form.getEventId());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(going == 0) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd | HH:mm");
                Date dateobj = new Date();

                String originalString = form.getDate();

                char[] c = originalString.toCharArray();

                //these all switch the date from mm/dd/yy to yy/mm/dd
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
                String swappedString = new String(c); // - yy/mm/dd

                //change to 24 time

                String tempString = swappedString.substring(11, swappedString.length());
                int numb = 0;
                String sub = "";

                if (tempString.indexOf('A') == -1) {
                    if (swappedString.indexOf(':') != -1) {
                        sub = tempString.substring(0, tempString.indexOf(':'));
                    } else {
                        sub = tempString.substring(0, tempString.indexOf('P'));
                    }
                    numb = Integer.parseInt(sub);
                    if (numb != 12) {
                        numb += 12;
                    }
                } else {
                    if (swappedString.indexOf(':') != -1) {
                        sub = tempString.substring(0, tempString.indexOf(':'));
                    } else {
                        sub = tempString.substring(0, tempString.indexOf('A'));
                    }
                    numb = Integer.parseInt(sub);
                    if (numb == 12) {
                        numb = 0;
                    }
                }

                if(String.valueOf(numb).length() == 1) {
                    if (swappedString.indexOf(':') == -1) {
                        swappedString = swappedString.substring(0, 11) + "0" + numb + ":00";
                    }
                    else {
                        swappedString = swappedString.substring(0, 11) + "0" +numb + tempString.substring(tempString.indexOf(':'), tempString.length() - 2);
                    }
                }
                else {
                    if (swappedString.indexOf(':') == -1) {
                        swappedString = swappedString.substring(0, 11) + numb + ":00";
                    }
                    else {
                        swappedString = swappedString.substring(0, 11) + numb + tempString.substring(tempString.indexOf(':'), tempString.length() - 2);
                    }
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("host_id", UserFirebase.uId);
                map.put("host_name", UserFirebase.thisUser.getName());
                map.put("event_name", form.getName());
                map.put("description", form.getDescription());
                map.put("location", form.getLocation());
                map.put("date", swappedString);
                map.put("number_going", String.valueOf(going));
                map.put("picture", form.getPicture());
                map.put("created_at", df.format(dateobj));

                //parses the categories
                for (int i = 0; i < categoryList.size(); i++) {
                    if (categoryList.get(i) == "Academic") {
                        map.put("catAcademic", "true");
                    }
                    if (categoryList.get(i) == "Student Orgs") {
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
                ref.child("events").child(form.getEventId()).setValue(map);
            }
        }).start();
    }

    /*---------------------------------------------------------------------------
     Function Name: getNum_going
     Description: gets the number of people going to the event
     Input: String eventId - id of the event
     Output: None
    ---------------------------------------------------------------------------*/
    public void getNum_going(final String eventId){
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com/events");
        final Firebase ref = new Firebase("https://eventory.firebaseio.com");
        Query queryRef = myFirebaseRef.child(eventId);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Checks if it is number_going that we are changing
                if (dataSnapshot.getKey().equals("number_going")) {
                    //gets the string and changes it to an int
                    going = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
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
    }
    public static boolean threadCheckSubEvent;
    public static ArrayList<SubEvent> subEvents;

    /*---------------------------------------------------------------------------
     Function Name: getSubEventList
     Description: gets the eventlist for subscribed
     Input: None
     Output: None
    ---------------------------------------------------------------------------*/
    public void getSubEventList() {
        subEvents = new ArrayList<>();
        threadCheckSubEvent = false;
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com/events");
        Query queryRef = myFirebaseRef.orderByKey();

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                SubEvent newSubEvent  = new SubEvent();
                newSubEvent.setEventId(snapshot.getKey());

                for (DataSnapshot child : snapshot.getChildren()) {
                    switch (child.getKey()) {
                        case "event_name":
                            newSubEvent.setEventName(String.valueOf(child.getValue()));
                            break;
                    }
                }
                subEvents.add(newSubEvent);
                threadCheckSubEvent = true;
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
    }

    /*---------------------------------------------------------------------------
     Function Name: createSubFeed
     Description: creates the list of events for subscriptions
     Input: None
     Output: Event
    ---------------------------------------------------------------------------*/
    public Event createSubFeed() {
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com");
        final Query[] queries = new Query[2];
        Query queryRef = myFirebaseRef.orderByKey();
        Query queryRef1 = myFirebaseRef.orderByKey();
        Query queryRef2 = myFirebaseRef.child("user_following").orderByChild("user_id").equalTo(UserFirebase.uId);
        queries[0] = queryRef;
        queries[1] = queryRef1;

        //query within a query to see subscribed and events_registrations
        queryRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals("following_id")) {
                        queries[1] = myFirebaseRef.child("events_registrations").orderByChild("user_id").equalTo(String.valueOf(child.getValue()));
                        queries[1].addChildEventListener(new ChildEventListener() {
                            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    if (child.getKey().equals("event_id")) {
                                        queries[0] = myFirebaseRef.child("events").orderByKey().equalTo(String.valueOf(child.getValue()));
                                        queries[0].addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                                                Event newEvent = new Event();
                                                //parses through each data field
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
                                                            newEvent.setAttendees(Integer.parseInt(String.valueOf(child.getValue())));
                                                            break;
                                                        case "picture":
                                                            newEvent.setPicture(String.valueOf(child.getValue()));
                                                            break;
                                                        case "host_id":
                                                            newEvent.setHostId(String.valueOf(child.getValue()));
                                                            break;
                                                        case "host_name":
                                                            newEvent.setHostName(String.valueOf(child.getValue()));
                                                            break;
                                                    }
                                                    newEvent.setEventId(snapshot.getKey());
                                                }

                                                newEvent.setCategories(a);
                                                a = new ArrayList<String>();

                                                // used to get the current time
                                                String currentDate;
                                                SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd | HH:mm");
                                                Date dateobj = new Date();
                                                currentDate = df.format(dateobj);

                                                if (currentDate.compareTo(newEvent.getDate()) < 0){
                                                    eventsList.add(newEvent);
                                                    for(int i=0; i<eventsList.size(); i++){
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


    public boolean followersThreadCheck;
    public int numFollowers;
    /*---------------------------------------------------------------------------
     Function Name: getFollowers
     Description: gets a list of followers
     Input: ArrayList of followers
     Output: None
    ---------------------------------------------------------------------------*/
    public void getFollowers ( final ArrayList<User> followers){
        followersThreadCheck = false;
        final Firebase ref = new Firebase("https://eventory.firebaseio.com");
        Query queryRef = ref.child("user_following").orderByKey();
        queryRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                HashMap<String, Object> values = (HashMap<String, Object>) snapshot.getValue();
                if (values != null) {
                    for (Map.Entry<String, Object> entry : values.entrySet()) {

                        boolean first = false;
                        for (Map.Entry<String, Object> entry2 : ((HashMap<String, Object>) entry.getValue()).entrySet()) {
                            if (entry2.getKey().equals("following_id") && entry2.getValue().equals(UserFirebase.uId)) {
                                first = true;
                            }

                            if (entry2.getKey().equals("user_id") && first) {
                                ++numFollowers;

                                final UserFirebase userFirebase = new UserFirebase();
                                userFirebase.getAnotherUser(String.valueOf(entry2.getValue()));

                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        while (!userFirebase.threadCheckAnotherUser) {
                                            try {
                                                Thread.sleep(300);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        followers.add(userFirebase.anotherUser);
                                    }
                                }).start();
                            }
                        }
                    }
                    followersThreadCheck = true;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public static boolean goingListThreadCheck;
    /*---------------------------------------------------------------------------
     Function Name: getEventsGoingList
     Description: gets a list of events going to
     Input: String userId - the user id going to events
     Output: None
    ---------------------------------------------------------------------------*/
    public void getEventsGoingList(String userId) {
        goingListThreadCheck = false;
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com");

        Query queryRef = myFirebaseRef.child("events_registrations").orderByChild("user_id").equalTo(userId);

        Query queryRef1 = myFirebaseRef.orderByKey();
        final Query[] queries = new Query[1];
        queries[0] = queryRef;
        //queries[1] = queryRef1;

        //queries the events_registrations if the user id matches the user
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if(child.getKey().equals("event_id")) {
                        queries[0] = myFirebaseRef.child("events").orderByKey().equalTo(String.valueOf(child.getValue()));
                        queries[0].addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                                Event newEvent  = new Event();

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
                                            newEvent.setAttendees(Integer.parseInt(String.valueOf(child.getValue())));
                                            break;
                                        case "picture":
                                            newEvent.setPicture(String.valueOf(child.getValue()));
                                            break;
                                        case "host_id":
                                            newEvent.setHostId(String.valueOf(child.getValue()));
                                            break;
                                        case "host_name":
                                            newEvent.setHostName(String.valueOf(child.getValue()));
                                            break;
                                        case "catAcademic":
                                            a.add("Academic");
                                            break;
                                        case "catCampus":
                                            a.add("Student Orgs");
                                            break;
                                        case "catConcerts":
                                            a.add("Concerts");
                                            break;
                                        case "catFood":
                                            a.add("Food");
                                            break;
                                        case "catFree":
                                            a.add("Free");
                                            break;
                                        case "catSocial":
                                            a.add("Social");
                                            break;
                                        case "catSports":
                                            a.add("Sports");
                                            break;
                                    }

                                    newEvent.setEventId(snapshot.getKey());
                                }

                                newEvent.setCategories(a);
                                a = new ArrayList<String>();

                                // used to get the current time
                                String currentDate;
                                SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd | HH:mm");
                                Date dateobj = new Date();
                                currentDate = df.format(dateobj);


                                if(tabFilter != null && (tabFilter.equals(tabHot) || tabFilter.equals(tabNew))) {
                                    if (currentDate.compareTo(newEvent.getDate()) < 0){
                                        eventsList.add(0, newEvent);
                                    }
                                }
                                else {
                                    //current date and time is "earlier" than the event. Aka the event has not happened yet.
                                    if (currentDate.compareTo(newEvent.getDate()) < 0){
                                        eventsList.add(newEvent);
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
                    }
                }
                goingListThreadCheck = true;
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
    }

    public static boolean hostingListThreadCheck;
    /*---------------------------------------------------------------------------
     Function Name: getEventsHostingList
     Description: gets the user's hosted events
     Input: String userId - the id of the user hosting events
     Output: None
    ---------------------------------------------------------------------------*/
    public void getEventsHostingList(String userId) {
        hostingListThreadCheck = false;
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com");
        Query queryRef = myFirebaseRef.child("events").orderByChild("host_id").equalTo(userId);

        //querying the events tab matching host_id to userId
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
                            newEvent.setAttendees(Integer.parseInt(String.valueOf(child.getValue())));
                            break;
                        case "picture":
                            newEvent.setPicture(String.valueOf(child.getValue()));
                            break;
                        case "host_id":
                            newEvent.setHostId(String.valueOf(child.getValue()));
                            break;
                        case "host_name":
                            newEvent.setHostName(String.valueOf(child.getValue()));
                            break;
                        case "catAcademic":
                            a.add("Academic");
                            break;
                        case "catCampus":
                            a.add("Student Orgs");
                            break;
                        case "catConcerts":
                            a.add("Concerts");
                            break;
                        case "catFood":
                            a.add("Food");
                            break;
                        case "catFree":
                            a.add("Free");
                            break;
                        case "catSocial":
                            a.add("Social");
                            break;
                        case "catSports":
                            a.add("Sports");
                            break;
                    }
                    newEvent.setEventId(snapshot.getKey());
                }

                newEvent.setCategories(a);
                a = new ArrayList<String>();

                // used to get the current time
                String currentDate;
                SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd | HH:mm");
                Date dateobj = new Date();

                eventsList.add(0, newEvent);
                hostingListThreadCheck = true;
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
    }

    public static DataSnapshot registration;
    /*---------------------------------------------------------------------------
     Function Name: deleteEvent
     Description: deletes the event
     Input: String eventID - the id of the event to delete
     Output: None
    ---------------------------------------------------------------------------*/
    public void deleteEvent(final String eventID) {
        final Firebase ref = new Firebase("https://eventory.firebaseio.com");
        ref.child("events").child(eventID).removeValue();
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com");
        //double queries the events and the events_registrations
        final Query[] queries = new Query[2];
        Query queryRef = myFirebaseRef.orderByKey();
        Query queryRef1 = myFirebaseRef.orderByKey();
        Query queryRef2 = myFirebaseRef.child("events_registrations");
        queries[0] = queryRef;
        queries[1] = queryRef1;
        queryRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    registration = child;
                    queries[1] = myFirebaseRef.child("events_registrations").orderByChild("event_id");
                    queries[1].addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            for (DataSnapshot child1 : dataSnapshot.getChildren()) {
                                if (String.valueOf(child1.getValue()).equals(eventID)) {
                                    Firebase tempRef = new Firebase("https://eventory.firebaseio.com" + "/events_registrations/" + String.valueOf(dataSnapshot.getKey()));
                                    tempRef.removeValue();
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
    }

    public static ArrayList<SubEvent> searchSubFeedList;
    /*---------------------------------------------------------------------------
     Function Name: createSearchSubFeedList
     Description: creates an event list for the search functionality
     Input: None
     Output: None
    ---------------------------------------------------------------------------*/
    public void createSearchSubFeedList() {
        searchSubFeedList = new ArrayList<>();

        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com");

        final Query[] queries = new Query[2];
        Query queryRef = myFirebaseRef.orderByKey();
        Query queryRef1 = myFirebaseRef.orderByKey();
        Query queryRef2 = myFirebaseRef.child("user_following").orderByChild("user_id").equalTo(UserFirebase.uId);
        queries[0] = queryRef;
        queries[1] = queryRef1;

        //double query through both events_registrations and subscriptions
        queryRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals("following_id")) {
                        queries[1] = myFirebaseRef.child("events_registrations").orderByChild("user_id").equalTo(String.valueOf(child.getValue()));
                        queries[1].addChildEventListener(new ChildEventListener() {
                            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    if (child.getKey().equals("event_id")) {
                                        queries[0] = myFirebaseRef.child("events").orderByKey().equalTo(String.valueOf(child.getValue()));
                                        queries[0].addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                                                SubEvent newEvent = new SubEvent();

                                                String date = "";
                                                for (DataSnapshot child : snapshot.getChildren()) {
                                                    switch (child.getKey()) {
                                                        case "date":
                                                            date = String.valueOf(child.getValue());
                                                            break;
                                                        case "event_name":
                                                            newEvent.setEventName(String.valueOf(child.getValue()));
                                                            break;
                                                    }
                                                    newEvent.setEventId(snapshot.getKey());
                                                }

                                                // used to get the current time
                                                String currentDate;
                                                SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd | HH:mm");
                                                Date dateobj = new Date();
                                                currentDate = df.format(dateobj);

                                                if (currentDate.compareTo(date) < 0){
                                                    searchSubFeedList.add(newEvent);
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

    }

    public static ArrayList<SubEvent> subEventCatList;
    /*---------------------------------------------------------------------------
     Function Name: getSubEventCatList
     Description: gets the eventlist for subscribed based on categories
     Input: String catFilter - the category to filter by
     Output: None
    ---------------------------------------------------------------------------*/
    public void getSubEventCatList(String catFilter) {
        subEventCatList = new ArrayList<>();
        final Firebase myFirebaseRef = new Firebase("https://eventory.firebaseio.com");
        Query queryRef = myFirebaseRef.orderByKey();

        //orders the query based on the category
        if(catFilter != null) {
            switch (catFilter) {
                case catAcademic:
                    queryRef = myFirebaseRef.child("events").orderByChild("catAcademic").equalTo("true");
                    break;
                case catCampus:
                    queryRef = myFirebaseRef.child("events").orderByChild("catCampus").equalTo("true");
                    break;
                case catConcerts:
                    queryRef = myFirebaseRef.child("events").orderByChild("catConcerts").equalTo("true");
                    break;
                case catFood:
                    queryRef = myFirebaseRef.child("events").orderByChild("catFood").equalTo("true");
                    break;
                case catFree:
                    queryRef = myFirebaseRef.child("events").orderByChild("catFree").equalTo("true");
                    break;
                case catSocial:
                    queryRef = myFirebaseRef.child("events").orderByChild("catSocial").equalTo("true");
                    break;
                case catSports:
                    queryRef = myFirebaseRef.child("events").orderByChild("catSports").equalTo("true");
                    break;
            }
        }

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                SubEvent newEvent = new SubEvent();

                String date = "";
                for (DataSnapshot child : snapshot.getChildren()) {
                    switch (child.getKey()) {
                        case "date":
                            date = String.valueOf(child.getValue());
                            break;
                        case "event_name":
                            newEvent.setEventName(String.valueOf(child.getValue()));
                            break;
                    }
                    newEvent.setEventId(snapshot.getKey());
                }

                // used to get the current time
                String currentDate;
                SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd | HH:mm");
                Date dateobj = new Date();
                currentDate = df.format(dateobj);

                if (currentDate.compareTo(date) < 0) {
                    subEventCatList.add(newEvent);
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
    }
}