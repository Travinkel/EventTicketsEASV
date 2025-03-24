package org.example.eventticketsystem.utils;

import org.example.eventticketsystem.models.Event;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class EventReader {


    //Create Arraylist of all events.
    public static ArrayList<Event> allEvents = new ArrayList<Event>();{

            try {
                // Create reader and read from "Mock Events" txt file in the resources folder.
                BufferedReader bread = new BufferedReader(new FileReader("src/main/resources/Mock Events"));

                String line;
                while ((line = bread.readLine()) != null) {
                    String[] vals = line.split(",");
                    int id = Integer.parseInt(vals[0]);

                    //Sets the params for an event to the read lines *IN ORDER* from the txt file
                    String name = vals[1];

                    //Date needs a lot of help cause THIS DATA TYPE F***ING SUCKS
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    java.util.Date date = formatter.parse(vals[2]);
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                    String location = vals[3];
                    String notes = vals[4];

                    Event e = new Event(id, name, date, location, notes);
                    allEvents.add(e);

                    String osh = Arrays.deepToString(allEvents.toArray());
                    System.out.println(osh);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }






    }


