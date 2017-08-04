package model;

import java.util.LinkedList;

/**
 * Created by oscarricaud on 7/26/17.
 */
public class Distributors {

    /* Distributors */
    private LinkedList<String> lamsItemList = new LinkedList<>();
    private LinkedList<String> samsItemList = new LinkedList<>();
    private LinkedList<String> taiwanTradingItemList = new LinkedList<>();
    private LinkedList<String> restaurantDepotItemList = new LinkedList<>();
    private LinkedList<String> shamrockItemList = new LinkedList<>();
    private LinkedList<String> coscoItemList = new LinkedList<>();
    private LinkedList<String> foodOfKingItemList = new LinkedList<>();

    public void Distributors(){
    }

    /* Food Of King */
    public LinkedList<String> getFoodOfKingItemListItemList() {
        if(foodOfKingItemList.isEmpty()){
            System.out.println("Food Of King List is empty");
        }
        return foodOfKingItemList;
    }

    public void addItemToFoodOfKingItemList(String item) {
        if(!foodOfKingItemList.contains(item)){ // If the list does not contain the item then don't add it
            foodOfKingItemList.add(item);
        }
    }

    /* Cosco */
    public LinkedList<String> getCoscoItemsList() {
        if(coscoItemList.isEmpty()){
            System.out.println("Shamrock List is empty");
        }
        return coscoItemList;
    }

    public void addItemToCoscoItemList(String item) {
        if(!coscoItemList.contains(item)){ // If the list does not contain the item then don't add it
            coscoItemList.add(item);
        }
    }

    /* Shamrock */
    public LinkedList<String> getShamrockItemsList() {
        if(shamrockItemList.isEmpty()){
            System.out.println("Shamrock List is empty");
        }
        return shamrockItemList;
    }

    public void addItemToShamrockItemList(String item) {
        if(!shamrockItemList.contains(item)){ // If the list does not contain the item then don't add it
            shamrockItemList.add(item);
        }
    }

    /* Restaurant Depot */
    public LinkedList<String> getRestaurantDepotItemsList() {
        if(restaurantDepotItemList.isEmpty()){
            System.out.println("Taiwan Trading Item List is empty");
        }
        return restaurantDepotItemList;
    }

    public void addItemToRestaurantDepotItemList(String item) {
        if(!restaurantDepotItemList.contains(item)){ // If the list does not contain the item then don't add it
            restaurantDepotItemList.add(item);
        }
    }

    /* Taiwan Trading */
    public LinkedList<String> getTaiwanTradingItemsList() {
        if(taiwanTradingItemList.isEmpty()){
            System.out.println("Taiwan Trading Item List is empty");
        }
        return taiwanTradingItemList;
    }

    public void addItemToTaiwanTradingItemList(String item) {
        if(!taiwanTradingItemList.contains(item)){ // If the list does not contain the item then don't add it
            taiwanTradingItemList.add(item);
        }
    }

    /* SAMS */
    public LinkedList<String> getSamsItemsList() {
        if(samsItemList.isEmpty()){
            System.out.println("Sams Item List is empty");
        }
        return samsItemList;
    }

    public void addItemToSamsList(String item) {
        if(!samsItemList.contains(item)){ // If the list does not contain the item then don't add it
            samsItemList.add(item);
        }
    }

    /* LAMS */
    public LinkedList<String> getLamsItemsList() {
        if(lamsItemList.isEmpty()){
            System.out.println("Lams Item List is empty");
        }
        return lamsItemList;
    }

    public void addItemToLamsList(String item) {
        if(!lamsItemList.contains(item)){ // If the list does not contain the item then don't add it
            lamsItemList.add(item);
        }
    }
    public LinkedList<String> getAllItems() {
        LinkedList<String> concatenateLists = new LinkedList<>();

        concatenateLists.addAll(lamsItemList);
        concatenateLists.addAll(samsItemList);
        concatenateLists.addAll(taiwanTradingItemList);
        concatenateLists.addAll(restaurantDepotItemList);
        concatenateLists.addAll(shamrockItemList);
        concatenateLists.addAll(coscoItemList);
        concatenateLists.addAll(foodOfKingItemList);

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
