package communication;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oscarricaud on 8/11/17.
 */
public class OrderRequest extends StringRequest {  // This line connects to my domain. Please @see Register class for more
    // details
    private static final String REGISTER_REQUEST_URL = "http://www.narped.com/inventory/Order.php";
    private Map<String, String> params;

    /**
     * This method sends a POST request to the database.
     * @param listener    The listener listens to the responses from the user.
     */

    public OrderRequest(String item, String distributor, String price, String qty, String currDate, int receipt_number,  Response.Listener<String> listener) {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("item", item);
        params.put("distributor", distributor);
        params.put("price", price);
        params.put("qty", qty);
        params.put("place_order_time", currDate);
        params.put("ticket_number", String.valueOf(receipt_number));
        params.put("order_status", "active");
        params.put("who_placed_order", "Pho Tre Bien");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

