package model;

import java.util.LinkedList;

/**
 * Created by oscarricaud on 7/28/17.
 */
public class MyCategory {
    private LinkedList<String> frontOfHouse = new LinkedList<>();
    private LinkedList<String> backOfHouse = new LinkedList<>();
    private LinkedList<String> kitchen = new LinkedList<>();
    private LinkedList<String> bar = new LinkedList<>();

    public void MyCategory(){

    }
    /* Front Of House Category */
    public LinkedList<String> getFrontOfHouse() {
        if(frontOfHouse.isEmpty()){
            System.out.println("Front of House list is empty");
        }
        return frontOfHouse;
    }

    public void addItemToFrontOfHouse(String item) {
        if(!frontOfHouse.contains(item)){ // If the list does not contain the item then don't add it
            frontOfHouse.add(item);
        }
    }

    public void addItemToBackOfHouse(String item) {
        if(!backOfHouse.contains(item)){ // If the list does not contain the item then don't add it
            backOfHouse.add(item);
        }
    }

    public void addItemToKitchen(String item) {
        if(!kitchen.contains(item)){ // If the list does not contain the item then don't add it
            kitchen.add(item);
        }
    }

    public void addItemToBar(String item) {
        if(!bar.contains(item)){ // If the list does not contain the item then don't add it
            bar.add(item);
        }
    }

    public LinkedList<String> getBackOfHouse() {
        if(backOfHouse.isEmpty()){
            System.out.println("Back of House list is empty");
        }
        return backOfHouse;
    }

    public LinkedList<String> getKitchen() {
        if(kitchen.isEmpty()){
            System.out.println("Kitchen  list is empty");
        }
        return kitchen;
    }

    public LinkedList<String> getBar() {
        if(bar.isEmpty()){
            System.out.println("Bar  list is empty");
        }
        return bar;
    }
    public LinkedList<String> getAllItems() {
        LinkedList<String> concatenateLists = new LinkedList<>();

        concatenateLists.addAll(frontOfHouse);
        concatenateLists.addAll(backOfHouse);
        concatenateLists.addAll(kitchen);
        concatenateLists.addAll(bar);

        return concatenateLists;
    }
}
