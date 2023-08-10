package uem.shopan.goease;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static final String TAG = "MainActivity";

    private RecyclerView courseRV;
    private Button okButton,cancelButton;
    private Dialog aboutUsDialog, changePassword, newDeviceDialog, testDialog;
    private EditText newUserID, newPassword;
    private TextView loginName, navUsername, navUserEmail;
    ProgressDialog progressDialog;

    String username;
    String password;

    // Arraylist for storing data
    private ArrayList<cardModel> courseModelArrayList;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.navUsername);
        navUserEmail = (TextView) headerView.findViewById(R.id.navUserEmail);

        courseRV = findViewById(R.id.idRVCourse);

        checkNetwork();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String username = sharedPreferences.getString(Constants.USERNAME, "");
        String password = sharedPreferences.getString(Constants.PASSWORD, "");

        if((!username.equals(""))&(!password.equals(""))) {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            //MainActivity.this.progressDialog.setTitle("ProgressDialog");
            progressDialog.setProgressStyle(0);
            progressDialog.show();
            progressDialog.setCancelable(false);

            new SendHttpRequestTask().execute(new String[]{username, password});

        } else {
            if (Constants.D) Log.d(TAG, "Login failed");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Add new device", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                addNewDevice();

                //testDialog();

            }
        });

    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_account: {
                //do somthing
                Log.d(TAG, "account");
                break;
            }
            case R.id.nav_settings: {
                //do somthing
                Log.d(TAG, "setting");
                break;
            }
            case R.id.nav_change_password: {
                Log.d(TAG, "Change Password");
                change_password();
                break;
            }
            case R.id.nav_logout: {
                //do somthing
                Log.d(TAG, "logout");
                logoutDialog();
                break;
            }
            case R.id.nav_about:{
                Log.d(TAG, "about us");
                show_about_us();
                break;
            }
            case R.id.nav_exit:{
                Log.d(TAG, "exit");
                exitDialog();
                break;
            }
            default:
                return true; //close the drawer when click
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {

        private String user_name;
        private String user_pass;

        private SendHttpRequestTask() {
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            user_name = params[0];
            user_pass = params[1];
            String data = MainActivity.this.sendHttpRequest(Constants.SERVER_ADDRESS+Constants.LOGIN_PAGE, user_name, user_pass);
            PrintStream printStream = System.out;
            printStream.println("Data [" + data + "]");
            return data;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            //MainActivity.this.output.setText(result);
            MainActivity.this.progressDialog.dismiss();

            try {
                //String database = "1,Device a:2,Device b:3,Device c:4,Device d";
                String data_list[] = result.split("!");

                for (int i =0;i<data_list.length;i++) {
                    if (Constants.D) Log.d(TAG, "List: " + data_list[i]);
                }


                if(data_list[0].equals(Constants.LOGIN_SUCCESS)) {
                    if (Constants.D) Log.d(TAG, "login_success");
                    //textView.setText(view);
                    /*
                    Intent i = new Intent(MainActivity.this, DeviceListActivity.class);
                    i.putExtra(Constants.USERNAME, user_name);
                    i.putExtra(Constants.PASSWORD, user_pass);
                    i.putExtra(Constants.LOGIN_NAME, node_list[1]);
                    i.putExtra(Constants.LOGIN_NODE_LIST, data_list[2]);
                    */
                    navUsername.setText(data_list[1]);
                    navUserEmail.setText(user_name);

                    courseModelArrayList = new ArrayList<>();

                    //String database = "1,Device a;2,Device b;3,Device c;4,Device d";
                    String node_list[] = data_list[2].split(";");

                    final String[] device_id = new String[node_list.length];
                    final String[] device_key = new String[node_list.length];
                    final String[] input_data = new String[node_list.length];
                    final String[] output_1 = new String[node_list.length];
                    final String[] output_2 = new String[node_list.length];
                    final String[] output_3 = new String[node_list.length];
                    final String[] output_4 = new String[node_list.length];
                    final String[] status = new String[node_list.length];

                    for (int i =0;i<node_list.length;i++) {

                        String node_data[] = node_list[i].split(",");
                        int length = node_data.length;
                        device_id[i]=node_data[1];
                        device_key[i]=node_data[2];
                        input_data[i]=node_data[3];
                        status[i]=node_data[length-1];
                        //output_1[i]=node_data[4];
                        //output_2[i]=node_data[5];
                        //output_3[i]=node_data[6];
                        //output_4[i]=node_data[7];
                        //status[i]=node_data[8];
                        //firstTwo
                        String key = firstTwo(node_data[1]);
                        int imgID = 0;
                        if (key.equals("SL")){
                            imgID = R.drawable.lamp;
                        }
                        else if (key.equals("SB")){
                            imgID = R.drawable.swtich;
                        }
                        else if (key.equals("DL")){
                            imgID = R.drawable.lock;
                        }
                        else if (key.equals("SR")){
                            imgID = R.drawable.remote;
                        }
                        else if (key.equals("SP")){
                            imgID = R.drawable.plug;
                        }
                        else {
                            imgID = R.drawable.light;
                        }

                        // here we have created new array list and added data to it.
                        courseModelArrayList.add(new cardModel(node_data[0], node_data[1],node_data[2] , imgID));
                    }

                    // we are initializing our adapter class and passing our arraylist to it.
                    cardAdapter courseAdapter = new cardAdapter(MainActivity.this, courseModelArrayList);

                    // below line is for setting a layout manager for our recycler view.
                    // here we are creating vertical list so we will provide orientation as vertical
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);

                    // in below two lines we are setting layoutmanager and adapter to our recycler view.
                    courseRV.setLayoutManager(linearLayoutManager);
                    courseRV.setAdapter(courseAdapter);

                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                    editor.putString(Constants.USERNAME, user_name);
                    editor.putString(Constants.PASSWORD, user_pass);
                    editor.putString(Constants.LOGIN_NAME, node_list[1]);
                    editor.putString(Constants.LOGIN_NODE_LIST, node_list[2]);
                    editor.apply();

                    //startActivity(i);
                    //finish();
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public String sendHttpRequest(String url, String name, String password) {
        StringBuffer buffer = new StringBuffer();
        try {
            PrintStream printStream = System.out;
            printStream.println("URL [" + url + "] - Name [" + name + "]");
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            OutputStream outputStream = con.getOutputStream();
            outputStream.write(("username=" + name).getBytes());
            outputStream.write(("&password=" + password).getBytes());
            //con.getOutputStream().write("&id=733".getBytes());

            InputStream is = con.getInputStream();
            byte[] b = new byte[1024];
            while (is.read(b) != -1) {
                buffer.append(new String(b));
            }
            con.disconnect();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return buffer.toString();
    }

    private void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }

    /*
     * Check if Internet is enabled. Otherwise exit.
     */
    private void checkNetwork() {
        if (Constants.D) Log.d(TAG, "checkNetwork()");
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            if (Constants.D) Log.d(TAG, "Not Connected");
            new AlertDialog.Builder(this)
                    .setTitle(R.string.no_internet_title)
                    .setMessage(R.string.no_internet_message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        } else {
            if (Constants.D) Log.d(TAG, "Connected");
        }
    }

    @Override
    public void onBackPressed() {
        if (Constants.D) Log.d(TAG, "onBackPressed()");
        //finish();
        exitDialog();
    }

    private void show_about_us(){

        aboutUsDialog = new Dialog(MainActivity.this);
        aboutUsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        aboutUsDialog.setContentView(R.layout.dialog_about_us);
        //lampDialog.setTitle(R.string.action_control);
        aboutUsDialog.setCancelable(false);
        aboutUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView cancelButton = (ImageView) aboutUsDialog.findViewById(R.id.cancelButton);
        if (cancelButton == null)
            Log.d(TAG, "Cancel");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            //@SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancel.onClick()");

                aboutUsDialog.dismiss();

            }
        });

        aboutUsDialog.show();

    }

    private void change_password(){

        changePassword = new Dialog(MainActivity.this);
        changePassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changePassword.setContentView(R.layout.dialog_change_password);
        //lampDialog.setTitle(R.string.action_control);
        changePassword.setCancelable(false);
        changePassword.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        FrameLayout mDialogNo = changePassword.findViewById(R.id.frmNo);
        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                changePassword.dismiss();
            }
        });

        FrameLayout mDialogOk = changePassword.findViewById(R.id.frmOk);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Okay" ,Toast.LENGTH_SHORT).show();

                changePassword.dismiss();

            }
        });

        changePassword.show();

    }

    private void addNewDevice(){

        newDeviceDialog = new Dialog(MainActivity.this);
        newDeviceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newDeviceDialog.setCancelable(false);
        newDeviceDialog.setContentView(R.layout.dialog_newdevice);
        newDeviceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        FrameLayout mDialogNo = newDeviceDialog.findViewById(R.id.frmNo);
        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                newDeviceDialog.dismiss();
            }
        });

        FrameLayout mDialogOk = newDeviceDialog.findViewById(R.id.frmOk);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Okay" ,Toast.LENGTH_SHORT).show();
                newDeviceDialog.cancel();
            }
        });

        newDeviceDialog.show();

    }

    private void testDialog(){

        testDialog = new Dialog(MainActivity.this);
        testDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        testDialog.setCancelable(false);
        testDialog.setContentView(R.layout.newcustom_layout);
        testDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txtMessage = (TextView) testDialog.findViewById(R.id.txtMessage);
        txtMessage.setText("Want to quit ?");

        FrameLayout mDialogNo = testDialog.findViewById(R.id.frmNo);
        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                testDialog.dismiss();
            }
        });

        FrameLayout mDialogOk = testDialog.findViewById(R.id.frmOk);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Okay" ,Toast.LENGTH_SHORT).show();
                testDialog.dismiss();
            }
        });

        testDialog.show();

    }

    private void exitDialog(){

        final Dialog exitDialog = new Dialog(MainActivity.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setCancelable(false);
        exitDialog.setContentView(R.layout.dialog_info);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txtMessage = (TextView) exitDialog.findViewById(R.id.txtMessage);
        txtMessage.setText("Want to quit ?");

        FrameLayout mDialogNo = exitDialog.findViewById(R.id.frmNo);
        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                exitDialog.dismiss();
            }
        });

        FrameLayout mDialogOk = exitDialog.findViewById(R.id.frmOk);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Okay" ,Toast.LENGTH_SHORT).show();
                exitDialog.dismiss();
                finish();
            }
        });

        exitDialog.show();

    }

    private void logoutDialog(){

        final Dialog exitDialog = new Dialog(MainActivity.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setCancelable(false);
        exitDialog.setContentView(R.layout.dialog_info);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txtMessage = (TextView) exitDialog.findViewById(R.id.txtMessage);
        txtMessage.setText("Want to logout ?");

        FrameLayout mDialogNo = exitDialog.findViewById(R.id.frmNo);
        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                exitDialog.dismiss();
            }
        });

        FrameLayout mDialogOk = exitDialog.findViewById(R.id.frmOk);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Okay" ,Toast.LENGTH_SHORT).show();
                exitDialog.dismiss();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString(Constants.USERNAME, "");
                editor.putString(Constants.PASSWORD, "");
                editor.putString(Constants.LOGIN_NAME, "");
                editor.putString(Constants.LOGIN_NODE_LIST, "");
                editor.apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        exitDialog.show();

    }

    public String firstTwo(String str) {

        if(str.length()<2){
            return str;
        }
        else{
            return str.substring(0,2);
        }
    }
}

