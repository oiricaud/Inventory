package model;

import java.util.LinkedList;

/**
 * Created by oscarricaud on 7/30/17.
 */
public class MyEmployees {
    private LinkedList<String> frontOfHouse = new LinkedList<>();
    private LinkedList<String> backOfHouse = new LinkedList<>();
    private LinkedList<String> kitchen = new LinkedList<>();
    private LinkedList<String> bar = new LinkedList<>();

    public void MyCategory(){

    }
    /* Front Of House Category */
    public LinkedList<String> getEmployeeFrontOfHouse() {
        if(frontOfHouse.isEmpty()){
            System.out.println("No employees found in Front of House");
        }
        return frontOfHouse;
    }

    public void addEmployeeToFrontOfHouse(String item) {
        if(!frontOfHouse.contains(item)){ // If the list does not contain the item then don't add it
            frontOfHouse.add(item);
        }
    }

    public void addEmployeeToBackOfHouse(String item) {
        if(!backOfHouse.contains(item)){ // If the list does not contain the item then don't add it
            backOfHouse.add(item);
        }
    }

    public void addEmployeeToKitchen(String item) {
        if(!kitchen.contains(item)){ // If the list does not contain the item then don't add it
            kitchen.add(item);
        }
    }

    public void addEmployeeToBar(String item) {
        if(!bar.contains(item)){ // If the list does not contain the item then don't add it
            bar.add(item);
        }
    }

    public LinkedList<String> getEmployeeBackOfHouse() {
        if(backOfHouse.isEmpty()){
            System.out.println("No employees found in Back of House");
        }
        return backOfHouse;
    }

    public LinkedList<String> getEmployeeKitchen() {
        if(kitchen.isEmpty()){
            System.out.println("No employees found in Kitchen");
        }
        return kitchen;
    }

    public LinkedList<String> getEmployeeBar() {
        if(bar.isEmpty()){
            System.out.println("No employees found in Front of House");
        }
        return bar;
    }
    public LinkedList<String> getAllEmployee() {
        LinkedList<String> concatenateLists = new LinkedList<>();

        concatenateLists.addAll(frontOfHouse);
        concatenateLists.addAll(backOfHouse);
        concatenateLists.addAll(kitchen);
        concatenateLists.addAll(bar);

        return concatenateLists;
    }
}
