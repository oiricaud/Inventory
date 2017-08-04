package model;

import java.util.LinkedList;

/**
 * Created by oscarricaud on 7/31/17.
 */
public class Prices {

    private LinkedList<String> prices = new LinkedList<>();

    /* LAMS */
    public LinkedList<String> getPricesItemsList() {
        if(prices.isEmpty()){
            System.out.println("Prices List is empty");
        }
        return prices;
    }

    public void addPricesToList(String item) {
        if(!prices.contains(item)){ // If the list does not contain the item then don't add it
            prices.add(item);
        }
    }
}
