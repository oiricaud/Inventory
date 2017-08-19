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
   // private LinkedList<String> items = new LinkedList<>();
    private LinkedList<String> countItems = new LinkedList<>();
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
    private LinkedList<String> inventoryTable = new LinkedList<>();

/* Weekly Count */
    private LinkedList<String> weeklyCountTable = new LinkedList<>();

/* Prices */
    private LinkedList<String> pcLamsPriceList = new LinkedList<>();
    private LinkedList<String> pcSamsPriceList = new LinkedList<>();
    private LinkedList<String> pcTaiwanTradingPriceList = new LinkedList<>();
    private LinkedList<String> pcRestaurantDepotPriceList = new LinkedList<>();
    private LinkedList<String> pcShamrockPriceList = new LinkedList<>();
    private LinkedList<String> pcCoscoPriceList = new LinkedList<>();
    private LinkedList<String> pcFoodOfKingPriceList = new LinkedList<>();

/* Active Orders */
    private LinkedList<String> phoTreBienActiveOrders = new LinkedList<>();
    private int numOfOrders;
    private int countNumberOfOrders = 0;
    private LinkedList<String> tempCounter = new LinkedList<>();

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
            Log.w("fullName", fullName);
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

/* PARSE SECTIONS */
    public void parseSections(JSONObject jsonObject) {

        try {
            String nameOfItem = String.valueOf(jsonObject.get("item"));
            String last_time_updated = String.valueOf(jsonObject.get("last_time_updated"));
            String category = String.valueOf(jsonObject.get("category"));
            String curr_qty = String.valueOf(jsonObject.get("curr_qty"));
            String max_qty = String.valueOf(jsonObject.get("max_qty"));
            //Log.w("lowStockRow", lowStockRow);

            Log.w("nameOfItem", nameOfItem);
            Log.w("last_time_updated", last_time_updated);

            /* If you remove the next 4 lines of code then it will mess up the table in the inventory view */
            nameOfItem = nameOfItem.replace(' ', '-');
            last_time_updated = last_time_updated.replace(' ', '-');
            category = category.replace(' ', '-');

            if (category.equalsIgnoreCase("Front-Of-House")) {
                addItemToFrontOfHouse(nameOfItem + " " + category + " " + last_time_updated);
            } else if (category.equalsIgnoreCase("Back-Of-House")) {
                addItemToBackOfHouse(nameOfItem + " " + category + " " + last_time_updated);
            }  else if (category.equalsIgnoreCase("Kitchen")) {
                addItemToKitchen(nameOfItem + " " +  category + " " + last_time_updated);
            } else if (category.equalsIgnoreCase("Bar")) {
                addItemToBar(nameOfItem + " " +  category + " " + last_time_updated);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

/* PARSE INVENTORY */
    public void parseInventory(JSONObject jsonObject) {

        try {
            String id = String.valueOf(jsonObject.get("id"));
            String item = String.valueOf(jsonObject.get("item"));
            String category = String.valueOf(jsonObject.get("category"));
            String curr_qty = String.valueOf(jsonObject.get("curr_qty"));
            String min_qty = String.valueOf(jsonObject.get("min_qty"));
            String max_qty = String.valueOf(jsonObject.get("max_qty"));
            String last_time_updated = String.valueOf(jsonObject.get("last_time_updated"));

            double convertCurrQty = Double.parseDouble(curr_qty);
            double convertMaxQty = Double.parseDouble(max_qty);
            double ratio = convertCurrQty / convertMaxQty;

            System.out.println("parseInventory");
            System.out.println("    id " + id);
            System.out.println("    item " + item);
            System.out.println("    category " + category);
            System.out.println("    curr_qty " + curr_qty);
            System.out.println("    min_qty " + min_qty);
            System.out.println("    max_qty " + max_qty);
            System.out.println("    last_time_updated " + last_time_updated);

            System.out.println("    convertCurrQty " + convertCurrQty);
            System.out.println("    convertMaxQty " + convertMaxQty);
            System.out.println("    ratio " + ratio);

            item = item.replace(' ', '-');
            category = category.replace(' ', '-');
            curr_qty = curr_qty.replace(' ', '-');
            max_qty = max_qty.replace(' ', '-');
            last_time_updated = last_time_updated.replace(' ', '-');

            addItemToInventoryTable(id, item, category, curr_qty, max_qty, last_time_updated);

            if((ratio) <= .2){ // Low Stock
                // Negate the ratio value
                int negateRatio = (int) (convertMaxQty - convertCurrQty);
                setLowStockItems(item + " " + category + " " + curr_qty + " " + min_qty + " " + max_qty + " " +
                        last_time_updated + " " + negateRatio);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("setLowStockItems1 " + lowStockItems.toString());
    }

/* PARSE WEEKLY COUNT */
    public void parseInventoryCount(JSONObject jsonObject) {
        try {
            if(!inventoryTable.isEmpty()){
                inventoryTable.clear();
            }
            String id = String.valueOf(jsonObject.get("id"));
            String item = String.valueOf(jsonObject.get("item"));
            String category = String.valueOf(jsonObject.get("category"));
            String curr_qty = String.valueOf(jsonObject.get("curr_qty"));
            String max_qty = String.valueOf(jsonObject.get("max_qty"));
            String last_time_updated = String.valueOf(jsonObject.get("last_time_updated"));

            double convertCurrQty = Double.parseDouble(curr_qty);
            double convertMaxQty = Double.parseDouble(max_qty);
            double ratio = convertCurrQty / convertMaxQty;

            System.out.println("parseInventoryCount");
            System.out.println("    id " + id);
            System.out.println("    item " + item);
            System.out.println("    category " + category);
            System.out.println("    curr_qty " + curr_qty);
            System.out.println("    max_qty " + max_qty);
            System.out.println("    last_time_updated " + last_time_updated);

            System.out.println("    convertCurrQty " + convertCurrQty);
            System.out.println("    convertMaxQty " + convertMaxQty);
            System.out.println("    ratio " + ratio);

            item = item.replace(' ', '-');
            category = category.replace(' ', '-');
            curr_qty = curr_qty.replace(' ', '-');
            max_qty = max_qty.replace(' ', '-');
            last_time_updated = last_time_updated.replace(' ', '-');

            addItemToInventoryCountTable(id, item, category, curr_qty, max_qty, last_time_updated);

        } catch (JSONException e) {
            e.printStackTrace();
        }
}

/* PARSE PRICES */
    public void parsePrices(JSONObject jsonObject){
            try {
                String currentRow = String.valueOf(jsonObject);
                String item = String.valueOf(jsonObject.get("item"));
                String pricePerQty = String.valueOf(jsonObject.get("pricePerQty"));
                String seller = String.valueOf(jsonObject.get("seller"));

                Log.w("parsePrices", currentRow);

                Log.w("     item", item);
                Log.w("     pricePerQty", pricePerQty);
                Log.w("     seller", seller);

                    /* If you remove the next 4 lines of code then it will mess up the table in the inventory view */
                item = item.replace(' ', '-');
                pricePerQty = pricePerQty.replace(' ', '-');
                seller = seller.replace(' ', '-');

                if (seller.equalsIgnoreCase("Lams")) {
                    addPriceToLams(item + " " + pricePerQty + " " + seller);
                } else if (seller.equalsIgnoreCase("Sams")) {
                    addPriceToSams(item + " " + pricePerQty + " " + seller);
                } else if (seller.equalsIgnoreCase("Taiwan-Trading")) {
                    addPriceToTaiwanTrading(item + " " + pricePerQty + " " + seller);
                } else if (seller.equalsIgnoreCase("Restaurant-Depot")) {
                    addPriceToRestaurantDepot(item + " " + pricePerQty + " " + seller);
                } else if (seller.equalsIgnoreCase("Shamrock")) {
                    addPriceToShamrock(item + " " + pricePerQty + " " + seller);
                } else if (seller.equalsIgnoreCase("Cosco")) {
                    addPriceToCosco(item + " " + pricePerQty + " " + seller);
                } else if (seller.equalsIgnoreCase("Food-King")) {
                    addPriceToFoodKing(item + " " + pricePerQty + " " + seller);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
}


/* PARSE - RECOMMENDED */
public LinkedList<String> parseRecommended(LinkedList<String> lowStock, LinkedList<String> prices){
    LinkedList<String> copy = new LinkedList<String>();

    LinkedList<String> temp = new LinkedList<>();
    for(int i = 0; i < lowStock.size(); i++) {
        String currRowLowStock = lowStock.get(i);
        String lowStockBreak[] = currRowLowStock.split(" ");

        System.out.println("lowStockBreak[0]" + lowStockBreak[0]); // Item
        System.out.println("lowStockBreak[1]" + lowStockBreak[1]); // Price
        System.out.println("lowStockBreak[2]" + lowStockBreak[2]); // qty


        for(int j = 0; j < prices.size(); j++){
            String currRowPrice = prices.get(j);
            String priceBreak[] = currRowPrice.split(" ");

            System.out.println("    priceBreak[0]" + priceBreak[0]); // Item
            System.out.println("    priceBreak[1]" + priceBreak[1]); // Price
            System.out.println("    priceBreak[2]" + priceBreak[2]); // Distributor

            // From the inventory table we found an item that is sold by a distributor
            if(!copy.contains(lowStockBreak[0])){ //
                System.out.println("We already added this item " + lowStockBreak[0]);
                // temp.add(temp.get(j));
            }
            if(lowStockBreak[0].equalsIgnoreCase(priceBreak[0])){
                copy.add(lowStockBreak[0] + " " +  priceBreak[2] + " " + priceBreak[1]  + " " + lowStockBreak[2]);
            }
        }

        // If an item is sold in multiple market/distributors then get the cheapest one
        if(lowStockBreak[0].contains(copy.toString())){
            System.out.println("We found a multiple item that is sold by multiple distributors");
        }
    }

    System.out.println("TEMP LIST"  + temp.toString());

    // If an item is sold in multiple market/distributors then get the cheapest one


    for(int k = 0; k < copy.size(); k++){
        String currRow = copy.get(k);
        String[] breakString = currRow.split(" ");

        System.out.println("currRow[item] =  " + breakString[0]); // Item
        System.out.println("currRow[seller] = " + breakString[1]); // Seller
        System.out.println("currRow[price] = " + breakString[2]); // Price

        for(int l = 1; l < copy.size(); l++){
            String nextRow = copy.get(l);

            String[] breakSecondString = nextRow.split(" ");
            System.out.println("    nextRow[item] = " + breakSecondString[0]); // Item
            System.out.println("    nextRow[seller] = " + breakSecondString[1]); // Seller
            System.out.println("    nextRow[price] =" + breakSecondString[2]); // Price

            String a = breakString[0];
            String b = breakSecondString[0];

            System.out.println("        A = " + a);
            System.out.println("        B = " + b);
            double c = Double.parseDouble(breakString[2]);
            double d = Double.parseDouble(breakSecondString[2]);
            System.out.println("                C = " + c);
            System.out.println("                D = " + d);

            if(a.equalsIgnoreCase(b)){ // A == B
                if(c < d){
                    copy.remove(l);
                }
                if(c > d){
                    copy.remove(l-1);
                }
            }
        }
    }
    return copy;
}
    public LinkedList<String> removeDuplicates(LinkedList<String> duplicateList) {
        Log.w("removeDuplicates()", "bo");
        System.out.println("duplicateList.toString() "  + duplicateList.toString());
        LinkedList<String> finalList = new LinkedList<>();
        LinkedList<String> copy = new LinkedList<>();
        copy = duplicateList;

        for(int i = 0; i < duplicateList.size(); i++){
            String curr = duplicateList.get(i);
            System.out.println("curr " + curr + " i = " + i);
            String[] splitCurr = curr.split(" ");

            String currItem = splitCurr[0];
            String currSeller = splitCurr[1];
            String currPrice = splitCurr[2];

            System.out.println("        currItem = " + currItem + " , currSeller = " + currSeller + " , " +
                    "currPrice = " + currPrice);

            for(int j = i+1; j < duplicateList.size(); j++){
                String currNext = duplicateList.get(j);
                System.out.println("    currNext " + currNext + " j = " + j);

                String[] splitCurrNext = currNext.split(" ");
                String currNextItem = splitCurrNext[0];
                String currNextSeller = splitCurrNext[1];
                String currNextPrice = splitCurrNext[2];

                System.out.println("        currNextItem = " + currNextItem + " , currNextSeller = " + currNextSeller + " , " +
                        "currNextPrice = " + currNextPrice);

                if(currItem.equalsIgnoreCase(currNextItem)){
                    System.out.println("    curr == currNext");

                    double a = Double.parseDouble(currPrice);
                    double b = Double.parseDouble(currNextPrice);

                    // distributor a sells cheaper than distributor b
                    // {apples 1.99, apples 2.99, apples, .99}
                    // First iteration copy = {apples 1.99, apples, .99} , remove [apples 2.99] 1th location
                    // Second iteration finalList = {apples .99} , remove [apples 1.99] 0th location
                    if(currItem.equalsIgnoreCase(currNextItem) && (a < b)){
                        System.out.println("    Distributor A sells cheaper than Distributor B. ");
                        // remove b
                        System.out.println("        Before removal the copy list is " + copy.toString());
                        System.out.println("            Remove current.next element = " + currNext);
                        copy.remove(currNext);
                        System.out.println("                After removal the copy list is " + copy.toString());
                    }
                    // {apples 2.99, apples 1.99, apples, .99}
                    // First iteration finalList = {apples 1.99} , remove [apples 2.99] 0th location
                    // Second iteration finalList = {apples .99} , remove [apples 1.99] 1th location
                    else if(currItem.equalsIgnoreCase(currNextItem) && (b < a)){
                        System.out.println("    Distributor B sells cheaper than Distributor A. ");
                        System.out.println("        Before removal the copy list is " + copy.toString());
                        System.out.println("            Remove current element = " + curr);
                        copy.remove(curr);
                        System.out.println("                After removal the copy list is " + copy.toString());
                    }
                }
                else{
                    System.out.println("curr != currNext");

                }
            }
        }
        System.out.println("duplicateList.toString() "  + duplicateList.toString());
        System.out.println(" copy " + copy.toString());
        return copy;
    }

    /* Parse Active Orders */
    public void parseActiveOrders(JSONObject jsonObject){

        try {
            String currentRow = String.valueOf(jsonObject);
            String item = String.valueOf(jsonObject.get("item"));
            String distributor = String.valueOf(jsonObject.get("distributor"));
            String price = String.valueOf(jsonObject.get("price"));
            String qty = String.valueOf(jsonObject.get("qty"));
            String place_order_time = String.valueOf(jsonObject.get("place_order_time"));
            String ticket_number = String.valueOf(jsonObject.get("ticket_number"));
            String status = String.valueOf(jsonObject.get("order_status"));
            String who_placed_order = String.valueOf(jsonObject.get("who_placed_order"));

            Log.w("parseActiveOrders", currentRow);

            Log.w("item", item);
            Log.w("distributor", distributor);
            Log.w("price", price);
            Log.w("qty", qty);
            Log.w("place_order_time", place_order_time);
            Log.w("ticket_number", ticket_number);
            Log.w("order_status", status);
            Log.w("who_placed_order", who_placed_order);

            item = item.replace(' ', '-');
            distributor = distributor.replace(' ', '-');
            price = price.replace(' ', '-');
            qty = qty.replace(' ', '-');
            place_order_time = place_order_time.replace(' ', '-');
            ticket_number = ticket_number.replace(' ', '-');
            status = status.replace(' ', '-');
            who_placed_order = who_placed_order.replace(' ', '-');

            if (who_placed_order.equalsIgnoreCase("Pho-Tre-Bien") && !tempCounter.contains(ticket_number)) {
                countNumberOfOrders++;
                tempCounter.add(ticket_number);
                double total = 49.99;
                addOrderToActiveOrders(ticket_number + " " + place_order_time + " " + status + " " + total + " " );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setNumberOfOrders(countNumberOfOrders);
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

    private void setLowStockItems(String row) {
        if(!lowStockItems.contains(row)){ // If the list does not contain the item then don't add it
            lowStockItems.add(row);
        }
    }

    public LinkedList<String> allItemsFromLowStock() {
        return lowStockItems;
    }

/* GETTERS AND SETTERS FOR INVENTORY */
    private void addItemToInventoryTable(String id, String item, String category, String curr_qty,
                                                      String max_qty, String last_time_updated){
        if(!inventoryTable.contains(id + " " + item + " " + category + " " + curr_qty + " " + max_qty + " " +
                last_time_updated)){
            inventoryTable.add(id + " " + item + " " + category + " " + curr_qty + " " + max_qty + " " +
                    last_time_updated);
        }
    }

    public LinkedList<String> getInventoryTable() {
        System.out.println("    weeklyCountTable4" + weeklyCountTable.toString());
        if(!weeklyCountTable.isEmpty()){

        }
        return inventoryTable;
    }
    public LinkedList<String> getWeeklyTable() {
        System.out.println("    weeklyCountTable4" + weeklyCountTable.toString());
        return weeklyCountTable;
    }

    private void setInventoryTable(LinkedList<String> temp){
        this.inventoryTable = temp;
    }


/* GETTERS AND SETTERS FOR WEEKLY COUNT */
    public void addItemToInventoryCountTable(String id, String item, String category, String curr_qty,
                                         String max_qty, String last_time_updated) {

        System.out.println("addItemToInventoryCountTable");
        System.out.println("    weeklyCountTable" + weeklyCountTable.toString());
        System.out.println("    inventoryTable" + inventoryTable.toString());
        System.out.println("    id " + id);
        System.out.println("    item " + item);
        System.out.println("    category " + category);
        System.out.println("    curr_qty " + curr_qty);
        System.out.println("    max_qty " + max_qty);
        System.out.println("    time " + last_time_updated);

        if (!weeklyCountTable.contains(id + " " + item + " " + category + " " + curr_qty + " " + max_qty)) {
            System.out.println("WEEKLY COUNT TABLE DOES NOT CONTAIN ITEM " + item + " therefore add " + item);
            weeklyCountTable.add(id + " " + item + " " + category + " " + curr_qty + " " + max_qty);
            System.out.println("    weeklyCountTable" + weeklyCountTable.toString());
        }
    }

    public void updateInventoryCount(String id, String item, String cat, String curr_qty, String max_qty, String
            copyTime){
        for(int i = 0 ; i < weeklyCountTable.size(); i++){
            System.out.println("Weekly Count Table " + weeklyCountTable.get(i));
            String[] currRow = weeklyCountTable.get(i).replace("[", "").replace("]", "").replace(",", "").split
                    (" ");

            String idCurrRow  = currRow[0];
            String itemCurrRow  = currRow[1];
            String catCurrRow  = currRow[2];
            String curr_qtyCurrRow  = currRow[3];
            String max_qtyCurrRow  = currRow[4];

            if(id.equals(idCurrRow)) { // We found the qty we need to update
                System.out.println("We found the qty we need to update");
                weeklyCountTable.remove(i);
                String newRow = id + " " + item + " " + cat + " " + curr_qty + " " + max_qty;
                weeklyCountTable.add(newRow);
            }
        }


    }
    public LinkedList<String> getWeeklyCountTable(){
        return weeklyCountTable;
    }



/* GETTERS AND SETTERS FOR PRICES */
    private void addPriceToLams(String item) {
        if(!pcLamsPriceList.contains(item)){ // If the list does not contain the item then don't add it
            pcLamsPriceList.add(item);
        }
    }
    private void addPriceToSams(String item) {
        if(!pcSamsPriceList.contains(item)){ // If the list does not contain the item then don't add it
            pcSamsPriceList.add(item);
        }
    }
    private void addPriceToTaiwanTrading(String item) {
        if(!pcTaiwanTradingPriceList.contains(item)){ // If the list does not contain the item then don't add it
            pcTaiwanTradingPriceList.add(item);
        }
    }
    private void addPriceToRestaurantDepot(String item) {
        if(!pcRestaurantDepotPriceList.contains(item)){ // If the list does not contain the item then don't add it
            pcRestaurantDepotPriceList.add(item);
        }
    }
    private void addPriceToShamrock(String item) {
        if(!pcShamrockPriceList.contains(item)){ // If the list does not contain the item then don't add it
            pcShamrockPriceList.add(item);
        }
    }
    private void addPriceToCosco(String item) {
        if(!pcCoscoPriceList.contains(item)){ // If the list does not contain the item then don't add it
            pcCoscoPriceList.add(item);
        }
    }
    private void addPriceToFoodKing(String item) {
        if(!pcFoodOfKingPriceList.contains(item)){ // If the list does not contain the item then don't add it
            pcFoodOfKingPriceList.add(item);
        }
    }
    public LinkedList<String> getAllPrices(){
        LinkedList<String> concatenateLists = new LinkedList<>();

        concatenateLists.addAll(pcLamsPriceList);
        concatenateLists.addAll(pcSamsPriceList);
        concatenateLists.addAll(pcTaiwanTradingPriceList);
        concatenateLists.addAll(pcRestaurantDepotPriceList);
        concatenateLists.addAll(pcShamrockPriceList);
        concatenateLists.addAll(pcCoscoPriceList);
        concatenateLists.addAll(pcFoodOfKingPriceList);

        return concatenateLists;
    }

/* GETTERS AND SETTERS FOR ACTIVE ORDERS */
    private void addOrderToActiveOrders(String item){
        System.out.println("SUNDAY"  + phoTreBienActiveOrders.toString());
        if (!phoTreBienActiveOrders.contains(item)) {
            phoTreBienActiveOrders.add(item);
        }
    }
    public LinkedList<String> getAllActiveOrders(){
        LinkedList<String> concatenateLists = new LinkedList<>();
        concatenateLists.addAll(phoTreBienActiveOrders);
        return concatenateLists;
    }
    private void setNumberOfOrders(int n){
        this.numOfOrders = n;
    }
    public int getNumberOfOrders(){
        return numOfOrders;
    }
}
