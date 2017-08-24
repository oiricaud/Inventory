package communication;

import java.util.LinkedList;

/**
 * Created by oscarricaud on 8/24/17.
 */
public class Order {
    private LinkedList<String> ticketNumber = new LinkedList<>();
    private LinkedList<String> listOfItems = new LinkedList<>();

    public void Order(){

    }
    public void setOrder(String ticket, String itemList){
        ticketNumber.add(ticket);
        listOfItems.add(itemList);
    }
    public String getOrder(String ticket){
        for(int i = 0 ; i < ticketNumber.size(); i++){
            if(ticket.equalsIgnoreCase(ticketNumber.get(i))){
                System.out.println("We found the item list for ticket " + ticket + " at position " + i);
                return listOfItems.get(i);
            }
        }

        return null;
    }
}
