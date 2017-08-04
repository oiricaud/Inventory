package Communication;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by oscarricaud on 7/19/17.
 */
public class InventoryRequest extends StringRequest {

    // This line connects to my domain. Please @see Register class for more details
    private static final String REGISTER_REQUEST_URL = "http://www.narped.com/inventory/Inventory.php";
    private Map<String, String> params;

    /**
     * This method sends a POST request to the database.
     *
     * @param item        The item the user is adding to the inventory database
     * @param category    The item's category
     * @param curr_qty    The current quantity Pho Tre Bien has.
     * @param max_qty     The max quantity Pho Tre Bien orders.
     * @param distributor The distributor where the item is coming from
     * @param price       Price of an item
     * @param listener    The listener listens to the responses from the user.
     */

    public InventoryRequest(String item, String category, String curr_qty, String max_qty, String distributor,
                            String price, Response.Listener<String> listener) {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("item", item);
        params.put("category", category);
        params.put("curr_qty", curr_qty);
        params.put("max_qty", max_qty);
        params.put("distributor", distributor);
        params.put("price", price);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
