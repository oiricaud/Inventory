package activity;

import communication.InventoryRequest;
import communication.LoginRequest;
import communication.PriceRequest;
import communication.RegisterRequest;
import adapter.ImageAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import inc.pheude.inventory.R;
import model.*;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * Created by oscarricaud on 7/19/17.
 */
public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private LinkedList<String> currListView = new LinkedList<>();
    private JSONParser jParser = new JSONParser();
    private String TAG_SUCCESS = "success";
    private String TAG_STUFF = "stuff";

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private RecyclerView mRecyclerView;

    private GridView menuGridView;

    private LinearLayout placeOrderColumns;
    private LinearLayout inventoryColumns;
    private LinearLayout pricesColumns;



    private TableLayout employeesTable;
    private TableLayout sectionsTable;
    private TableLayout inventoryTable;
    private TableLayout pricesTable;
    private TableLayout placeOrderTable;

    private RelativeLayout swipeRefresh;

    private LinearLayout menuView;
    private RelativeLayout inventoryView;
    private RelativeLayout placeOrderView;
    private RelativeLayout employeesView;
    private RelativeLayout sectionsView;
    private RelativeLayout pricesView;

    private RelativeLayout addItem;
    private RelativeLayout addPrice;

    private RelativeLayout foodItemPopUp;
    private RelativeLayout placeOrderPopUp;

    private TextView titleClicked;
    private ProgressBar loading;

    private Parser parser = new Parser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loading = (ProgressBar) findViewById(R.id.loadingCircle);
        loading.setVisibility(View.GONE); // Hide loading
        TextView createAccount = (TextView) findViewById(R.id.link_signup);
        Button login = (Button) findViewById(R.id.btn_login);
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

    /* User Log In */
    /**
     * This method verifies the users credentials and if they are correct, it launches the home menu.
     */
    private void userLogin() {

        final EditText etUserName = (EditText) findViewById(R.id.input_user_name);
        final EditText etPassword = (EditText) findViewById(R.id.input_password);
        final String username = etUserName.getText().toString();
        final String password = etPassword.getText().toString();
        showLoadingCircle();

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
                        loading.setVisibility(View.GONE);
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
                                Log.w("Employee Name:", employeename);
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
                pricesView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);

                drawerFragment.closeDrawers();
                launchHomeView();
                break;
            case 1:  // Employees
                menuView.setVisibility(LinearLayout.GONE);
                sectionsView.setVisibility(RelativeLayout.GONE);
                inventoryView.setVisibility(RelativeLayout.GONE);
                pricesView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);

                drawerFragment.closeDrawers();
                launchEmployeesView();
                break;

            case 2:  // Sections
                menuView.setVisibility(LinearLayout.GONE);
                employeesView.setVisibility(RelativeLayout.GONE);
                inventoryView.setVisibility(RelativeLayout.GONE);
                pricesView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);

                drawerFragment.closeDrawers();
                launchSectionsView();
                break;
            case 3:  // Inventory
                menuView.setVisibility(LinearLayout.GONE);
                employeesView.setVisibility(RelativeLayout.GONE);
                pricesView.setVisibility(RelativeLayout.GONE);
                sectionsView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);

                drawerFragment.closeDrawers();
                launchInventoryView();
                break;
            case 4:  // Weekly Count
                menuView.setVisibility(LinearLayout.GONE);
                employeesView.setVisibility(RelativeLayout.GONE);
                pricesView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);
                drawerFragment.closeDrawers();
                launchWeeklyCountView();
                break;
            case 5:  // View Prices
                menuView.setVisibility(LinearLayout.GONE);
                employeesView.setVisibility(RelativeLayout.GONE);
                placeOrderView.setVisibility(RelativeLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);
                drawerFragment.closeDrawers();
                launchPricesView();
                break;
            case 6:  // Place Order
                placeOrderView.setVisibility(RelativeLayout.VISIBLE);
                menuView.setVisibility(LinearLayout.GONE);
                employeesView.setVisibility(RelativeLayout.GONE);
                sectionsView.setVisibility(LinearLayout.GONE);
                placeOrderPopUp.setVisibility(RelativeLayout.GONE);
                drawerFragment.closeDrawers();
                launchPlaceOrderView();
                break;
            case 7:  // Log Out
                Log.w("Log out ", "Logging out");
                restartActivity();
                break;
        }
    }

/** Toolbar **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    if(mToolbar.getTitle().equals("Pho Tre Bien")){ // Display the settings icon
        getMenuInflater().inflate(R.menu.home_menu, menu);
    }
    if(mToolbar.getTitle().equals("Employee - All Employees")){ // Display the settings icon
        getMenuInflater().inflate(R.menu.employees_menu, menu);
    }
    if(mToolbar.getTitle().equals("Restaurant - All Sections")){ // Display the settings icon
        getMenuInflater().inflate(R.menu.category_menu_main, menu);
    }
    if(mToolbar.getTitle().equals("Inventory - All Inventory")){ // Display the settings icon
        getMenuInflater().inflate(R.menu.menu_main, menu);
    }
    if(mToolbar.getTitle().equals("Prices - All Prices from sellers")){ // Display the settings icon
        getMenuInflater().inflate(R.menu.prices_menu, menu);
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
            mToolbar.setTitle("Employee - All Employees");
            currListView = parser.getAllEmployee();
            updateEmployeesView();
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

/* INVENTORY DROP DOWN */
        if (item.getTitle().equals("Lams")) {
            mToolbar.setTitle("Inventory - Lams Items");
            currListView = parser.getLamsItemsList();
            updateInventoryView();
        }
        if (item.getTitle().equals("Sams")) {
            mToolbar.setTitle("Inventory - Sams Items");
            currListView = parser.getSamsItemsList();
            updateInventoryView();
        }
        if (item.getTitle().equals("Taiwan Trading")) {
            mToolbar.setTitle("Inventory - Taiwan Trading Items");
            currListView = parser.getTaiwanTradingItemsList();
            updateInventoryView();
        }
        if (item.getTitle().equals("Restaurant Depot")) {
            mToolbar.setTitle("Inventory - Restaurant Depot Items");
            currListView = parser.getRestaurantDepotItemsList();
            updateInventoryView();
        }
        if (item.getTitle().equals("Shamrock")) {
            mToolbar.setTitle("Inventory - Shamrock Items");
            currListView = parser.getShamrockItemsList();
            updateInventoryView();
        }
        if (item.getTitle().equals("Cosco")){
            mToolbar.setTitle("Inventory - Cosco Items");
            currListView = parser.getCoscoItemsList();
            updateInventoryView();
        }
        if (item.getTitle().equals("Food King")) {
            mToolbar.setTitle("Inventory - Food Of King Items");
            currListView = parser.getFoodOfKingItemListItemList();
            updateInventoryView();
        }
        if (item.getTitle().equals("All Items")) {
            mToolbar.setTitle("Inventory - All Items");
            currListView = parser.getAllItemsFromDistributors();
            updateInventoryView();
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
                "Pork Roleld Beef", "Rare Steak Brisket", "Shrimp Spring Rolls", "Summer Rolls", "Vietnamese Egg Rolls",
                "Winter Rolls", "Calamari", "Four Seasons Rolls", "Grilled Pork Rolls",
        };
        final String[] imageDetails = {
                "100 pork rolled beef", "", "", "", "", "", "", "", "",
        };

        final FloatingActionButton closePopUp = (FloatingActionButton) findViewById(R.id.closePopup);
        final TextView details = (TextView) findViewById(R.id.details);
        final ImageView imageIcon = (ImageView) findViewById(R.id.imageIcon);
        final Button confirm = (Button) findViewById(R.id.user_add_item);

        swipeRefresh = (RelativeLayout) findViewById(R.id.swipe_refresh);

        /* Pop Ups */
        foodItemPopUp = (RelativeLayout) findViewById(R.id.food_item_selected_pop_up);
        placeOrderPopUp = (RelativeLayout) findViewById(R.id.pop_up);

        /* Views */
        employeesView = (RelativeLayout) findViewById(R.id.employees_tab);
        sectionsView = (RelativeLayout) findViewById(R.id.sections_tab);
        inventoryView = (RelativeLayout) findViewById(R.id.inventory_tab);
        pricesView = (RelativeLayout) findViewById(R.id.prices_tab);
        placeOrderView = (RelativeLayout) findViewById(R.id.place_order_tab);
        loading = (ProgressBar) findViewById(R.id.loadingCircle);

        titleClicked = (TextView) findViewById(R.id.titleClicked);
        addItem = (RelativeLayout) findViewById(R.id.add_item_view);
        addPrice = (RelativeLayout) findViewById(R.id.add_price_view);

        menuGridView = (GridView) findViewById(R.id.grid_view);

        inventoryColumns = (LinearLayout) findViewById(R.id.inventory_columns);
        pricesColumns =  (LinearLayout) findViewById(R.id.prices_columns);
        placeOrderColumns = (LinearLayout) findViewById(R.id.place_order_columns);
        menuView = (LinearLayout) findViewById(R.id.menu_view);

        /* Tables */
        employeesTable = (TableLayout) findViewById(R.id.employee_table);
        sectionsTable = (TableLayout) findViewById(R.id.section_table);
        inventoryTable = (TableLayout) findViewById(R.id.inventory_table);
        pricesTable =  (TableLayout) findViewById(R.id.prices_table);
        placeOrderTable = (TableLayout) findViewById(R.id.place_order_table);

        /* Hide Layouts */
        swipeRefresh.setVisibility(RelativeLayout.GONE);
        foodItemPopUp.setVisibility(RelativeLayout.GONE);
        loading.setVisibility(View.GONE);
        placeOrderPopUp.setVisibility(RelativeLayout.GONE);
        employeesView.setVisibility(RelativeLayout.GONE);
        sectionsView.setVisibility(RelativeLayout.GONE);
        inventoryView.setVisibility(RelativeLayout.GONE);
        pricesView.setVisibility(RelativeLayout.GONE);
        placeOrderView.setVisibility(RelativeLayout.GONE);
        addItem.setVisibility(RelativeLayout.GONE);
        addPrice.setVisibility(RelativeLayout.GONE);

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


/* Employee View **/
    private void launchEmployeesView() {
        menuGridView.setVisibility(RelativeLayout.INVISIBLE);
        addItem.setVisibility(RelativeLayout.INVISIBLE);

        inventoryColumns.setVisibility(LinearLayout.VISIBLE);
        employeesTable.setVisibility(LinearLayout.VISIBLE);
        employeesView.setVisibility(RelativeLayout.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mToolbar.setTitle("Employee - All Employees");

        new talkToDataBase().execute();
        showLoadingCircle();
    }

    /* ----> Employee Table */
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

                    row.addView(nameOfEmployee);
                    row.addView(userName);
                    row.addView(userType);

                    LinearLayout.LayoutParams paramsNameOfEmployee = (LinearLayout.LayoutParams) nameOfEmployee.getLayoutParams();
                    paramsNameOfEmployee.setMargins(pixelToDP(25), 0, pixelToDP(85), pixelToDP(25));

                    LinearLayout.LayoutParams paramsUserName = (LinearLayout.LayoutParams) nameOfEmployee.getLayoutParams();
                    paramsUserName.setMargins(pixelToDP(25), 0, pixelToDP(85), pixelToDP(25)); // Left, Top, Right, Bottom

                    LinearLayout.LayoutParams paramsUserType = (LinearLayout.LayoutParams) nameOfEmployee.getLayoutParams();
                    paramsUserType.setMargins(pixelToDP(25), 0, pixelToDP(85), pixelToDP(25)); // Left, Top, Right, Bottom

                    nameOfEmployee.setLayoutParams(paramsNameOfEmployee);
                    userName.setLayoutParams(paramsUserName);
                    userType.setLayoutParams(paramsUserType);

                    employeesTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout
                            .LayoutParams.WRAP_CONTENT));
                    loading.setVisibility(View.GONE); // Hide loading circle

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
        showLoadingCircle();
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

                    final TextView item1 = new TextView(MainActivity.this);
                    TextView category1 = new TextView(MainActivity.this);

                    String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");
                    Log.w("categories", String.valueOf(currListView.toString()));

                    item1.setText(strArray[0]); //ITEM
                    category1.setText(strArray[1]); // SECTION

                    row.addView(item1);
                    row.addView(category1);

                    LinearLayout.LayoutParams paramsItem = (LinearLayout.LayoutParams) item1.getLayoutParams();
                    paramsItem.setMargins(pixelToDP(25), 0, pixelToDP(0), pixelToDP(25));

                    LinearLayout.LayoutParams paramsCategory = (LinearLayout.LayoutParams) category1.getLayoutParams();
                    paramsCategory.setMargins(pixelToDP(150), 0, 40, pixelToDP(25)); // Left, Top, Right, Bottom

                    item1.setLayoutParams(paramsItem);
                    category1.setLayoutParams(paramsCategory);

                    sectionsTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout
                            .LayoutParams.WRAP_CONTENT));
                    loading.setVisibility(View.GONE); // Hide loading circle

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

        showLoadingCircle();

        Button plusButton = (Button) findViewById(R.id.plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAddItemView();
            }
        });
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

                    final TextView item1 = new TextView(MainActivity.this);
                    TextView category1 = new TextView(MainActivity.this);
                    TextView curr_qty1 = new TextView(MainActivity.this);
                    TextView max_qty1 = new TextView(MainActivity.this);

                    String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");

                    item1.setText(strArray[0]); //ITEM
                    category1.setText(strArray[1]);
                    curr_qty1.setText(strArray[2]);
                    max_qty1.setText(strArray[3]);

                    row.addView(item1);
                    row.addView(category1);
                    row.addView(curr_qty1);
                    row.addView(max_qty1);

                    LinearLayout.LayoutParams paramsItem = (LinearLayout.LayoutParams) item1.getLayoutParams();
                    paramsItem.setMargins(pixelToDP(20), 0, pixelToDP(0), pixelToDP(25));

                    LinearLayout.LayoutParams paramsCategory = (LinearLayout.LayoutParams) category1.getLayoutParams();
                    paramsCategory.setMargins(pixelToDP(25), 0, 10, pixelToDP(25)); // Left, Top, Right, Bottom

                    LinearLayout.LayoutParams paramsCurrentQTY = (LinearLayout.LayoutParams) curr_qty1.getLayoutParams();
                    paramsCurrentQTY.setMargins(pixelToDP(80), 0, 60, pixelToDP(25)); //substitute

                    LinearLayout.LayoutParams paramsMaxQTY = (LinearLayout.LayoutParams) max_qty1.getLayoutParams();
                    paramsMaxQTY.setMargins(pixelToDP(80), 0, 0, pixelToDP(25)); //substitute

                    item1.setLayoutParams(paramsItem);
                    category1.setLayoutParams(paramsCategory);
                    curr_qty1.setLayoutParams(paramsCurrentQTY);
                    max_qty1.setLayoutParams(paramsMaxQTY);

                    inventoryTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));
                    loading.setVisibility(View.GONE); // Hide loading circle

                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Log.w("Name of Item Selected", String.valueOf(item1.getText()));
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
        String[] distributors = {"Select Vendor", "Lams", "Sams", "Taiwan Trading", "Restaurant Depot",
                "Shamrock", "Cosco", "Food King"};

        final EditText etItem = (EditText) findViewById(R.id.food_item);
        final Spinner etSections = (Spinner) findViewById(R.id.et_section);
        final EditText etCurrentQty = (EditText) findViewById(R.id.current_qty);
        final EditText etMaxQty = (EditText) findViewById(R.id.max_qty);
        final Spinner etDistributor = (Spinner) findViewById(R.id.distributor);

        ArrayAdapter<String> adapterForSections = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sections);
        etSections.setAdapter(adapterForSections);

        ArrayAdapter<String> adapterForDistributors = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, distributors);
        etDistributor.setAdapter(adapterForDistributors);

        final EditText etPrices = (EditText) findViewById(R.id.prices);
        final Button btn_addItem = (Button) findViewById(R.id.btn_add_item);
        final TextView view_items = (TextView) findViewById(R.id.view_items);

        /* Clear Form */
        etItem.setText("");
        //etSections.setText("");
        etCurrentQty.setText("");
        etMaxQty.setText("");
        //  etDistributor.setText("");
        etPrices.setText("");

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
                final String category = etSections.getSelectedItem().toString();
                final String currentQty = etCurrentQty.getText().toString();
                final String maxQty = etMaxQty.getText().toString();
                final String distributor = etDistributor.getSelectedItem().toString();
                final String comments = etPrices.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                toast("Success adding item", 10000000);
                                launchInventoryView();
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
                InventoryRequest inventoryRequest = new InventoryRequest(item, category, currentQty, maxQty, distributor,
                        comments, responseListener);
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
        menuGridView.setVisibility(RelativeLayout.INVISIBLE);
        inventoryColumns.setVisibility(LinearLayout.INVISIBLE);
        inventoryTable.setVisibility(LinearLayout.INVISIBLE);
        addItem.setVisibility(RelativeLayout.INVISIBLE);
        swipeRefresh.setVisibility(RelativeLayout.INVISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mToolbar.setTitle("Weekly Count");
    }


/* Prices View **/
    private void launchPricesView() {
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

       // showLoadingCircle();

        Button plusButton = (Button) findViewById(R.id.plus_button_for_prices);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAddPricesView();
                Log.w("Button pressed", "Blah");
            }
        });

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

                    final TextView item1 = new TextView(MainActivity.this);
                    TextView pricePerQty1 = new TextView(MainActivity.this);
                    TextView distributor = new TextView(MainActivity.this);

                    String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");

                    item1.setText(strArray[0]); //ITEM
                    pricePerQty1.setText(strArray[1]);
                    distributor.setText(strArray[2]);


                    row.addView(item1);
                    row.addView(pricePerQty1);
                    row.addView(distributor);

                    LinearLayout.LayoutParams paramsItem = (LinearLayout.LayoutParams) item1.getLayoutParams();
                    paramsItem.setMargins(pixelToDP(20), 0, pixelToDP(0), pixelToDP(25));

                    LinearLayout.LayoutParams paramsCategory = (LinearLayout.LayoutParams) pricePerQty1.getLayoutParams();
                    paramsCategory.setMargins(pixelToDP(25), 0, 10, pixelToDP(25)); // Left, Top, Right, Bottom

                    LinearLayout.LayoutParams paramsCurrentQTY = (LinearLayout.LayoutParams) distributor.getLayoutParams();
                    paramsCurrentQTY.setMargins(pixelToDP(80), 0, 60, pixelToDP(25)); //substitute

                    item1.setLayoutParams(paramsItem);
                    pricePerQty1.setLayoutParams(paramsCategory);
                    distributor.setLayoutParams(paramsCurrentQTY);

                    pricesTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT,
                            DrawerLayout.LayoutParams.WRAP_CONTENT));
                    loading.setVisibility(View.GONE); // Hide loading circle

                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Log.w("Name of Item Selected", String.valueOf(item1.getText()));
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
        mToolbar.setTitle("Add Item to Inventory ");
        /* Prepare data */
        String[] distributors = {"Select Vendor", "Lams", "Sams", "Taiwan Trading", "Restaurant Depot",
                "Shamrock", "Cosco", "Food King"};

        final EditText etItem = (EditText) findViewById(R.id.food_item);
        final EditText etPricePerQty = (EditText) findViewById(R.id.price_per_qty);
        final Spinner etDistributor = (Spinner) findViewById(R.id.distributor);

        ArrayAdapter<String> adapterForDistributors = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, distributors);
        etDistributor.setAdapter(adapterForDistributors);

        final EditText etPrices = (EditText) findViewById(R.id.prices);
        final Button btn_addItem = (Button) findViewById(R.id.btn_add_item);
        final TextView view_items = (TextView) findViewById(R.id.view_items);

        /* Clear Form */
        etItem.setText("");
        etPricePerQty.setText("");
        etPrices.setText("");

        /* Close Dialog  */
        FloatingActionButton close_Popup = (FloatingActionButton) findViewById(R.id.close_Popup);
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

        // User presses the "No account yet? Create one"
        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                PriceRequest inventoryRequest = new PriceRequest(item, currentQty, distributor, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(inventoryRequest);
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
        mToolbar.setTitle("Order - Place an Order");

        recommendedOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        new talkToDataBase().execute();
    }

    /* ----> Place Order Recommended Table */
    private void updatePlaceOrderView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                placeOrderTable.removeAllViews();
                // get a reference for the TableLayout
                for (String aCurrListView : parser.allItemsFromLowStock()) {

                    final TableRow row = new TableRow(MainActivity.this);
                    row.setClickable(true);

                    final TextView item1 = new TextView(MainActivity.this);
                    TextView distributor1 = new TextView(MainActivity.this);
                    TextView price1 = new TextView(MainActivity.this);
                    TextView curr_qty1 = new TextView(MainActivity.this);

                    String strArray[] = aCurrListView.replace("[", "").replace("]", "").replace(",", "").split(" ");
                    Log.w("HERE4", String.valueOf(parser.allItemsFromLowStock().toString()));
                    item1.setText(strArray[0]); //ITEM
                    distributor1.setText(strArray[1]);
                    price1.setText(strArray[2]);
                    curr_qty1.setText(strArray[3]);

                    row.addView(item1);
                    row.addView(distributor1);
                    row.addView(price1);
                    row.addView(curr_qty1);
                    Log.w("item1", item1.getText().toString());
                    LinearLayout.LayoutParams paramsItem = (LinearLayout.LayoutParams) item1.getLayoutParams();
                    paramsItem.setMargins(pixelToDP(20), 0, pixelToDP(0), pixelToDP(25));

                    LinearLayout.LayoutParams paramsDistributor = (LinearLayout.LayoutParams) distributor1.getLayoutParams();
                    paramsDistributor.setMargins(pixelToDP(25), 0, 10, pixelToDP(25)); // Left, Top, Right, Bottom

                    LinearLayout.LayoutParams paramsPrice = (LinearLayout.LayoutParams) price1.getLayoutParams();
                    paramsPrice.setMargins(pixelToDP(80), 0, 60, pixelToDP(25)); //substitute

                    LinearLayout.LayoutParams paramsCurrentQTY = (LinearLayout.LayoutParams) curr_qty1.getLayoutParams();
                    paramsCurrentQTY.setMargins(pixelToDP(80), 0, 0, pixelToDP(25)); //substitute

                    item1.setLayoutParams(paramsItem);
                    distributor1.setLayoutParams(paramsDistributor);
                    price1.setLayoutParams(paramsPrice);
                    curr_qty1.setLayoutParams(paramsCurrentQTY);

                    placeOrderTable.addView(row, new TableLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout
                            .LayoutParams.WRAP_CONTENT));
                    loading.setVisibility(View.GONE); // Hide loading circle

                    row.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Log.w("Name of Item Selected", String.valueOf(item1.getText()));
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
            if(mToolbar.getTitle().equals("Employee - All Employees")){
                String URL_PHP = "http://www.narped.com/inventory/Employees.php";
               json = jParser.makeHttpRequest(URL_PHP, "GET", params);
            }

            if(mToolbar.getTitle().equals("Restaurant - All Sections") || (mToolbar.getTitle().equals("Inventory - " +
                    "All Inventory") || (mToolbar.getTitle().equals("Order - Place an Order")))){
                String URL_PHP = "http://www.narped.com/inventory/ItemsInventory.php";
                json = jParser.makeHttpRequest(URL_PHP, "GET", params);
            }

            if(mToolbar.getTitle().equals("Prices - All Prices from sellers")){
                String URL_PHP = "http://www.narped.com/inventory/Prices.php";
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
                            parser.parseEmployees(JOStuff);
                            parser.parseCategory(JOStuff);
                            parser.parseInventory(JOStuff);
                            parser.parsePrices(JOStuff);
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
            if(mToolbar.getTitle().equals("Employee - All Employees")){
                currListView = parser.getAllEmployee();
                updateEmployeesView();
            }
            if(mToolbar.getTitle().equals("Restaurant - All Sections")){
                currListView = parser.getAllItems();
                updateSectionsView();
            }
            if(mToolbar.getTitle().equals("Inventory - All Inventory")){
                currListView = parser.getAllItemsFromDistributors();
                updateInventoryView();
            }
            if(mToolbar.getTitle().equals("Prices - All Prices from sellers")){
                currListView = parser.getAllItemsFromDistributors();
                updatePricesView();
            }
            if(mToolbar.getTitle().equals("Order - Place an Order")){
                Log.w("HERE2", parser.allItemsFromLowStock().toString());
                currListView = parser.allItemsFromLowStock();
                updatePlaceOrderView();
            }
            super.onPostExecute(aVoid);
        }
    }


/* Show Loading Circle View */
    private void showLoadingCircle() {
        loading.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.GONE); // Hide loading circle
            }
        }, 2500);
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