package model;

import java.util.LinkedList;

/**
 * Created by oscarricaud on 8/24/17.
 */
public class Employees {

    private String type;
    private String fullName;
    private String userId;
    private LinkedList<String> employeeList = new LinkedList<>();

    public void addEmployee(String employeeType, String name, String userId){
        employeeList.add(employeeType + " " + name + " " + userId);

    }

    public String getType(String type) {

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
