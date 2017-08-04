package Communication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by oscarricaud on 4/10/17.
 */
public class RegisterRequest extends StringRequest {

    // This line connects to my domain. Please @see Register class for more details
    private static final String REGISTER_REQUEST_URL = "http://www.narped.com/inventory/Register.php";
    private Map<String, String> params;

    /**
     * This method sends a POST request to the database.
     *
     * @param listener The listener listens to the responses from the user.
     */

    public RegisterRequest(String fullname, String user_name, String user_password, String user_type, Response
            .Listener<String>
            listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("fullname", fullname);
        params.put("user_name", user_name);
        params.put("user_password", user_password);
        params.put("user_type", user_type);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}