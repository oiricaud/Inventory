package model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by oscarricaud on 7/22/17.
 */
/* AKA Parse */
public class Parser {
    private LinkedList<String> items = new LinkedList<>();
    private LinkedList<String> lowStockItems = new LinkedList<>();

    public Parser(JSONObject jsonObject) {

        try {
            //Log.w("alpha", String.valueOf(jsonObject.get("item")));
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


            if((ratio) <= .2){ // Low Stock
                lowStockItems.add(item);
                lowStockItems.add(distributor);
                lowStockItems.add(price);
                lowStockItems.add(curr_qty);
                setLowStockItems(lowStockItems);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void setLowStockItems(LinkedList<String> lowStocks) {
        this.lowStockItems = lowStocks;
    }

    public LinkedList<String> allItemsFromLowStock() {
        return lowStockItems;
    }

    private void setItems(LinkedList<String> items) {
        this.items = items;
    }

    public LinkedList<String> allItems() {
        return items;
    }

}
