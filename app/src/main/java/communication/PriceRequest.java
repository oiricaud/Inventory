package communication;

/**
 * Created by oscarricaud on 8/6/17.
 */

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PriceRequest extends StringRequest {

    // This line connects to my domain. Please @see Register class for more details
    private static final String REGISTER_REQUEST_URL = "http://www.narped.com/inventory/Setprices.php";
    private Map<String, String> params;

    /**
     * This method sends a POST request to the database.
     *
     * @param item        The item the user is adding to the inventory database
     * @param pricePerQty    The item's category
     * @param seller    The current quantity Pho Tre Bien has.
     * @param listener    The listener listens to the responses from the user.
     */

    public PriceRequest(String item, String pricePerQty, String seller, Response.Listener<String> listener) {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("item", item);
        params.put("pricePerQty", pricePerQty);
        params.put("seller", seller);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
