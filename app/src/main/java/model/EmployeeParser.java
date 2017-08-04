package model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by oscarricaud on 7/30/17.
 */
/* AKA Parse */
public class EmployeeParser {
    private LinkedList<String> employees = new LinkedList<>();

    public EmployeeParser(JSONObject jsonObject) {
        try {

            String fullName = String.valueOf(jsonObject.get("fullname"));
            String username = String.valueOf(jsonObject.get("username"));
            String userType = String.valueOf(jsonObject.get("usertype"));

            /* If you remove the next 4 lines of code then it will mess up the table in the inventory view */
            fullName = fullName.replace(' ', '-');
            username = username.replace(' ', '-');
            userType = userType.replace(' ', '-');


            employees.add(fullName);
            employees.add(username);
            employees.add(userType);

            setEmployees(employees);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setEmployees(LinkedList<String> employee) {
        this.employees = employees;
    }

    public LinkedList<String> allEmployees() {
        return employees;
    }

}
