package model;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 *
 * Created by oscarricaud on 7/22/17.
 */
/* This class allows you to parse json objects */
public class Parser {
    private LinkedList<String> items = new LinkedList<>();
    private LinkedList<String> lowStockItems = new LinkedList<>();

/* Employees  */
    private LinkedList<String> employees = new LinkedList<>();
    private LinkedList<String> epFrontOfHouse = new LinkedList<>();
    private LinkedList<String> epBackOfHouse = new LinkedList<>();
    private LinkedList<String> epKitchen = new LinkedList<>();
    private LinkedList<String> epBar = new LinkedList<>();

/* Categories for items */
    private LinkedList<String> catFrontOfHouse = new LinkedList<>();
    private LinkedList<String> catBackOfHouse = new LinkedList<>();
    private LinkedList<String> catKitchen = new LinkedList<>();
    private LinkedList<String> catBar = new LinkedList<>();

/* Inventory - Distributors */
    private LinkedList<String> ivLamsItemList = new LinkedList<>();
    private LinkedList<String> ivSamsItemList = new LinkedList<>();
    private LinkedList<String> ivTaiwanTradingItemList = new LinkedList<>();
    private LinkedList<String> ivRestaurantDepotItemList = new LinkedList<>();
    private LinkedList<String> ivShamrockItemList = new LinkedList<>();
    private LinkedList<String> ivCoscoItemList = new LinkedList<>();
    private LinkedList<String> ivFoodOfKingItemList = new LinkedList<>();

    public Parser(){

    }



/* PARSE EMPLOYEES */
    public void parseEmployees(JSONObject jsonObject){
        try {
            String currentRow = String.valueOf(jsonObject);
            String fullName = String.valueOf(jsonObject.get("fullname"));
            String username = String.valueOf(jsonObject.get("username"));
            String userType = String.valueOf(jsonObject.get("usertype"));

            Log.w("current row", currentRow);
            Log.w("nameOfEmployee", fullName);
            Log.w("userType", userType);
            Log.w("username", username);

            /* If you remove the next 4 lines of code then it will mess up the table in the inventory view */
            fullName = fullName.replace(' ', '-');
            username = username.replace(' ', '-');
            userType = userType.replace(' ', '-');

            if (userType.equalsIgnoreCase("Front-Of-House")) {
                addEmployeeToFrontOfHouse(fullName + " " + username + " " + userType);
            } else if (userType.equalsIgnoreCase("Back-Of-House")) {
                addEmployeeToBackOfHouse(fullName + " " + username + " " + userType);
            } else if (userType.equalsIgnoreCase("Kitchen")) {
                addEmployeeToKitchen(fullName + " " + username + " " + userType);
            } else if (userType.equalsIgnoreCase("Bar")) {
                addEmployeeToBar(fullName + " " + username + " " + userType);
            }

            employees.add(fullName);
            employees.add(username);
            employees.add(userType);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

/* PARSE CATEGORIES */
    public void parseCategory(JSONObject jsonObject) {

        try {
            String nameOfItem = String.valueOf(jsonObject.get("item"));
            String distributor = String.valueOf(jsonObject.get("distributor"));
            String category = String.valueOf(jsonObject.get("category"));
            String curr_qty = String.valueOf(jsonObject.get("curr_qty"));
            String max_qty = String.valueOf(jsonObject.get("max_qty"));
            String price = String.valueOf(jsonObject.get("price"));
            //Log.w("lowStockRow", lowStockRow);

            Log.w("nameOfItem", nameOfItem);
            Log.w("distributor", distributor);

            /* If you remove the next 4 lines of code then it will mess up the table in the inventory view */
            nameOfItem = nameOfItem.replace(' ', '-');
            distributor = distributor.replace(' ', '-');
            category = category.replace(' ', '-');

            if (category.equalsIgnoreCase("Front-Of-House")) {
                addItemToFrontOfHouse(nameOfItem + " " + category + " " + distributor);
            } else if (category.equalsIgnoreCase("Back-Of-House")) {
                addItemToBackOfHouse(nameOfItem + " " + category + " " + distributor);
            }  else if (category.equalsIgnoreCase("Kitchen")) {
                addItemToKitchen(nameOfItem + " " +  category + " " + distributor);
            } else if (category.equalsIgnoreCase("Bar")) {
                addItemToBar(nameOfItem + " " +  category + " " + distributor);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

/* PARSE INVENTORY */
    public void parseInventory(JSONObject jsonObject) {

        try {
            String item = String.valueOf(jsonObject.get("item"));
            String category = String.valueOf(jsonObject.get("category"));
            String curr_qty = String.valueOf(jsonObject.get("curr_qty"));
            String max_qty = String.valueOf(jsonObject.get("max_qty"));
            String distributor = String.valueOf(jsonObject.get("distributor"));
            String price = String.valueOf(jsonObject.get("price"));

            System.out.println("Category " + category);

            double convertCurrQty = Double.parseDouble(curr_qty.toString());
            double convertMaxQty = Double.parseDouble(max_qty.toString());
            double ratio = convertCurrQty / convertMaxQty;
            System.out.println("convertCurrQty " + convertCurrQty);
            System.out.println("convertMaxQty " + convertMaxQty);
            System.out.println("ratio " + ratio);

            item = item.replace(' ', '-');
            category = category.replace(' ', '-');
            curr_qty = curr_qty.replace(' ', '-');
            max_qty = max_qty.replace(' ', '-');
            distributor = distributor.replace(' ', '-');

            items.add(item);
            items.add(category);
            items.add(curr_qty);
            items.add(max_qty);
            items.add(distributor);
            items.add(price);
            setItems(items);

            if (distributor.equalsIgnoreCase("Lams")) {
                addItemToLamsList(item + " " + category + " " + curr_qty + " " + max_qty);
            } else if (distributor.equalsIgnoreCase("Sams")) {
                addItemToSamsList(item + " " + category + " " + curr_qty + " " + max_qty);
            }  else if (distributor.equalsIgnoreCase("Taiwan-Trading")){
                addItemToTaiwanTradingItemList(item + " " + category + " " + curr_qty + " " + max_qty);
            } else if (distributor.equalsIgnoreCase("Restaurant-Depot")){
                addItemToRestaurantDepotItemList(item + " " + category + " " + curr_qty + " " + max_qty);
            } else if (distributor.equalsIgnoreCase("Shamrock")) {
                addItemToShamrockItemList(item + " " + category + " " + curr_qty + " " + max_qty);
            } else if (distributor.equalsIgnoreCase("Cosco")){
                addItemToCoscoItemList(item + " " + category + " " + curr_qty + " " + max_qty);
            } else if (distributor.equalsIgnoreCase("Food-King")){
                addItemToFoodOfKingItemList(item + " " + category + " " + curr_qty + " " + max_qty);
            }

            if((ratio) <= .2){ // Low Stock
                setLowStockItems(item + " " + distributor + " " + price + " " + curr_qty);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




/* GETTERS AND SETTERS FOR EMPLOYEES */
    public LinkedList<String> getEmployeeFrontOfHouse() {
        if(epFrontOfHouse.isEmpty()){
            System.out.println("No employees found in Front of House");
        }
        return epFrontOfHouse;
    }

    public LinkedList<String> getEmployeeBackOfHouse() {
        if(epBackOfHouse.isEmpty()){
            System.out.println("No employees found in Back of House");
        }
        return epBackOfHouse;
    }

    public LinkedList<String> getEmployeeKitchen() {
        if(epKitchen.isEmpty()){
            System.out.println("No employees found in Kitchen");
        }
        return epKitchen;
    }

    public LinkedList<String> getEmployeeBar() {
        if(epBar.isEmpty()){
            System.out.println("No employees found in Front of House");
        }
        return epBar;
    }

    public LinkedList<String> getAllEmployee() {
        LinkedList<String> concatenateLists = new LinkedList<>();

        concatenateLists.addAll(epFrontOfHouse);
        concatenateLists.addAll(epBackOfHouse);
        concatenateLists.addAll(epKitchen);
        concatenateLists.addAll(epBar);

        return concatenateLists;
    }

    private void addEmployeeToFrontOfHouse(String item) {
        if(!epFrontOfHouse.contains(item)){ // If the list does not contain the item then don't add it
            epFrontOfHouse.add(item);
        }
    }

    private void addEmployeeToBackOfHouse(String item) {
        if(!epBackOfHouse.contains(item)){ // If the list does not contain the item then don't add it
            epBackOfHouse.add(item);
        }
    }

    private void addEmployeeToKitchen(String item) {
        if(!epKitchen.contains(item)){ // If the list does not contain the item then don't add it
            epKitchen.add(item);
        }
    }

    private void addEmployeeToBar(String item) {
        if(!epBar.contains(item)){ // If the list does not contain the item then don't add it
            epBar.add(item);
        }
    }

    private void setEmployees(LinkedList<String> employee) {
        this.employees = employees;
    }

    public LinkedList<String> allEmployees() {
        return employees;
    }




/* GETTERS AND SETTERS FOR SECTIONS/CATEGORIES */
    public LinkedList<String> getFrontOfHouse() {
        if(catFrontOfHouse.isEmpty()){
            System.out.println("Front of House list is empty");
        }
        return catFrontOfHouse;
    }

    public void addItemToFrontOfHouse(String item) {
        if(!catFrontOfHouse.contains(item)){ // If the list does not contain the item then don't add it
            catFrontOfHouse.add(item);
        }
    }

    private void addItemToBackOfHouse(String item) {
        if(!catBackOfHouse.contains(item)){ // If the list does not contain the item then don't add it
            catBackOfHouse.add(item);
        }
    }

    private void addItemToKitchen(String item) {
        if(!catKitchen.contains(item)){ // If the list does not contain the item then don't add it
            catKitchen.add(item);
        }
    }

    private void addItemToBar(String item) {
        if(!catBar.contains(item)){ // If the list does not contain the item then don't add it
            catBar.add(item);
        }
    }

    public LinkedList<String> getBackOfHouse() {
        if(catBackOfHouse.isEmpty()){
            System.out.println("Back of House list is empty");
        }
        return catBackOfHouse;
    }

    public LinkedList<String> getKitchen() {
        if(catKitchen.isEmpty()){
            System.out.println("Kitchen  list is empty");
        }
        return catKitchen;
    }

    public LinkedList<String> getBar() {
        if(catBar.isEmpty()){
            System.out.println("Bar  list is empty");
        }
        return catBar;
    }

    public LinkedList<String> getAllItems() {
        LinkedList<String> concatenateLists = new LinkedList<>();

        concatenateLists.addAll(catFrontOfHouse);
        concatenateLists.addAll(catBackOfHouse);
        concatenateLists.addAll(catKitchen);
        concatenateLists.addAll(catBar);

        return concatenateLists;
    }



/* GETTERS AND SETTERS FOR ITEMS */
    private void setItems(LinkedList<String> items) {
        this.items = items;
    }

    private void setLowStockItems(String row) {
        if(!lowStockItems.contains(row)){ // If the list does not contain the item then don't add it
            lowStockItems.add(row);
        }
    }

    public LinkedList<String> allItemsFromLowStock() {
        return lowStockItems;
    }

    public LinkedList<String> allItems() {
        return items;
    }



/* GETTERS AND SETTERS FOR INVENTORY / DISTRIBUTORS */
    /* Food Of King */
public LinkedList<String> getFoodOfKingItemListItemList() {
    if(ivFoodOfKingItemList.isEmpty()){
        System.out.println("Food Of King List is empty");
    }
    return ivFoodOfKingItemList;
}

    public void addItemToFoodOfKingItemList(String item) {
        if(!ivFoodOfKingItemList.contains(item)){ // If the list does not contain the item then don't add it
            ivFoodOfKingItemList.add(item);
        }
    }

    /* Cosco */
    public LinkedList<String> getCoscoItemsList() {
        if(ivCoscoItemList.isEmpty()){
            System.out.println("Shamrock List is empty");
        }
        return ivCoscoItemList;
    }

    public void addItemToCoscoItemList(String item) {
        if(!ivCoscoItemList.contains(item)){ // If the list does not contain the item then don't add it
            ivCoscoItemList.add(item);
        }
    }

    /* Shamrock */
    public LinkedList<String> getShamrockItemsList() {
        if(ivShamrockItemList.isEmpty()){
            System.out.println("Shamrock List is empty");
        }
        return ivShamrockItemList;
    }

    public void addItemToShamrockItemList(String item) {
        if(!ivShamrockItemList.contains(item)){ // If the list does not contain the item then don't add it
            ivShamrockItemList.add(item);
        }
    }

    /* Restaurant Depot */
    public LinkedList<String> getRestaurantDepotItemsList() {
        if(ivRestaurantDepotItemList.isEmpty()){
            System.out.println("Taiwan Trading Item List is empty");
        }
        return ivRestaurantDepotItemList;
    }

    public void addItemToRestaurantDepotItemList(String item) {
        if(!ivRestaurantDepotItemList.contains(item)){ // If the list does not contain the item then don't add it
            ivRestaurantDepotItemList.add(item);
        }
    }

    /* Taiwan Trading */
    public LinkedList<String> getTaiwanTradingItemsList() {
        if(ivTaiwanTradingItemList.isEmpty()){
            System.out.println("Taiwan Trading Item List is empty");
        }
        return ivTaiwanTradingItemList;
    }

    public void addItemToTaiwanTradingItemList(String item) {
        if(!ivTaiwanTradingItemList.contains(item)){ // If the list does not contain the item then don't add it
            ivTaiwanTradingItemList.add(item);
        }
    }

    /* SAMS */
    public LinkedList<String> getSamsItemsList() {
        if(ivSamsItemList.isEmpty()){
            System.out.println("Sams Item List is empty");
        }
        return ivSamsItemList;
    }

    public void addItemToSamsList(String item) {
        if(!ivSamsItemList.contains(item)){ // If the list does not contain the item then don't add it
            ivSamsItemList.add(item);
        }
    }

    /* LAMS */
    public LinkedList<String> getLamsItemsList() {
        if(ivLamsItemList.isEmpty()){
            System.out.println("Lams Item List is empty");
        }
        return ivLamsItemList;
    }

    public void addItemToLamsList(String item) {
        if(!ivLamsItemList.contains(item)){ // If the list does not contain the item then don't add it
            ivLamsItemList.add(item);
        }
    }

    public LinkedList<String> getAllItemsFromDistributors() {
        LinkedList<String> concatenateLists = new LinkedList<>();

        concatenateLists.addAll(ivLamsItemList);
        concatenateLists.addAll(ivSamsItemList);
        concatenateLists.addAll(ivTaiwanTradingItemList);
        concatenateLists.addAll(ivRestaurantDepotItemList);
        concatenateLists.addAll(ivShamrockItemList);
        concatenateLists.addAll(ivCoscoItemList);
        concatenateLists.addAll(ivFoodOfKingItemList);

        return concatenateLists;
    }

    public LinkedList<String> getItemsFromSection(String someSection){
        LinkedList<String> tempList = new LinkedList<>();
        for(int i = 0; i < getAllItems().size(); i++) {
            if(getAllItems().get(i).equalsIgnoreCase(someSection)) {
                tempList.add(getAllItems().get(i));
            }
        }
        return tempList;
    }
}
