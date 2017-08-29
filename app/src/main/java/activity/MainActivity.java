package activity;

import adapter.ImageAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.*;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import communication.*;
import inc.pheude.inventory.R;
import model.Parser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 *
 * Created by oscarricaud on 7/19/17.
 */
public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private LinkedList<String> currListView = new LinkedList<>();
    private JSONParser jParser = new JSONParser();
    private String TAG_SUCCESS = "success";
    private String TAG_STUFF = "stuff";
    final int sdk = android.os.Build.VERSION.SDK_INT;

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private RecyclerView mRecyclerView;

    private GridView menuGridView;

    private LinearLayout placeOrderColumns;
    private LinearLayout inventoryColumns;
    private LinearLayout inventoryCountColumns;
    private LinearLayout pricesColumns;
    private LinearLayout activeOrdersColumns;

    private TableLayout employeesTable;
    private TableLayout sectionsTable;
    private TableLayout inventoryTable;
    private TableLayout inventoryCountTable;
    private TableLayout pricesTable;
    private TableLayout placeOrderTable;
    private TableLayout activeOrdersTable;
    private TableLayout activeOrdersTablePopUp;
    private RelativeLayout swipeRefresh;
    private LinearLayout menuView;
    private RelativeLayout employeesView;
    private RelativeLayout sectionsView;
    private RelativeLayout inventoryView;
    private RelativeLayout inventoryCountView;
    private RelativeLayout pricesView;
    private RelativeLayout placeOrderView;
    private RelativeLayout activeOrdersView;

    private RelativeLayout addItem;
    private RelativeLayout addPrice;

    private RelativeLayout foodItemPopUp;
    private RelativeLayout placeOrderPopUp;
    private RelativeLayout activeOrdersPopUp;
    private TextView titleClicked;
    private LinearLayout loading;
    private InputStream is=null;
    private String result=null;
    private String line=null;
    private int code;

    private Parser parser = new Parser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loading = (LinearLayout) findViewById(R.id.layout_progressbar);
        loading.setVisibility(View.GONE);
        TextView createAccount = (TextView) findViewById(R.id.link_signup);
        Button login = (Button) findViewById(R.id.btn_login);
        changeBackgroundColor();

        // If user decides to create an account, change view
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
        // If the user decides to log in
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }

    private void changeBackgroundColor() {
        // Random number to obtain random background
        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((6 - 0) + 1) + 0;


        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        if(randomNum == 0){
            linearLayout.setBackgroundDrawable( getResources().getDrawable(R.drawable.deepsea));
        }
        if(randomNum == 1){
            linearLayout.setBackgroundDrawable( getResources().getDrawable(R.drawable.dusk));
        }
        if(randomNum == 2){
            linearLayout.setBackgroundDrawable( getResources().getDrawable(R.drawable.predawn));
        }
        if(randomNum == 3){
            linearLayout.setBackgroundDrawable( getResources().getDrawable(R.drawable.starfall));
        }
        if(randomNum == 4){
            linearLayout.setBackgroundDrawable( getResources().getDrawable(R.drawable.sunset));
        }
        if(randomNum == 5){
            linearLayout.setBackgroundDrawable( getResources().getDrawable(R.drawable.velvetsun));
        }
    }

    /* User Log In */
    /**
     * This method verifies the users credentials and if they are correct, it launches the home menu.
     */
    private void userLogin() {

        final EditText etUserName = (EditText) findViewById(R.id.input_user_name);
        final EditText etPassword = (EditText) findViewById(R.id.input_password);
        final String username = etUserName.getText().toString();
        final String password = etPassword.getText().toString();
        loading.setVisibility(View.VISIBLE);



        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.w("jsonResponse", jsonResponse.toString());

                    boolean success = jsonResponse.getBoolean("success");

                    if (success && (!isEmpty(etUserName) || !isEmpty(etPassword))) {
                        toast("Success logon!", 5000);
                        setContentView(R.layout.activity_main);
                        launchHomeView();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        //hideLoadingCircle();
                        toast("Failed, please try again", 5000);
                        builder.setMessage("Incorrect Username or Password try again").setNegativeButton("Retry", null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        LoginRequest loginRequest = new LoginRequest(username, password, responseListener);

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(loginRequest);
    }

    /* Create Account */
    /**
     * Create Account
     * This method waits for the user to to type in their information then creates an account and stores it to my
     * database.
     */
    private void createAccount() {
        setContentView(R.layout.activity_sign_up);
        /* Prepare data */
        final EditText etUserName = (EditText) findViewById(R.id.user_name);
        final EditText etUserId = (EditText) findViewById(R.id.user_id);
        final EditText etPassword = (EditText) findViewById(R.id.input_password);
        final Spinner etUserType = (Spinner) findViewById(R.id.user_type);
        final Button btn_signup = (Button) findViewById(R.id.btn_signup);
        final TextView link_login = (TextView) findViewById(R.id.link_login);
        String[] employeeType = {"Manager", "Cook", "Front Of House", "Back Of House", "Kitchen", "Bar"};

        ArrayAdapter<String> adapterForSections = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, employeeType);
        etUserType.setAdapter(adapterForSections);

        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartActivity();
            }
        });

        // User presses the "No account yet? Create one"
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String employeename = etUserName.getText().toString();
                final String username = etUserId.getText().toString();
                final String usertype = etUserType.getSelectedItem().toString();
                final String password = etPassword.getText().toString();

                link_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        launchHomeView();
                    }
                });
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                toast("Success creating account", 5000);
                                restartActivity();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                toast("Error, please try again", 5000);
                                Log.w("Employees Name:", employeename);
                                Log.w("User Name:", username);
                                Log.w("Password:", password);
                                Log.w("User Type:", usertype);

                                builder.setMessage("Register Failed").setNegativeButton("Retry", null).create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // The next 3 lines calls the @see RegisterRequest class.
                RegisterRequest registerRequest = new RegisterRequest(employeename, username, password, usertype,
                        responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(registerRequest);
            }
        });
    }

/** Side Bar **/
    @Override
    public void onDrawerItemSelected(View view, int position) {
        switch (position) {
            case 0:  // Home Sweet Home
                menuView.setVisibility(LinearLayout.VISIBLE);
                employeesView.setVisibility(RelativeLayout.GONE);
                sectionsView.setVisibility(RelativeLayout.GONE);
                inventoryView.setVisibility(RelativeLayout.GONE);
                inventoryCountView.setVisibility(RelativeLayout.GONE);
                pricesView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);
                activeOrdersView.setVisibility(RelativeLayout.INVISIBLE);
                drawerFragment.closeDrawers();
                launchHomeView();
                break;
            case 1:  // Employees
                menuView.setVisibility(LinearLayout.GONE);
                sectionsView.setVisibility(RelativeLayout.GONE);
                inventoryView.setVisibility(RelativeLayout.GONE);
                inventoryCountView.setVisibility(RelativeLayout.GONE);
                pricesView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersView.setVisibility(RelativeLayout.INVISIBLE);
                drawerFragment.closeDrawers();
                launchEmployeesView();
                break;

            case 2:  // Sections
                menuView.setVisibility(LinearLayout.GONE);
                employeesView.setVisibility(RelativeLayout.GONE);
                inventoryView.setVisibility(RelativeLayout.GONE);
                inventoryCountView.setVisibility(RelativeLayout.GONE);
                pricesView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersView.setVisibility(RelativeLayout.INVISIBLE);
                drawerFragment.closeDrawers();
                launchSectionsView();
                break;
            case 3:  // Inventory
                menuView.setVisibility(LinearLayout.GONE);
                employeesView.setVisibility(RelativeLayout.GONE);
                pricesView.setVisibility(RelativeLayout.GONE);
                inventoryCountView.setVisibility(RelativeLayout.GONE);
                sectionsView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersView.setVisibility(RelativeLayout.INVISIBLE);
                drawerFragment.closeDrawers();
                launchInventoryView();
                break;
            case 4:  // Inventory Count
                menuView.setVisibility(LinearLayout.GONE);
                inventoryView.setVisibility(RelativeLayout.GONE);
                employeesView.setVisibility(RelativeLayout.GONE);
                pricesView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersView.setVisibility(RelativeLayout.INVISIBLE);
                drawerFragment.closeDrawers();
                launchWeeklyCountView();
                break;
            case 5:  // View Prices
                menuView.setVisibility(LinearLayout.GONE);
                employeesView.setVisibility(RelativeLayout.GONE);
                inventoryCountView.setVisibility(RelativeLayout.GONE);
                inventoryView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersView.setVisibility(RelativeLayout.INVISIBLE);
                drawerFragment.closeDrawers();
                launchPricesView();
                break;
            case 6:  // Place Order
                placeOrderView.setVisibility(RelativeLayout.VISIBLE);

                menuView.setVisibility(LinearLayout.GONE);
                employeesView.setVisibility(RelativeLayout.GONE);
                inventoryCountView.setVisibility(RelativeLayout.GONE);
                sectionsView.setVisibility(LinearLayout.GONE);
                inventoryView.setVisibility(RelativeLayout.GONE);
                pricesView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersView.setVisibility(RelativeLayout.INVISIBLE);
                drawerFragment.closeDrawers();
                launchPlaceOrderView();
                break;

            case 7: // Active Orders
                mToolbar.setTitle("Active Orders");
                activeOrdersView.setVisibility(RelativeLayout.VISIBLE);

                menuView.setVisibility(LinearLayout.GONE);
                employeesView.setVisibility(RelativeLayout.GONE);
                inventoryCountView.setVisibility(RelativeLayout.GONE);
                sectionsView.setVisibility(LinearLayout.GONE);
                inventoryView.setVisibility(RelativeLayout.GONE);
                pricesView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);
                activeOrdersPopUp.setVisibility(RelativeLayout.GONE);
                drawerFragment.closeDrawers();
                launchActiveOrders();
                break;
            case 8:  // Log Out
                Log.w("Log out ", "Logging out");
                restartActivity();
                break;
        }
    }

/** Toolbar **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.w("MTOOLBAR GET TITLE", mToolbar.getTitle().toString());
    // Inflate the menu; this adds items to the action bar if it is present.
    if(mToolbar.getTitle().equals("Pho Tre Bien")){ // Display the settings icon
        getMenuInflater().inflate(R.menu.home_menu, menu);
    }
    if(mToolbar.getTitle().equals("Employees - All Employees")){ // Display the settings icon
        getMenuInflater().inflate(R.menu.employees_menu, menu);
        getMenuInflater().inflate(R.menu.prices_menu, menu); // Search Icon
    }
    if(mToolbar.getTitle().equals("Restaurant - All Sections")){ // Display the settings icon
        getMenuInflater().inflate(R.menu.category_menu_main, menu);
        getMenuInflater().inflate(R.menu.prices_menu, menu); // Search Icon
    }
    if(mToolbar.getTitle().equals("Inventory - All Inventory")){ // Display the settings icon
        getMenuInflater().inflate(R.menu.prices_menu, menu); // Search Icon
        getMenuInflater().inflate(R.menu.category_menu_add, menu); // Add Icon
        getMenuInflater().inflate(R.menu.category_menu_remove, menu); // Remove Icon
    }
    if(mToolbar.getTitle().equals("Weekly Count")){ // Display the settings icon
        getMenuInflater().inflate(R.menu.prices_menu, menu); // Search Icon
        getMenuInflater().inflate(R.menu.category_menu_main, menu); // ...
    }
    if(mToolbar.getTitle().equals("Prices - All Prices from sellers")){
        getMenuInflater().inflate(R.menu.prices_menu, menu); // Search Icon
        getMenuInflater().inflate(R.menu.menu_main, menu); // ...
        getMenuInflater().inflate(R.menu.category_menu_add, menu); // Add Icon
        getMenuInflater().inflate(R.menu.category_menu_remove, menu); // Remove Icon
    }
    return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.w("ITEM GET TITLE", item.getTitle().toString());
        Log.w("MTOOLBAR GET TITLE", mToolbar.getTitle().toString());

/* EMPLOYEES DROP DOWN */
        if (item.getTitle().equals("EP: Front Of House")) {
            mToolbar.setTitle("Employees - Front of House");
            currListView = parser.getEmployeeFrontOfHouse();
            updateEmployeesView();
        }
        if (item.getTitle().equals("EP: Back Of House")) {
            mToolbar.setTitle("Employees - Back of House");
            currListView = parser.getEmployeeBackOfHouse();
            updateEmployeesView();
        }
        if (item.getTitle().equals("EP: Kitchen")) {
            mToolbar.setTitle("Employees - Kitchen");
            currListView = parser.getEmployeeKitchen();
            updateEmployeesView();
        }
        if (item.getTitle().equals("EP: Bar")) {
            mToolbar.setTitle("Employees - Bar");
            currListView = parser.getEmployeeBar();
            updateEmployeesView();
        }
        if (item.getTitle().equals("All Employees")) {
            mToolbar.setTitle("Employees - All Employees");
            currListView = parser.getAllEmployee();
            updateEmployeesView();
        }
/* INVENTORY DROP DOWN */
        if (item.getTitle().equals("Add")) {
            launchAddItemView();
            launchAddPricesView();
        }
        if (item.getTitle().equals("Remove")) {
            launchRemoveItemView();
        }

/* SECTIONS DROP DOWN */
        if (item.getTitle().equals("Front Of House")) {
            mToolbar.setTitle("Restaurant - Front of House");
            currListView = parser.getFrontOfHouse();
            updateSectionsView();
        }
        if (item.getTitle().equals("Back of House")) {
            mToolbar.setTitle("Restaurant - Back of House");
            currListView = parser.getBackOfHouse();
            updateSectionsView();
        }
        if (item.getTitle().equals("Kitchen")) {
            mToolbar.setTitle("Restaurant - Kitchen");
            currListView = parser.getKitchen();
            updateSectionsView();
        }
        if (item.getTitle().equals("Bar")) {
            mToolbar.setTitle("Restaurant - Bar");
            currListView = parser.getBar();
            updateSectionsView();
        }
        if (item.getTitle().equals("All Sections")) {
            mToolbar.setTitle("Restaurant - All Sections");
            currListView = parser.getAllItems();
            updateSectionsView();
        }

/* Prices DROP DOWN */
        if (item.getTitle().equals("Lams")) {
            mToolbar.setTitle("Inventory - Lams Items");
            currListView = parser.getLamsItemsList();
            updatePricesView();
        }
        if (item.getTitle().equals("Sams")) {
            mToolbar.setTitle("Inventory - Sams Items");
            currListView = parser.getSamsItemsList();
            updatePricesView();
        }
        if (item.getTitle().equals("Taiwan Trading")) {
            mToolbar.setTitle("Prices - Taiwan Trading Items");
            currListView = parser.getTaiwanTradingItemsList();
            updatePricesView();
        }
        if (item.getTitle().equals("Restaurant Depot")) {
            mToolbar.setTitle("Prices - Restaurant Depot Items");
            currListView = parser.getRestaurantDepotItemsList();
            updatePricesView();
        }
        if (item.getTitle().equals("Shamrock")) {
            mToolbar.setTitle("Prices - Shamrock Items");
            currListView = parser.getShamrockItemsList();
            updatePricesView();
        }
        if (item.getTitle().equals("Cosco")){
            mToolbar.setTitle("Prices - Cosco Items");
            currListView = parser.getCoscoItemsList();
            updatePricesView();
        }
        if (item.getTitle().equals("Food King")) {
            mToolbar.setTitle("Prices - Food Of King Items");
            currListView = parser.getFoodOfKingItemListItemList();
            updatePricesView();
        }
        if (item.getTitle().equals("All Items")) {
            mToolbar.setTitle("Prices - All Items");
            currListView = parser.getAllPrices();
            updatePricesView();
        }

        return super.onOptionsItemSelected(item);
    }

/* Home View **/
    private void launchHomeView() {
        final int[] imageId = {
                R.drawable.pork_rolled_beef, R.drawable.rare_steak_brisket, R.drawable.shrimp_spring_rolls, R.drawable.summer_rolls,
                R.drawable.vietnamese_eggrolls, R.drawable.winter_rolls, R.drawable.calamari, R.drawable.four_seasons_rolls,
                R.drawable.grilled_pork_rolls,
        };
        final String[] imageTitle = {
                "Pork Rolled Beef", "Rare Steak Brisket", "Shrimp Spring Rolls", "Summer Rolls", "Vietnamese Egg Rolls",
                "Winter Rolls", "Calamari", "Four Seasons Rolls", "Grilled Pork Rolls",
        };
        final String[] imageDetails = {
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
        };

        final FloatingActionButton closePopUp = (FloatingActionButton) findViewById(R.id.closePopup);
        final TextView details = (TextView) findViewById(R.id.details);
        final ImageView imageIcon = (ImageView) findViewById(R.id.imageIcon);
        final Button confirm = (Button) findViewById(R.id.user_add_item);

        loading = (LinearLayout) findViewById(R.id.layout_progressbar);


        swipeRefresh = (RelativeLayout) findViewById(R.id.swipe_refresh);

        /* Pop Ups */
        foodItemPopUp = (RelativeLayout) findViewById(R.id.food_item_selected_pop_up);
        placeOrderPopUp  = (RelativeLayout) findViewById(R.id.pop_up);
        activeOrdersPopUp = (RelativeLayout) findViewById(R.id.active_orders_pop_up);
        /* Views */
        employeesView = (RelativeLayout) findViewById(R.id.employees_tab);
        sectionsView = (RelativeLayout) findViewById(R.id.sections_tab);
        inventoryView = (RelativeLayout) findViewById(R.id.inventory_tab);
        inventoryCountView = (RelativeLayout) findViewById(R.id.weekly_count_tab);
        pricesView = (RelativeLayout) findViewById(R.id.prices_tab);
        placeOrderView = (RelativeLayout) findViewById(R.id.place_order_tab);
        activeOrdersView = (RelativeLayout) findViewById(R.id.active_orders_tab);


        titleClicked = (TextView) findViewById(R.id.titleClicked);
        addItem = (RelativeLayout) findViewById(R.id.add_item_view);
        addPrice = (RelativeLayout) findViewById(R.id.add_price_view);

        menuGridView = (GridView) findViewById(R.id.grid_view);

        menuView = (LinearLayout) findViewById(R.id.menu_view);
        inventoryColumns = (LinearLayout) findViewById(R.id.inventory_columns);
        inventoryCountColumns =  (LinearLayout) findViewById(R.id.weekly_columns);
        pricesColumns =  (LinearLayout) findViewById(R.id.prices_columns);
        placeOrderColumns = (LinearLayout) findViewById(R.id.place_order_columns);
        activeOrdersColumns = (LinearLayout) findViewById(R.id.active_orders_columns);

        /* Tables */
        employeesTable = (TableLayout) findViewById(R.id.employee_table);
        sectionsTable = (TableLayout) findViewById(R.id.section_table);
        inventoryTable = (TableLayout) findViewById(R.id.inventory_table);
        inventoryCountTable = (TableLayout) findViewById(R.id.weekly_table);
        pricesTable =  (TableLayout) findViewById(R.id.prices_table);
        placeOrderTable = (TableLayout) findViewById(R.id.place_order_table);
        activeOrdersTable = (TableLayout) findViewById(R.id.active_orders_table);
        activeOrdersTablePopUp = (TableLayout) findViewById(R.id.current_orders_table);

        /* Hide Layouts */
        swipeRefresh.setVisibility(RelativeLayout.GONE);

        placeOrderPopUp.setVisibility(RelativeLayout.GONE);
        activeOrdersPopUp.setVisibility(RelativeLayout.GONE);
        foodItemPopUp.setVisibility(RelativeLayout.GONE);

        employeesView.setVisibility(RelativeLayout.GONE);
        sectionsView.setVisibility(RelativeLayout.GONE);
        inventoryView.setVisibility(RelativeLayout.GONE);
        inventoryCountView.setVisibility(RelativeLayout.GONE);
        pricesView.setVisibility(RelativeLayout.GONE);
        placeOrderView.setVisibility(RelativeLayout.GONE);
        activeOrdersView.setVisibility(RelativeLayout.GONE);


        addItem.setVisibility(RelativeLayout.GONE);
        addPrice.setVisibility(RelativeLayout.GONE);

        loading.setVisibility(View.GONE);
        /* Tool Bar */
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        /* Show Layouts */
        menuGridView.setVisibility(GridView.VISIBLE);
        titleClicked.setVisibility(TextView.VISIBLE);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        mToolbar.setTitle("Pho Tre Bien");

        /* Grid Images Listener */
        ImageAdapter adapter = new ImageAdapter(MainActivity.this, imageTitle, imageId);

        menuGridView.setAdapter(adapter);
        menuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                foodItemPopUp.setVisibility(RelativeLayout.VISIBLE);
                menuGridView.setVisibility(GridView.INVISIBLE);
                titleClicked.setText(imageTitle[position]);
                details.setText(imageDetails[position]);
                imageIcon.setBackgroundResource(imageId[position]);
                imageIcon.bringToFront();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm.setVisibility(Button.INVISIBLE);
            }
        });

        closePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodItemPopUp.setVisibility(RelativeLayout.INVISIBLE);
                menuGridView.setVisibility(GridView.VISIBLE);
                titleClicked.setVisibility(TextView.VISIBLE);
                confirm.setVisibility(Button.VISIBLE);
            }
        });
    }


/* Employees View **/
    private void launchEmployeesView() {

        menuGridView.setVisibility(RelativeLayout.INVISIBLE);
        addItem.setVisibility(RelativeLayout.INVISIBLE);
        loading = (LinearLayout) findViewById(R.id.layout_progressbar);

        loading.setVisibility(View.VISIBLE);
        loading.setZ(10);

        inventoryColumns.setVisibility(LinearLayout.VISIBLE);
        employeesTable.setVisibility(LinearLayout.VISIBLE);
        employeesView.setVisibility(RelativeLayout.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mToolbar.setTitle("Employees - All Employees");

        new talkToDataBase().execute();
    }

    /* ----> Employees Table */
    private void updateEmployeesView() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                employeesTable.removeAllViews();

                // get a reference for the TableLayout
                for (String aCurrListView : currListView) {

                    final TableRow row = new TableRow(MainActivity.this);
                    row.setClickable(true);

                    final TextView nameOfEmployee = new TextView(MainActivity.this);
                    TextView userName = new TextView(MainActivity.this);
                    final TextView userType = new TextView(MainActivity.this);

                    String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");
                    Log.w("employees", String.valueOf(currListView.toString()));
                    nameOfEmployee.setText(strArray[0]);
                    userName.setText(strArray[1]);
                    userType.setText(strArray[2]);

                    nameOfEmployee.setTextColor(Color.BLACK);
                    userName.setTextColor(Color.BLACK);
                    userType.setTextColor(Color.BLACK);

                    nameOfEmployee.setTextSize(18);
                    userName.setTextSize(18);
                    userType.setTextSize(18);

                    Typeface roboto = Typeface.createFromAsset(getAssets(),
                            "font/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
                    nameOfEmployee.setTypeface(roboto);
                    userName.setTypeface(roboto);
                    userType.setTypeface(roboto);

                    row.addView(nameOfEmployee);
                    row.addView(userName);
                    row.addView(userType);

                    LinearLayout.LayoutParams paramsNameOfEmployee = (LinearLayout.LayoutParams) nameOfEmployee.getLayoutParams();
                    paramsNameOfEmployee.setMargins(25, 15 , 0, 25);

                    LinearLayout.LayoutParams paramsUserName = (LinearLayout.LayoutParams) nameOfEmployee.getLayoutParams();
                    paramsUserName.setMargins(25, 15 , 0, 25); // Left, Top, Right, Bottom

                    LinearLayout.LayoutParams paramsUserType = (LinearLayout.LayoutParams) nameOfEmployee.getLayoutParams();
                    paramsUserType.setMargins(25, 15 , 0, 25); // Left, Top, Right, Bottom

                    nameOfEmployee.setLayoutParams(paramsNameOfEmployee);
                    userName.setLayoutParams(paramsUserName);
                    userType.setLayoutParams(paramsUserType);

                    employeesTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout
                            .LayoutParams.WRAP_CONTENT));
                    //hideLoadingCircle();

                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                        }
                    });
                }
            }
        });
    }



/* Sections View **/
    private void launchSectionsView() {
        loading = (LinearLayout) findViewById(R.id.layout_progressbar);

        loading.setVisibility(View.VISIBLE);
        loading.setZ(10);

        menuGridView.setVisibility(RelativeLayout.INVISIBLE);
        addItem.setVisibility(RelativeLayout.INVISIBLE);

        inventoryColumns.setVisibility(LinearLayout.VISIBLE);
        sectionsTable.setVisibility(LinearLayout.VISIBLE);
        sectionsView.setVisibility(RelativeLayout.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mToolbar.setTitle("Restaurant - All Sections");

        new talkToDataBase().execute();
    }

    /* ----> Sections Table */
    private void updateSectionsView() {
        //showLoadingCircle();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                sectionsTable.removeAllViews();

                // get a reference for the TableLayout
                for (String aCurrListView : currListView) {

                    final TableRow row = new TableRow(MainActivity.this);
                    row.setClickable(true);

                    final TextView item = new TextView(MainActivity.this);
                    TextView category = new TextView(MainActivity.this);

                    String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");
                    Log.w("categories", String.valueOf(currListView.toString()));

                    item.setText(strArray[0]); //ITEM
                    category.setText(strArray[1]); // SECTION

                    row.addView(item);
                    row.addView(category);

                    item.setTextSize(18);
                    category.setTextSize(18);

                    item.setTextColor(Color.BLACK);
                    category.setTextColor(Color.BLACK);

                    Typeface roboto = Typeface.createFromAsset(getAssets(),
                            "font/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
                    item.setTypeface(roboto);
                    category.setTypeface(roboto);

                    LinearLayout.LayoutParams paramsItem = (LinearLayout.LayoutParams) item.getLayoutParams();
                    paramsItem.setMargins(70, 15 , 0, 25);

                    LinearLayout.LayoutParams paramsCategory = (LinearLayout.LayoutParams) category.getLayoutParams();
                    paramsCategory.setMargins(0, 15, 0, 25); // Left, Top, Right, Bottom

                    item.setLayoutParams(paramsItem);
                    category.setLayoutParams(paramsCategory);

                    sectionsTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout
                            .LayoutParams.WRAP_CONTENT));
                    //hideLoadingCircle();

                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                        }
                    });
                }
            }
        });
    }


/* Inventory View **/
    private void launchInventoryView() {
        loading = (LinearLayout) findViewById(R.id.layout_progressbar);

        loading.setVisibility(View.VISIBLE);
        loading.setZ(10);

        menuGridView.setVisibility(RelativeLayout.INVISIBLE);
        addItem.setVisibility(RelativeLayout.INVISIBLE);

        inventoryColumns.setVisibility(LinearLayout.VISIBLE);
        inventoryTable.setVisibility(LinearLayout.VISIBLE);
        inventoryView.setVisibility(RelativeLayout.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mToolbar.setTitle("Inventory - All Inventory");

        new talkToDataBase().execute();

    }

    /* ----> Inventory Table */
    private void updateInventoryView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                inventoryTable.removeAllViews();

                // get a reference for the TableLayout
                for (String aCurrListView : currListView) {

                    final TableRow row = new TableRow(MainActivity.this);
                    row.setClickable(true);

                    final TextView item = new TextView(MainActivity.this);
                    TextView category = new TextView(MainActivity.this);
                    TextView curr_qty = new TextView(MainActivity.this);
                    TextView max_qty = new TextView(MainActivity.this);
                    TextView time_stamp = new TextView(MainActivity.this);

                    String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");

                    item.setText(strArray[1]);     // ITEM
                    category.setText(strArray[2]); // CATEGORY
                    curr_qty.setText(strArray[3]); // CURR_QTY
                    max_qty.setText(strArray[4]);  // MAX_QTY
                    time_stamp.setText(strArray[5]);  // TIME_STAMP


                    item.setTextSize(18);
                    category.setTextSize(18);
                    curr_qty.setTextSize(18);
                    max_qty.setTextSize(18);
                    time_stamp.setTextSize(18);

                    item.setTextColor(Color.BLACK);
                    category.setTextColor(Color.BLACK);
                    curr_qty.setTextColor(Color.BLACK);
                    max_qty.setTextColor(Color.BLACK);
                    time_stamp.setTextColor(Color.BLACK);

                    Typeface roboto = Typeface.createFromAsset(getAssets(),
                            "font/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
                    item.setTypeface(roboto);
                    category.setTypeface(roboto);
                    curr_qty.setTypeface(roboto);
                    max_qty.setTypeface(roboto);
                    time_stamp.setTypeface(roboto);

                    System.out.println("In updateInventoryMethod " + Arrays.toString(strArray));
                    System.out.println("    id: " + strArray[0]);
                    System.out.println("    item: " + strArray[1]);
                    System.out.println("    category: " + strArray[2]);
                    System.out.println("    curr_qty: " + strArray[3]);
                    System.out.println("    max_qty: " + strArray[4]);
                    System.out.println("    time_stamp: " + strArray[5]);

                    row.addView(item);
                    row.addView(category);
                    row.addView(curr_qty);
                    //row.addView(max_qty);
                    row.addView(time_stamp);


                    LinearLayout.LayoutParams paramsItem = (LinearLayout.LayoutParams) item.getLayoutParams();
                    paramsItem.setMargins(25, 15 , 0, 25);

                    LinearLayout.LayoutParams paramsCategory = (LinearLayout.LayoutParams) category.getLayoutParams();
                    paramsCategory.setMargins(0, 15, 0, 25); // Left, Top, Right, Bottom

                    LinearLayout.LayoutParams paramsCurrentQTY = (LinearLayout.LayoutParams) curr_qty.getLayoutParams();
                    paramsCurrentQTY.setMargins(0, 15, 0, 25);

                    //LinearLayout.LayoutParams paramsMaxQTY = (LinearLayout.LayoutParams) max_qty.getLayoutParams();
                    //paramsMaxQTY.setMargins(pixelToDP(80), 0, 0, pixelToDP(25)); //substitute

                    LinearLayout.LayoutParams paramsTimeStamp = (LinearLayout.LayoutParams) time_stamp.getLayoutParams();
                    paramsTimeStamp.setMargins(0, 15, 0, 25);


                    item.setLayoutParams(paramsItem);
                    category.setLayoutParams(paramsCategory);
                    curr_qty.setLayoutParams(paramsCurrentQTY);
                    //max_qty.setLayoutParams(paramsMaxQTY);
                    time_stamp.setLayoutParams(paramsTimeStamp);


                    inventoryTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));

                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Log.w("Name of Item Selected", String.valueOf(item.getText()));
                        }
                    });
                }
            }
        });
    }

    /* ----> Inventory Add Item Function */
    private void launchAddItemView() {
        menuGridView.setVisibility(RelativeLayout.INVISIBLE);
        addItem.setVisibility(RelativeLayout.VISIBLE);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mToolbar.setTitle("Add Item to Inventory ");
        /* Prepare data */
        String[] sections = {"Select Section", "Front of house", "Kitchen", "Back of house", "Bar"};


        final EditText etItem = (EditText) findViewById(R.id.food_item);
        final EditText etCurrentQty = (EditText) findViewById(R.id.current_qty);
        final EditText etMinQty = (EditText) findViewById(R.id.min_qty);
        final EditText etMaxQty = (EditText) findViewById(R.id.max_qty);
        final Spinner etSections = (Spinner) findViewById(R.id.et_section);


        ArrayAdapter<String> adapterForSections = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sections);
        etSections.setAdapter(adapterForSections);

        final Button btn_addItem = (Button) findViewById(R.id.btn_add_item);
        final TextView view_items = (TextView) findViewById(R.id.view_items);

        /* Clear Form */
        etItem.setText("");
        etCurrentQty.setText("");
        etMinQty.setText("");
        etMaxQty.setText("");

        /* Close Dialog  */
        FloatingActionButton close_Popup = (FloatingActionButton) findViewById(R.id.close_Popup);
        close_Popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchInventoryView();
            }
        });

        /* Confirm add item */
        view_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchInventoryView();
            }
        });

        // User presses the "No account yet? Create one"
        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String item = etItem.getText().toString();
                final String currentQty = etCurrentQty.getText().toString();
                final String minQty = etMinQty.getText().toString();
                final String maxQty = etMaxQty.getText().toString();
                final String category = etSections.getSelectedItem().toString();
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();
                final String last_time_updated = dateFormat.format(date);
                loading = (LinearLayout) findViewById(R.id.layout_progressbar);

                loading.setVisibility(View.VISIBLE);
                loading.setZ(10);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                toast("Success adding item", 10000000);
                                loading.setVisibility(View.INVISIBLE);
                                launchInventoryView();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                toast("Failed, please try again, perhaps item is already added?", 5000);
                                builder.setMessage("Adding Item Failed").setNegativeButton("Retry", null).create()
                                        .show();
                                loading.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // The next 3 lines calls the @see RegisterRequest class.
                InventoryRequest inventoryRequest = new InventoryRequest(item, currentQty, minQty, maxQty, category,
                        last_time_updated, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(inventoryRequest);
            }
        });
    }

    /* -----> Inventory Remove Item Function */
    private void launchRemoveItemView() {

    }


/* Weekly Count View **/
    private void launchWeeklyCountView() {

        loading = (LinearLayout) findViewById(R.id.layout_progressbar);
        loading.setVisibility(View.VISIBLE);
        loading.setZ(10);

        menuGridView.setVisibility(RelativeLayout.INVISIBLE);
        addItem.setVisibility(RelativeLayout.INVISIBLE);

        inventoryCountColumns.setVisibility(LinearLayout.VISIBLE);
        inventoryCountTable.setVisibility(LinearLayout.VISIBLE);
        inventoryCountView.setVisibility(RelativeLayout.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mToolbar.setTitle("Weekly Count");

        new talkToDataBase().execute();


        Button saveBtn = (Button) findViewById(R.id.save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Update Inventory?")
                        .setMessage("You will be updating Pho Tre Bien's restaurant inventory. Are you sure you want " +
                                "to proceed?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                loading = (LinearLayout) findViewById(R.id.layout_progressbar);

                                loading.setVisibility(View.VISIBLE);
                                loading.setZ(10);
                                Thread thread = new Thread() {
                                    public void run() {
                                        Looper.prepare();
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // UPDATE INVENTORY TABLE HERE TO DATABASE
                                                for(int t = 0; t < parser.getWeeklyTable().size(); t++){
                                                    System.out.println("Get updated inventory list " + parser.getWeeklyTable().get(t));
                                                    updateInventory(parser.getWeeklyTable().get(t));
                                                }

                                                handler.removeCallbacks(this);
                                                Looper.myLooper().quit();
                                            }
                                        }, 400);
                                        Looper.loop();
                                    }
                                };
                                thread.start();
                                toast("SUCCESS UPDATING INVENTORY", 10000);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_eye)
                        .show();
            }
        });

    }

    /* ----> Weekly Count Table */
    private void updateWeeklyCount() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                inventoryCountTable.removeAllViews();

                for (String aCurrListView : currListView) {
                    System.out.println("updateWeeklyCount() " + currListView.toString());
                    final TableRow row = new TableRow(MainActivity.this);
                    row.setClickable(true);

                    final TextView id = new TextView(MainActivity.this);
                    final TextView item = new TextView(MainActivity.this);
                    final TextView category = new TextView(MainActivity.this);
                    ArrayList<String> spinnerArray = new ArrayList<String>();

                    for (int k = 0; k < 100; k++) {
                        spinnerArray.add(String.valueOf(k));
                    }
                    Spinner spinner = new Spinner(MainActivity.this);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R
                            .layout.simple_spinner_dropdown_item, spinnerArray);
                    spinner.setAdapter(spinnerArrayAdapter);

                    final String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");
                    System.out.println("in the update weekly method " + Arrays.toString(strArray));

                    id.setText(strArray[0]);         //ID
                    item.setText(strArray[1]);     // ITEM
                    category.setText(strArray[2]);         // CAT
                    //category1.setText(strArray[3]);     // CURR_QTY
                    //category1.setText(strArray[4]);     // MAX_QTY
                    //category1.setText(strArray[5]);     // TIME



                    row.addView(id);
                    row.addView(item);
                    row.addView(category);
                    row.addView(spinner);

                    id.setTextColor(Color.BLACK);
                    item.setTextColor(Color.BLACK);
                    category.setTextColor(Color.BLACK);

                    Typeface roboto = Typeface.createFromAsset(getAssets(),
                            "font/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
                    id.setTypeface(roboto);
                    item.setTypeface(roboto);
                    category.setTypeface(roboto);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            String currSpinner = (String) parent.getItemAtPosition(position);

                            if (Integer.parseInt(currSpinner) > 0) { // Add Item to final list if qty is > 0
                                System.out.println("in the spinner function " + Arrays.toString(strArray));
                                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                                Date date = new Date();
                                final String currDate = dateFormat.format(date);

                                String copyId = strArray[0];
                                String copyItem = (String) item.getText();
                                String copyCategory = (String) category.getText();
                                String copyCurrQty = currSpinner;
                                String copyMaxQty = strArray[4];
                                String copyTime = currDate;

                                System.out.println("    Spinner ");
                                System.out.println("            id " + copyId);
                                System.out.println("            item " + copyItem);
                                System.out.println("            category " + copyCategory);
                                System.out.println("            curr_qty " + copyCurrQty);
                                System.out.println("            max_qty " + copyMaxQty);
                                System.out.println("            time " + copyTime);

                                parser.updateInventoryCount(copyId, copyItem, copyCategory, copyCurrQty, copyMaxQty,
                                        copyTime);
                            }
                        }

                        /**
                         * Callback method to be invoked when the selection disappears from this
                         * view. The selection can disappear for instance when touch is activated
                         * or when the adapter becomes empty.
                         *
                         * @param parent The AdapterView that now contains no selected item.
                         */
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    inventoryCountTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));

                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Log.w("Name of Item Selected", String.valueOf(item.getText()));
                        }
                    });
                }
            }
        });
    }

    /* Save Data to db */
    private void updateInventory(String list) {
        String strArray[] = list.replace("[", "").replace("]", "").replace(",", "").split(" ");
        String id = strArray[0];
        String item = strArray[1];
        //String category = strArray[2];
        String curr_qty = strArray[3];
        //String max_qty = strArray[4];

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String last_time_updated = dateFormat.format(date);

        System.out.println("Update Inventory ");
        System.out.println("                Sid " + id);
        System.out.println("                Sitem " + item);
        //System.out.println("                Scategory " + category);
        System.out.println("                Scurr_qty " + curr_qty);
        //System.out.println("                Smax_qty " + max_qty);
        System.out.println("                Slast_time_updated " + last_time_updated);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("id", id));
        nameValuePairs.add(new BasicNameValuePair("item", item));
        //nameValuePairs.add(new BasicNameValuePair("category", category));
        nameValuePairs.add(new BasicNameValuePair("curr_qty", curr_qty));
        //nameValuePairs.add(new BasicNameValuePair("max_qty", max_qty));
        nameValuePairs.add(new BasicNameValuePair("last_time_updated", last_time_updated));

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.narped.com/inventory/UpdateInventory.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        }
        catch(Exception e) {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    Toast.LENGTH_LONG).show();
        }

        try {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        }
        catch(Exception e) {
            Log.e("Fail 2", e.toString());
        }

        try {
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));

            if(code==1) {
                Toast.makeText(getBaseContext(), "Update Successfully",
                        Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.INVISIBLE);
            }
            else {
                Toast.makeText(getBaseContext(), "Sorry, Try Again",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e) {
            Log.e("Fail 3", e.toString());
        }
    }

/* Prices View **/
    private void launchPricesView() {
        loading = (LinearLayout) findViewById(R.id.layout_progressbar);

        loading.setVisibility(View.VISIBLE);
        loading.setZ(10);

        menuGridView.setVisibility(RelativeLayout.INVISIBLE);
        addPrice.setVisibility(RelativeLayout.INVISIBLE);

        pricesColumns.setVisibility(LinearLayout.VISIBLE);
        pricesTable.setVisibility(LinearLayout.VISIBLE);
        pricesView.setVisibility(RelativeLayout.VISIBLE);


        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mToolbar.setTitle("Prices - All Prices from sellers");
        new talkToDataBase().execute();

    }

    /* ----> Prices Table */
    private void updatePricesView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                pricesTable.removeAllViews();

                // get a reference for the TableLayout
                for (String aCurrListView : currListView) {

                    final TableRow row = new TableRow(MainActivity.this);
                    row.setClickable(true);

                    final TextView item = new TextView(MainActivity.this);
                    TextView pricePerQty = new TextView(MainActivity.this);
                    TextView distributor = new TextView(MainActivity.this);

                    String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");

                    item.setText(strArray[0]); //ITEM
                    pricePerQty.setText(strArray[1]);
                    distributor.setText(strArray[2]);

                    item.setTextSize(18);
                    pricePerQty.setTextSize(18);
                    distributor.setTextSize(18);

                    item.setTextColor(Color.BLACK);
                    pricePerQty.setTextColor(Color.BLACK);
                    distributor.setTextColor(Color.BLACK);


                    Typeface roboto = Typeface.createFromAsset(getAssets(),
                            "font/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
                    item.setTypeface(roboto);
                    pricePerQty.setTypeface(roboto);
                    distributor.setTypeface(roboto);

                    row.addView(item);
                    row.addView(pricePerQty);
                    row.addView(distributor);

                    LinearLayout.LayoutParams paramsItem = (LinearLayout.LayoutParams) item.getLayoutParams();
                    paramsItem.setMargins(25, 15 , 0, 25);

                    LinearLayout.LayoutParams paramsCategory = (LinearLayout.LayoutParams) pricePerQty.getLayoutParams();
                    paramsCategory.setMargins(25, 15 , 0, 25); // Left, Top, Right, Bottom

                    LinearLayout.LayoutParams paramsCurrentQTY = (LinearLayout.LayoutParams) distributor.getLayoutParams();
                    paramsCurrentQTY.setMargins(25, 15 , 0, 25); //substitute

                    item.setLayoutParams(paramsItem);
                    pricePerQty.setLayoutParams(paramsCategory);
                    distributor.setLayoutParams(paramsCurrentQTY);

                    pricesTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT,
                            DrawerLayout.LayoutParams.WRAP_CONTENT));
                    //hideLoadingCircle();

                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Log.w("Name of Item Selected", String.valueOf(item.getText()));
                        }
                    });
                }
            }
        });
    }

    /* ----> Prices Add Item Function */
    private void launchAddPricesView() {
        menuGridView.setVisibility(RelativeLayout.INVISIBLE);
        addPrice.setVisibility(RelativeLayout.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mToolbar.setTitle("Add Item to Distributors ");
        /* Prepare data */
        String[] distributors = {"Select Vendor", "Lams", "Sams", "Taiwan Trading", "Restaurant Depot",
                "Shamrock", "Cosco", "Food King"};

        final EditText etItem = (EditText) findViewById(R.id.price_item);
        final EditText etPricePerQty = (EditText) findViewById(R.id.price_per_qty);
        final Spinner etDistributor = (Spinner) findViewById(R.id.distributor_for_prices);

        ArrayAdapter<String> adapterForDistributors = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, distributors);
        etDistributor.setAdapter(adapterForDistributors);

        final Button btn_addItem = (Button) findViewById(R.id.btn_add_item_for_prices);
        final TextView view_items = (TextView) findViewById(R.id.view_prices);


        /* Clear Form */
        etItem.setText("");
        etPricePerQty.setText("");

        /* Close Dialog  */
        FloatingActionButton close_Popup = (FloatingActionButton) findViewById(R.id.close_Popup_for_price);
        close_Popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPricesView();
            }
        });

        /* Confirm add item */
        view_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPricesView();
            }
        });

        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add Item to Distributors?")
                        .setMessage("You will be adding an item to a vendor. Are you sure you want to proceed?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               // showLoadingCircle();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        final String item = etItem.getText().toString();
                                        final String currentQty = etPricePerQty.getText().toString();
                                        final String distributor = etDistributor.getSelectedItem().toString();

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonResponse = new JSONObject(response);
                                                    boolean success = jsonResponse.getBoolean("success");
                                                    if (success) {
                                                        toast("Success adding item", 10000000);
                                                        launchPricesView();
                                                    } else {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                        toast("Failed, please try again, perhaps item is already added?", 5000);
                                                        builder.setMessage("Adding Item Failed").setNegativeButton("Retry", null).create()
                                                                .show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        // The next 3 lines calls the @see RegisterRequest class.
                                        PriceRequest priceRequest = new PriceRequest(item, currentQty, distributor, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                        queue.add(priceRequest);
                                    }
                                }, 500);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_eye)
                        .show();
            }
        });
    }



/* Place Order View **/
    private void launchPlaceOrderView() {

        final Button recommendedOrder = (Button) findViewById(R.id.reccomended_orders);
        final FloatingActionButton closePopUpButton = (FloatingActionButton) findViewById(R.id.close_place_order_pop_up);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        menuGridView.setVisibility(RelativeLayout.INVISIBLE);
        inventoryColumns.setVisibility(LinearLayout.INVISIBLE);
        inventoryTable.setVisibility(LinearLayout.INVISIBLE);
        addItem.setVisibility(RelativeLayout.INVISIBLE);
        swipeRefresh.setVisibility(RelativeLayout.INVISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        mRecyclerView.setLayoutManager(layoutManager);

        // Obtain prices from previous view
        mToolbar.setTitle("Prices - All Prices from sellers");
        new talkToDataBase().execute();

        recommendedOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbar.setTitle("Order - Place an Order");
                loading = (LinearLayout) findViewById(R.id.layout_progressbar);

                loading.setVisibility(View.VISIBLE);
                loading.setZ(109);
                new talkToDataBase().execute();

                placeOrderPopUp.setVisibility(ScrollView.VISIBLE);

                placeOrderColumns.setVisibility(LinearLayout.VISIBLE);
                placeOrderTable.setVisibility(LinearLayout.VISIBLE);
                placeOrderView.setVisibility(RelativeLayout.VISIBLE);

                closePopUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        placeOrderPopUp.setVisibility(ScrollView.GONE);
                        placeOrderColumns.setVisibility(LinearLayout.GONE);
                        placeOrderTable.setVisibility(LinearLayout.GONE);
                    }
                });

            }
        });



    }

    /* ----> Place Order Recommended Table */
    private void updatePlaceOrderView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LinkedList<String> recepitOrder = new LinkedList<String>();
                placeOrderTable.removeAllViews();
                // get a reference for the TableLayout
                System.out.println("DEXTER" + currListView.toString());
                for (String aCurrListView : currListView) {

                    final TableRow row = new TableRow(MainActivity.this);
                    row.setClickable(true);

                    final TextView item = new TextView(MainActivity.this);
                    final TextView distributor = new TextView(MainActivity.this);
                    final TextView price = new TextView(MainActivity.this);
                    TextView par = new TextView(MainActivity.this);
                    TextView curr_qty = new TextView(MainActivity.this);

                    item.setTextSize(12);
                    distributor.setTextSize(12);
                    price.setTextSize(12);
                    par.setTextSize(12);
                    curr_qty.setTextSize(12);

                    String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");
                    System.out.println("Boop " + Arrays.toString(strArray));
                    item.setText(strArray[0]); //ITEM
                    distributor.setText(strArray[1]);
                    price.setText(strArray[2]);
                    par.setText(strArray[4] + " - " + strArray[5]);
                    curr_qty.setText(strArray[5]);

                    item.setTextColor(Color.BLACK);
                    distributor.setTextColor(Color.BLACK);
                    price.setTextColor(Color.BLACK);
                    par.setTextColor(Color.BLACK);
                    curr_qty.setTextColor(Color.BLACK);

                    Typeface roboto = Typeface.createFromAsset(getAssets(),
                            "font/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
                    item.setTypeface(roboto);
                    distributor.setTypeface(roboto);
                    price.setTypeface(roboto);
                    par.setTypeface(roboto);
                    curr_qty.setTypeface(roboto);

                    ArrayList<String> spinnerArray = new ArrayList<String>();

                    for (int k = 0; k < 100; k++) {
                        spinnerArray.add(String.valueOf(k));
                    }

                    Spinner spinner = new Spinner(MainActivity.this);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R
                            .layout.simple_spinner_dropdown_item, spinnerArray);
                    spinner.setAdapter(spinnerArrayAdapter);


                    row.addView(item);
                    row.addView(distributor);
                    row.addView(price);
                    row.addView(par);
                    row.addView(curr_qty);
                    row.addView(spinner);

                    Log.w("item1", item.getText().toString());

                    LinearLayout.LayoutParams paramsItem = (LinearLayout.LayoutParams) item.getLayoutParams();
                    paramsItem.setMargins(0, 15 , 0, 25);

                    LinearLayout.LayoutParams paramsDistributor = (LinearLayout.LayoutParams) distributor.getLayoutParams();
                    paramsDistributor.setMargins(0, 15 , 0, 25); // Left, Top, Right, Bottom

                    LinearLayout.LayoutParams paramsPrice = (LinearLayout.LayoutParams) price.getLayoutParams();
                    paramsPrice.setMargins(0, 15 , 0, 25);

                    LinearLayout.LayoutParams paramsPar = (LinearLayout.LayoutParams) par.getLayoutParams();
                    paramsPar.setMargins(0, 15 , 0, 25);

                    LinearLayout.LayoutParams paramsCurrentQTY = (LinearLayout.LayoutParams) curr_qty.getLayoutParams();
                    paramsCurrentQTY.setMargins(0, 15 , 0, 25);

                    LinearLayout.LayoutParams paramsDropDown = (LinearLayout.LayoutParams) spinner.getLayoutParams();
                    paramsDropDown.setMargins(0, 15 , 0, 25);

                    item.setLayoutParams(paramsItem);
                    distributor.setLayoutParams(paramsDistributor);
                    price.setLayoutParams(paramsPrice);
                    par.setLayoutParams(paramsPar);
                    curr_qty.setLayoutParams(paramsCurrentQTY);
                    spinner.setLayoutParams(paramsDropDown);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            String currSpinner = (String) parent.getItemAtPosition(position);

                            if (Integer.parseInt(currSpinner) > 0) { // Add Item to final list if qty is > 0
                                String finalStuff = item.getText() + " " + distributor.getText() + " " + price.getText() + " " + currSpinner;
                                recepitOrder.add(finalStuff);
                            }
                        }

                        /**
                         * Callback method to be invoked when the selection disappears from this
                         * view. The selection can disappear for instance when touch is activated
                         * or when the adapter becomes empty.
                         *
                         * @param parent The AdapterView that now contains no selected item.
                         */
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    placeOrderTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout
                            .LayoutParams.WRAP_CONTENT));


                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Log.w("Name of Item Selected", String.valueOf(item.getText()));
                        }
                    });
                }

                    Button placeOrderBtn = (Button) findViewById(R.id.place_order_button);
                    placeOrderBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Place an Order")
                                    .setMessage("You will be receiving an email and updating Pho Tre Bien Inventory " +
                                                    "\n" +
                                            recepitOrder.toString())
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                           // showLoadingCircle();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {
                                                                JSONObject jsonResponse = new JSONObject(response);
                                                                boolean success = jsonResponse.getBoolean("success");
                                                                if (success) {
                                                                    toast("Success sending order!", 10000000);
                                                                } else {
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                                    toast("Failed, please try again.", 5000);
                                                                    builder.setMessage("Adding Item Failed").setNegativeButton("Retry", null).create()
                                                                            .show();
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    };

                                                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                                                    Date date = new Date();
                                                    final String currDate = dateFormat.format(date);
                                                    Random random = new Random();
                                                    int receipt_number = random.nextInt(100000 + 1) + 1;
                                                    for(int t = 0; t < recepitOrder.size(); t++) {

                                                        String strArray[] = recepitOrder.get(t).replace("[", "").replace("]", "").replace(",", "").split(" ");
                                                        final String item = strArray[0];
                                                        final String distributor = strArray[1];
                                                        final String price = strArray[2];
                                                        final String qty = strArray[3];

                                                        System.out.println("item1 " + item);
                                                        System.out.println("distributor1 " + distributor);
                                                        System.out.println("price1 " + price);
                                                        System.out.println("qty " + qty);
                                                        System.out.println("place_order_time " + currDate);
                                                        System.out.println("receipt_number " + receipt_number);

                                                        OrderRequest orderRequest = new OrderRequest(item,
                                                                distributor, price, qty, currDate, receipt_number,
                                                                responseListener);
                                                        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                                        queue.add(orderRequest);

                                                    }

                                                    // SEND DATA TO DB HERE
                                                    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                                    emailIntent.setType("text/plain");
                                                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,  "ptbexpress@gmail.com");
                                                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                                                            "RECEIPT #" + receipt_number + " Date: " + currDate);
                                                    String temp = currListView.toString();
                                                    temp=temp.replaceAll(",", "\n");
                                                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, temp);

                                                    emailIntent.setType("message/rfc822");

                                                    try {
                                                        startActivity(Intent.createChooser(emailIntent,
                                                                "Send email using..."));
                                                    } catch (android.content.ActivityNotFoundException ex) {
                                                        Toast.makeText(MainActivity.this,
                                                                "No email clients installed.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }, 1500);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            updatePlaceOrderView();
                                        }
                                    })
                                    .setIcon(R.drawable.ic_eye)
                                    .show();
                        }
                    });

            }
        });
    }


/* Active Orders View **/
    private void launchActiveOrders(){
        loading = (LinearLayout) findViewById(R.id.layout_progressbar);

        loading.setVisibility(View.VISIBLE);
        loading.setZ(10);

        menuGridView.setVisibility(RelativeLayout.INVISIBLE);
        addItem.setVisibility(RelativeLayout.INVISIBLE);

        activeOrdersColumns.setVisibility(LinearLayout.VISIBLE);
        activeOrdersTable.setVisibility(LinearLayout.VISIBLE);
        activeOrdersTablePopUp.setVisibility(LinearLayout.VISIBLE);
        activeOrdersView.setVisibility(RelativeLayout.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mToolbar.setTitle("Active Orders");
        new talkToDataBase().execute();

    }

    /* ----> Active Order Table */
    private void updateActiveOrdersView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                activeOrdersTable.removeAllViews();

                // iterate number of unique order numbers
                System.out.println("Number orders" + parser.getNumberOfOrders());
                for(int n = 0; n < parser.getNumberOfOrders(); n++){
                    String aCurrListView = parser.getAllActiveOrders().get(n);

                    final TableRow row = new TableRow(MainActivity.this);
                    row.setClickable(true);

                    final TextView ticketNumber = new TextView(MainActivity.this);
                    TextView orderDate = new TextView(MainActivity.this);
                    TextView status = new TextView(MainActivity.this);
                    TextView total = new TextView(MainActivity.this);

                    ticketNumber.setTextSize(18);
                    orderDate.setTextSize(18);
                    status.setTextSize(18);
                    total.setTextSize(18);

                    final String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");

                    ticketNumber.setText(strArray[1]); //ITEM
                    orderDate.setText(strArray[2]); // ORDER DATE
                    status.setText(strArray[3]); // STATUS
                    total.setText(strArray[4]); // TOTAL

                    row.addView(ticketNumber);
                    row.addView(orderDate);
                    row.addView(status);
                    row.addView(total);

                    ticketNumber.setTextColor(Color.BLACK);
                    orderDate.setTextColor(Color.BLACK);
                    status.setTextColor(Color.BLACK);
                    total.setTextColor(Color.BLACK);

                    Typeface roboto = Typeface.createFromAsset(getAssets(),
                            "font/Roboto-Light.ttf"); //use this.getAssets if you are calling from an Activity
                    ticketNumber.setTypeface(roboto);
                    orderDate.setTypeface(roboto);
                    status.setTypeface(roboto);
                    total.setTypeface(roboto);

                    LinearLayout.LayoutParams paramsItem = (LinearLayout.LayoutParams) ticketNumber.getLayoutParams();
                    paramsItem.setMargins(25, 15 , 0, 25);

                    LinearLayout.LayoutParams paramsCategory = (LinearLayout.LayoutParams) orderDate.getLayoutParams();
                    paramsCategory.setMargins(25, 15 , 0, 25); // Left, Top,
                    // Right, Bottom

                    LinearLayout.LayoutParams paramsCurrentQTY = (LinearLayout.LayoutParams) status.getLayoutParams();
                    paramsCurrentQTY.setMargins(25, 15 , 0, 25);

                    LinearLayout.LayoutParams paramsMaxQTY = (LinearLayout.LayoutParams) total.getLayoutParams();
                    paramsMaxQTY.setMargins(25, 15 , 0, 25); //substitute

                    ticketNumber.setLayoutParams(paramsItem);
                    orderDate.setLayoutParams(paramsCategory);
                    status.setLayoutParams(paramsCurrentQTY);
                    total.setLayoutParams(paramsMaxQTY);

                    activeOrdersTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout
                            .LayoutParams.WRAP_CONTENT));

                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            activeOrdersPopUp.setVisibility(View.VISIBLE);
                            TextView tvTicketNumber = (TextView) findViewById(R.id.ticket_number);
                            tvTicketNumber.setText("Ticket Number: " + ticketNumber.getText());
                            final LinkedList<String> currPopUpSelected = parser.getOrder(String.valueOf(ticketNumber.getText()));
                            System.out.println("ACTIVE ORDERS " + currPopUpSelected);
                            activeOrdersTablePopUp.removeAllViews();

                            for(int i = 0 ; i < currPopUpSelected.size(); i++){
                                String aCurrListView = currPopUpSelected.get(i);

                                final TableRow row = new TableRow(MainActivity.this);
                                row.setClickable(true);

                                final TextView item = new TextView(MainActivity.this);
                                TextView qty = new TextView(MainActivity.this);
                                TextView price = new TextView(MainActivity.this);

                                final String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");

                                item.setText(strArray[1]);
                                qty.setText(strArray[3]);
                                price.setText(strArray[4]);

                                row.addView(item);
                                row.addView(qty);
                                row.addView(price);

                                LinearLayout.LayoutParams paramsItem = (LinearLayout.LayoutParams) item.getLayoutParams();
                                paramsItem.setMargins(25, 15 , 0, 25);

                                LinearLayout.LayoutParams paramsCategory = (LinearLayout.LayoutParams) qty.getLayoutParams();
                                paramsCategory.setMargins(25, 15 , 0, 25); // Left, Top,
                                // Right, Bottom

                                LinearLayout.LayoutParams paramsCurrentQTY = (LinearLayout.LayoutParams) price.getLayoutParams();
                                paramsCurrentQTY.setMargins(25, 15 , 0, 25);

                                activeOrdersTablePopUp.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));

                                Button confirm = (Button) findViewById(R.id.current_orders_confirm);
                                confirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Thread thread = new Thread() {
                                            public void run() {
                                                Looper.prepare();
                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // UPDATE INVENTORY TABLE HERE TO DATABASE
                                                        updateInventory(currPopUpSelected.toString());
                                                        handler.removeCallbacks(this);
                                                        Looper.myLooper().quit();
                                                    }
                                                }, 400);
                                                Looper.loop();
                                            }
                                        };
                                        thread.start();
                                        toast("SUCCESS UPDATING INVENTORY", 10000);

                                    }
                                });
                            }

                            FloatingActionButton closePopUp = (FloatingActionButton) findViewById(R.id.active_orders_close_pop_up);
                            closePopUp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    activeOrdersPopUp.setVisibility(View.INVISIBLE);
                                }
                            });

                        }
                    });
                }

            }
        });
    }

/* Get Data From Database */
    private class talkToDataBase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... args) {

            /* Building Parameters */
            List<NameValuePair> params = new ArrayList<>();
            JSONObject json = new JSONObject();
            if(mToolbar.getTitle().equals("Employees - All Employees")){
                String URL_PHP = "http://www.narped.com/inventory/Employees.php";
                json = jParser.makeHttpRequest(URL_PHP, "GET", params);
            }
            else if(mToolbar.getTitle().equals("Restaurant - All Sections")){
                String URL_PHP = "http://www.narped.com/inventory/ItemsInventory.php";
                json = jParser.makeHttpRequest(URL_PHP, "GET", params);
            }
            else if(mToolbar.getTitle().equals("Inventory - " + "All Inventory")){
                String URL_PHP = "http://www.narped.com/inventory/ItemsInventory.php";
                json = jParser.makeHttpRequest(URL_PHP, "GET", params);
            }
            else if (mToolbar.getTitle().equals("Order - Place an Order")){
                String URL_PHP = "http://www.narped.com/inventory/ItemsInventory.php";
                json = jParser.makeHttpRequest(URL_PHP, "GET", params);
            }
            else if(mToolbar.getTitle().equals("Weekly Count")){
                String URL_PHP = "http://www.narped.com/inventory/ItemsInventory.php";
                json = jParser.makeHttpRequest(URL_PHP, "GET", params);
            }
            else if(mToolbar.getTitle().equals("Prices - All Prices from sellers")){
                String URL_PHP = "http://www.narped.com/inventory/Prices.php";
                json = jParser.makeHttpRequest(URL_PHP, "GET", params);
            }
            else if(mToolbar.getTitle().equals("Active Orders")){
                String URL_PHP = "http://www.narped.com/inventory/ActiveOrders.php";
                json = jParser.makeHttpRequest(URL_PHP, "GET", params);
            }
            try {
                /* Checking for SUCCESS TAG */
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray JAStuff = json.getJSONArray(TAG_STUFF);
                    /* CHECK THE NUMBER OF RECORDS **/
                    int intStuff = JAStuff.length();
                    if (intStuff != 0) {
                        for (int i = 0; i < JAStuff.length(); i++) {
                            JSONObject JOStuff = JAStuff.getJSONObject(i);

                            if(mToolbar.getTitle().equals("Employees - All Employees")){
                                parser.parseEmployees(JOStuff);
                            }
                            else if(mToolbar.getTitle().equals("Restaurant - All Sections")){
                                parser.parseSections(JOStuff);
                            }
                            else if(mToolbar.getTitle().equals("Inventory - All Inventory")){
                                parser.parseInventory(JOStuff);
                                parser.parseSections(JOStuff);
                            }
                            else if(mToolbar.getTitle().equals("Weekly Count")){
                                parser.parseInventoryCount(JOStuff);
                            }
                            else if(mToolbar.getTitle().equals("Prices - All Prices from sellers")){
                                parser.parsePrices(JOStuff);
                            }
                            else if(mToolbar.getTitle().equals("Order - Place an Order")){
                                parser.parseInventory(JOStuff);
                            }
                            if(mToolbar.getTitle().equals("Active Orders")){
                                parser.parseActiveOrders(JOStuff);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(mToolbar.getTitle().equals("Employees - All Employees")){
                currListView = parser.getAllEmployee();
                loading.setVisibility(View.INVISIBLE);
                updateEmployeesView();
            }
            else if(mToolbar.getTitle().equals("Restaurant - All Sections")){
                currListView = parser.getAllItems();
                loading.setVisibility(View.INVISIBLE);
                updateSectionsView();
            }
            else if(mToolbar.getTitle().equals("Inventory - All Inventory")){
                Log.w("2000", parser.getInventoryTable().toString());
                currListView = parser.getInventoryTable();
                loading.setVisibility(View.INVISIBLE);
                updateInventoryView();
            }
            else if(mToolbar.getTitle().equals("Weekly Count")){
                Log.w("2500", parser.getWeeklyCountTable().toString());
                currListView = parser.getWeeklyCountTable();
                loading.setVisibility(View.INVISIBLE);
                updateWeeklyCount();
            }
            else if(mToolbar.getTitle().equals("Prices - All Prices from sellers")){
                Log.w("3000", parser.getAllPrices().toString());
                currListView = parser.getAllPrices();
                loading.setVisibility(View.INVISIBLE);
                updatePricesView();
            }
            else if(mToolbar.getTitle().equals("Order - Place an Order")){
                mToolbar.getMenu().clear();
                LinkedList<String> prices = new LinkedList<>();
                LinkedList<String> lowStock = new LinkedList<>();
                LinkedList<String> recommended = new LinkedList<>();

                prices.addAll(parser.getAllPrices());
                lowStock.addAll(parser.allItemsFromLowStock());
                recommended = (parser.parseRecommended(lowStock, prices));

                Log.w("3500 Prices--> ", prices.toString());
                Log.w("3700 Low Stock--> ", lowStock.toString());

                final LinkedList<String> finalRecommended = recommended;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("3800 Recommended--> ", finalRecommended.toString());
                        //showLoadingCircle();
                        System.out.println(("3900 removeDuplicates--> "+ parser.removeDuplicates(finalRecommended)));
                        currListView = finalRecommended;
                        updatePlaceOrderView();
                        loading.setVisibility(View.INVISIBLE);
                    }
                }, 5000);
            }

            else if(mToolbar.getTitle().equals("Active Orders")){
                System.out.println("SUNDAY3");
                Log.w("4500", parser.getAllActiveOrders().toString());
                loading.setVisibility(View.INVISIBLE);
                currListView =parser.getAllActiveOrders();
                updateActiveOrdersView();
            }
            super.onPostExecute(aVoid);
        }
    }



/* Restart Activity */
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

/* Toast */
    /**
     * Show a toast message.
     */
    private void toast(String msg, int lengthOfToast) {
        final Toast toast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
        new CountDownTimer(1500, lengthOfToast) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }

            public void onFinish() {
                toast.cancel();
            }
        }.start();
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    private int pixelToDP(int pixel) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) ((pixel * scale) + 0.5f);
    }

}
