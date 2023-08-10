package uem.shopan.goease;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private Button btnLogin, btnRegister,registerButton,cancelButton;
    private String username;
    private String password;
    private EditText newUserName, newUserEmail, newPassword1, newPassword2, edtUser,edtPass;
    private TextView output;
    private ProgressDialog progressDialog;

    //public String url = "http://18.221.122.79/android_login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        edtUser = (EditText) findViewById(R.id.user_name);
        edtPass = (EditText) findViewById(R.id.user_pass);
        output = (TextView) findViewById(R.id.textView);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                hideKeybaord(v);

                String username = edtUser.getText().toString();
                String password = edtPass.getText().toString();

                if (!(username.equals("")) && !(password.equals(""))) {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Loading...");
                    //MainActivity.this.progressDialog.setTitle("ProgressDialog");
                    progressDialog.setProgressStyle(0);
                    progressDialog.show();
                    progressDialog.setCancelable(false);

                    new SendHttpRequestTask().execute(new String[]{username, password});
                }
                else {
                    Snackbar.make(findViewById(android.R.id.content),"Please enter Username and Password",Snackbar.LENGTH_LONG).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                if (true) Log.d(TAG, "register");

                user_registration();
            }
        });
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
            String data = LoginActivity.this.sendHttpRequest(Constants.SERVER_ADDRESS+Constants.LOGIN_PAGE, user_name, user_pass);
            PrintStream printStream = System.out;
            printStream.println("Data [" + data + "]");
            return data;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            //MainActivity.this.output.setText(result);
            LoginActivity.this.progressDialog.dismiss();

            try {
                //String database = "1,Device a:2,Device b:3,Device c:4,Device d";
                String node_list[] = result.split("!");

                for (int i =0;i<node_list.length;i++) {
                    if (Constants.D) Log.d(TAG, "List: " + node_list[i]);
                }


                if(node_list[0].equals(Constants.LOGIN_SUCCESS)) {
                    if (Constants.D) Log.d(TAG, "login_success");
                    Snackbar.make(findViewById(android.R.id.content),"Login Success",Snackbar.LENGTH_LONG).show();
                    //output.setText("Done");
                    /*
                    Intent i = new Intent(MainActivity.this, DeviceListActivity.class);
                    i.putExtra(Constants.USERNAME, user_name);
                    i.putExtra(Constants.PASSWORD, user_pass);
                    i.putExtra(Constants.LOGIN_NAME, node_list[1]);
                    i.putExtra(Constants.LOGIN_NODE_LIST, node_list[2]);
                    */

                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                    editor.putString(Constants.USERNAME, user_name);
                    editor.putString(Constants.PASSWORD, user_pass);
                    editor.putString(Constants.LOGIN_NAME, node_list[1]);
                    editor.putString(Constants.LOGIN_NODE_LIST, node_list[2]);
                    editor.apply();

                    //startActivity(i);
                    //finish();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    if (Constants.D) Log.d(TAG, "login_fail");
                    //output.setText("Login Error !!!");
                    Snackbar.make(findViewById(android.R.id.content),"User not found !!!",Snackbar.LENGTH_LONG).show();
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

    private void exitDialog(){

        final Dialog exitDialog = new Dialog(LoginActivity.this);
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

    private void user_registration(){

        final Dialog registerDialog = new Dialog(LoginActivity.this);
        registerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        registerDialog.setCancelable(false);
        registerDialog.setContentView(R.layout.dialog_register);
        registerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        FrameLayout mDialogNo = registerDialog.findViewById(R.id.frmNo);
        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                registerDialog.dismiss();
            }
        });

        FrameLayout mDialogOk = registerDialog.findViewById(R.id.frmOk);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Okay" ,Toast.LENGTH_SHORT).show();


                registerDialog.dismiss();

            }
        });

        registerDialog.show();

    }

}
