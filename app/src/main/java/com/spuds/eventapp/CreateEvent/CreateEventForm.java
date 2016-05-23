package com.spuds.eventapp.CreateEvent;

import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by qtmluong on 5/15/2016.
 */
public class CreateEventForm {
    private String name;
    private String date;
    private String location;
    private String description;
    private String picture;

    public CreateEventForm(EditText event_name, EditText event_date, EditText event_time, Spinner spinner, EditText event_location, EditText event_description, String picture){
        name = event_name.getText().toString();
        date = event_date.getText().toString() + " | " + event_time.getText().toString() + spinner.getSelectedItem().toString();;
        location = event_location.getText().toString();
        description = event_description.getText().toString();
        this.picture = picture;
    }

    public String getName(){
        return name;
    }

    public String getDate(){
        return date;
    }

    public String getLocation(){
        return location;
    }

    public String getDescription(){
        return description;
    }

    public String getPicture() {
        return picture;
    }
    public boolean allFilled(){
        if(name.length()>0 && date.length()>0 && location.length()>0 && description.length()>0){
            return true;
        }
        else{
            return false;
        }
    }
}
