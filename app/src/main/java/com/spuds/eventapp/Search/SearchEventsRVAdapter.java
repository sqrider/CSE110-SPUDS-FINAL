package com.spuds.eventapp.Search;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spuds.eventapp.EventDetails.EventDetailsFragment;
import com.spuds.eventapp.Firebase.UserFirebase;
import com.spuds.eventapp.Profile.ProfileFragment;
import com.spuds.eventapp.R;
import com.spuds.eventapp.Shared.Event;
import com.spuds.eventapp.Shared.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tina on 4/16/16.
 */

/*---------------------------------------------------------------------------
Class Name:                SearchEventsRVAdapter
Description:               Contains information about SearchEventsRVAdapter
---------------------------------------------------------------------------*/
public class SearchEventsRVAdapter extends RecyclerView.Adapter<SearchEventsRVAdapter.EventViewHolder> {

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        CardView card;
        ImageView eventPic;
        TextView eventName;
        TextView eventLocation;
        TextView eventAttendees;
        TextView eventCategories;
        TextView eventHost;
        TextView seeMore;
        TextView monthDate, dayDate;
        TextView eventTime;

    /*---------------------------------------------------------------------------
    Class Name:                EventViewHolder
    Description:               Holds all the elements necessary for an event
    ---------------------------------------------------------------------------*/
        EventViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.cv);
            eventPic = (ImageView) itemView.findViewById(R.id.event_pic);
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            eventLocation = (TextView) itemView.findViewById(R.id.event_loc);
            eventAttendees = (TextView) itemView.findViewById(R.id.event_attendees);
            eventCategories = (TextView) itemView.findViewById(R.id.event_categories);
            eventHost = (TextView) itemView.findViewById(R.id.event_host);
            seeMore = (TextView) itemView.findViewById(R.id.label_see_more);
            monthDate = (TextView) itemView.findViewById(R.id.date_month);
            dayDate = (TextView) itemView.findViewById(R.id.date_day);
            eventTime = (TextView) itemView.findViewById(R.id.event_time);

        }

    }

    List<Event> events;
    Fragment currentFragment;
    String tagCurrentFragment;

    /*---------------------------------------------------------------------------
    Function Name:                SubscriptionsListRVAdapter
    Description:                  Constructor
    Input:                        List<User> events: array list of events
                                  Fragment currentFragment: fragment RVAdapter instantiated
                                  String tagCurrentFragment
    Output:                       None.
    ---------------------------------------------------------------------------*/
    public SearchEventsRVAdapter(List<Event> events, Fragment currentFragment, String tagCurrentFragment){
        this.events = events;
        this.currentFragment = currentFragment;
        this.tagCurrentFragment = tagCurrentFragment;
    }

    /*---------------------------------------------------------------------------
    Function Name:                onCreateViewHolder()
    Description:                  Necessary method to override: Defines the layout
                                  and type of each view holder
    Input:                        ViewGroup viewGroup
                                  int i
    Output:                       EventViewHolder
    ---------------------------------------------------------------------------*/
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //Change the view of this font!!!
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_event_feed, viewGroup, false);
        //Typeface raleway_light = Typeface.createFromAsset(v.getContext().getAssets(),  "raleway-light.ttf");
        overrideFonts(v.getContext(),v);

        Typeface raleway_medium = Typeface.createFromAsset(viewGroup.getContext().getAssets(),  "Raleway-Medium.ttf");

        //title font
        TextView name = (TextView) v.findViewById(R.id.event_name);
        name.setTypeface(raleway_medium);

        EventViewHolder evh = new EventViewHolder(v);
        return evh;
    }


    /*---------------------------------------------------------------------------
    Function Name:                onBindViewHolder()
    Description:                  Necessary method to override: Binds information
                                  to each view holder at position i
    Input:                        EventViewHolder eventViewHolder
                                  int i: position of the item in the RecyclerView
    Output:                       None.
    ---------------------------------------------------------------------------*/
    @Override
    public void onBindViewHolder(final EventViewHolder eventViewHolder, final int i) {

        if (events.get(i).getPicture() != null && events.get(i).getPicture() != "") {
            String imageFile = events.get(i).getPicture();
            Bitmap src = null;

            try {
                byte[] imageAsBytes = Base64.decode(imageFile, Base64.DEFAULT);
                src = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            } catch (OutOfMemoryError e) {
                System.err.println(e.toString());
            }
            // If it was created successfully, set image bitmap
            if (src != null) {
                eventViewHolder.eventPic.setImageBitmap(src);
            }
            // If it was not created successfully, set image bitmap to default wine and dine
            else
                eventViewHolder.eventPic.setImageResource(R.drawable.wineanddine);
        // If no picture exists, set image bitmap to default wine and dine
        } else {
            eventViewHolder.eventPic.setImageResource(R.drawable.wineanddine);
        }

        // Set the event information to corresponding views
        eventViewHolder.eventName.setText(events.get(i).getEventName());
        eventViewHolder.eventLocation.setText(events.get(i).getLocation());
        eventViewHolder.eventAttendees.setText(String.valueOf(events.get(i).getAttendees()));
        eventViewHolder.eventHost.setText(events.get(i).getHostName());

        String d = "";

        switch (events.get(i).getDate().substring(3,5)) {
            case "01":
                d = "J A N";
                break;
            case "02":
                d = "F E B";
                break;
            case "03":
                d = "M A R";
                break;
            case "04":
                d = "A P R";
                break;
            case "05":
                d = "M A Y";
                break;
            case "06":
                d = "J U N";
                break;
            case "07":
                d = "J U L";
                break;
            case "08":
                d = "A U G";
                break;
            case "09":
                d = "S E P";
                break;
            case "10":
                d = "O C T";
                break;
            case "11":
                d = "N O V";
                break;
            case "12":
                d = "D E C";
                break;
        }
        //set the month
        eventViewHolder.monthDate.setText(d);
        //set the day
        eventViewHolder.dayDate.setText(String.valueOf(events.get(i).getDate().substring(6,8)));

        String tempString = events.get(i).getDate().substring(11, events.get(i).getDate().length());
        String sub = tempString.substring(0, tempString.indexOf(':'));
        String col = tempString.substring(tempString.indexOf(':'), tempString.length());
        int numb = Integer.parseInt(sub);

        //PM
        if(Integer.parseInt(sub) >= 12 && Integer.parseInt(sub) < 24) {
            if(numb != 12) {
                numb -= 12;
            }

            sub = numb + col + "PM";
        }

        //AM
        else{
            if(numb == 0) {
                numb += 12;
            }
            sub = numb + col + "AM";
        }

        eventViewHolder.eventTime.setText(sub);

        // Categories
        String categories = "";
        if(events.get(i).getCategories() != null && events.get(i).getCategories().size() != 0) {
            for (int categoryIndex = 0; categoryIndex < events.get(i).getCategories().size() - 1; categoryIndex++) {
                categories += events.get(i).getCategories().get(categoryIndex) + ", ";
            }
            categories += events.get(i).getCategories().get(events.get(i).getCategories().size() - 1);
        }

        eventViewHolder.eventCategories.setText(categories);

        final int test = i;
        //set on click listener for the entire event card
        eventViewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Pass the event object in a bundle to pass to Event Details Fragment
                Fragment eventDetailsFragment = new EventDetailsFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable(currentFragment.getString(R.string.event_details), events.get(test));
                eventDetailsFragment.setArguments(bundle);

                // Makes sure the fragment being hidden is the tabs fragment which includes each page fragment,
                // not just one of the view page fragments
                String tabFragmentTag = "";
                if (tagCurrentFragment.equals(currentFragment.getString(R.string.fragment_home_feed))) {
                    tabFragmentTag = currentFragment.getString(R.string.home);
                } else if(tagCurrentFragment.equals(currentFragment.getString(R.string.fragment_my_events))) {
                    tabFragmentTag = currentFragment.getString(R.string.my_events);
                } else if (tagCurrentFragment.equals(currentFragment.getString(R.string.fragment_my_sub_feed))) {
                    tabFragmentTag = currentFragment.getString(R.string.subscriptionFeed);
                } else if (tagCurrentFragment.equals(currentFragment.getString(R.string.fragment_category_feed)))
                {
                    tabFragmentTag = currentFragment.getString(R.string.category_feed);
                }


                ((MainActivity) currentFragment.getActivity()).removeSearchToolbar();
                // Add Event Details Fragment to fragment manager
                currentFragment.getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame_layout, eventDetailsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(tabFragmentTag)
                        .commit();

            }
        });


    }

    ArrayList<Button> buttons = new ArrayList<>();


    /*---------------------------------------------------------------------------
    Function Name:                getItemCount()
    Description:                  Necessary method to override: How many items
                                  in the RecyclerView
    Input:                        None
    Output:                       int: number of cards/items
    ---------------------------------------------------------------------------*/
    @Override
    public int getItemCount() {
        return events.size();
    }

    /*---------------------------------------------------------------------------
    Function Name:                overrideFonts()
    Description:                  used to override fonts
    Input:                        Context context: the context we care about
                                  View v: the view we care about
    Output:                       None.
    ---------------------------------------------------------------------------*/
    private void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView ) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "raleway-regular.ttf"));
            }
        }
        catch (Exception e) {
        }
    }

    /*---------------------------------------------------------------------------
    Function Name:                startProfileFragment()
    Description:                  Switches the view to the profile fragment
                                  passing in the required fields
    Input:                        final UserFirebase userFirebase
    Output:                       None.
    ---------------------------------------------------------------------------*/
    private void startProfileFragment(final UserFirebase userFirebase) {

        // Create a new profile fragment
        Fragment profileFragment = new ProfileFragment();
        // Pass in the type of the profile
        Bundle bundle = new Bundle();
        bundle.putString(currentFragment.getString(R.string.profile_type),
                currentFragment.getString(R.string.profile_type_other));

        // Pass in details of the user to profile
        bundle.putSerializable(currentFragment.getString(R.string.user_details), userFirebase.anotherUser);

        profileFragment.setArguments(bundle);

        currentFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)currentFragment.getActivity()).removeSearchToolbar();
            }
        });
        // Add Event Details Fragment to fragment manager
        currentFragment.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame_layout, profileFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(currentFragment.getString(R.string.fragment_profile))
                .commit();

    }


}
