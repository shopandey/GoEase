package uem.shopan.goease;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Details extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    TextView textTitle,txtSW1,txtSW2,txtSW3,txtSW4, txtNodeID, txtNodeName, txtNodeInput;

    private Button bntFunctio,colorButton,cancelButton,okButton;
    //private Dialog registerDialog;
    private RecyclerView RemoteList;
    private Dialog switchDialog, changeNameDialog, lampDialog, lockDialog, remoteDialog, remoteListDialog;
    private EditText txtName;
    private SwitchCompat swOnOff1, swOnOff2, swOnOff3, swOnOff4;
    private ProgressDialog progressDialog;
    private  String m_Text = "", ApplianceName, device_name, device_id, device_key, SwOnOff_label_1, SwOnOff_label_2, SwOnOff_label_3, SwOnOff_label_4;
    private SeekBar lblBrightness;
    private ImageView imgColor, imgLockStatus, imgLockControl;
    private Button btnCmd11, btnCmd12, btnCmd13, btnCmd21, btnCmd22, btnCmd23, btnCmd31, btnCmd32, btnCmd33;
    private ListView lstRemote;
    private int pwm, red, green, blue;
    private Button[] btnCmd = new Button[27];
    private String[] sName = new String[27];
    private String sAddress, sType;
    private String[] sCommand = new String[27];
    private String[] sRepeat = new String[27];

    /** Items entered by the user is stored in this ArrayList variable */
    private ArrayList<cardRemoteModel> courseRemoteModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent i = getIntent();
        String  page = i.getStringExtra("page");
        device_name = i.getStringExtra("name");
        device_id = i.getStringExtra("id");
        device_key = i.getStringExtra("key");

        if (page.equals("1")) {

            progressDialog = new ProgressDialog(Details.this);
            progressDialog.setMessage("Loading...");
            //MainActivity.this.progressDialog.setTitle("ProgressDialog");
            progressDialog.setProgressStyle(0);
            progressDialog.show();
            progressDialog.setCancelable(false);

            new SendHttpRequestTask().execute(new String[]{Constants.SERVER_ADDRESS + Constants.DEVICE_INFO_PAGE, device_id, device_key, ""});

        }
        else if (page.equals("2")) {

            String  remote_id = i.getStringExtra("remote_id");

            remote_data(remote_id);
            remote_control();

        }
        //textTitle = findViewById(R.id.textView);
        //textTitle.setText(title);

    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {

        private String user_name;
        private String user_pass;
        private String device_data;

        private SendHttpRequestTask() {
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            user_name = params[1];
            user_pass = params[2];
            device_data = params[3];
            String data = Details.this.sendHttpRequest(params[0], user_name, user_pass, device_data);
            PrintStream printStream = System.out;
            printStream.println("Data [" + data + "]");
            return data;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            //MainActivity.this.output.setText(result);
            Details.this.progressDialog.dismiss();

            try {
                //String database = "1,Device a:2,Device b:3,Device c:4,Device d";
                String data_list[] = result.split(",");

                for (int i =0;i<data_list.length;i++) {
                    if (Constants.D) Log.d(TAG, "List: " + data_list[i]);
                }


                if(data_list[0].equals(Constants.LOGIN_SUCCESS)) {
                    if (Constants.D) Log.d(TAG, "device_login_success");
                    if (data_list.length>2)
                    {
                        if (firstTwo(device_id).equals("SL")){
                            pwm = Integer.parseInt(data_list[3]);
                            red = Integer.parseInt(data_list[4]);
                            green = Integer.parseInt(data_list[5]);
                            blue = Integer.parseInt(data_list[6]);
                            lamp_control();
                        }
                        else if (firstTwo(device_id).equals("SB")){
                            switch_control(getValue(data_list[3]),getValue(data_list[4]),getValue(data_list[5]),getValue(data_list[6]));
                        }
                        else if (firstTwo(device_id).equals("DL")){
                            lock_control(getValue(data_list[2]));
                        }
                        else if (firstTwo(device_id).equals("SR")){
                            //remote_control();
                            remote_list();
                        }
                        else if (firstTwo(device_id).equals("SP")){
                            ;
                        }
                        else {
                            switch_control(getValue(data_list[3]),getValue(data_list[4]),getValue(data_list[5]),getValue(data_list[6]));
                        }
                    }
                }
                else{
                    if (Constants.D) Log.d(TAG, "login_fail");
                    //output.setText("Error");
                    Snackbar.make(findViewById(android.R.id.content),"Device not found !!!",Snackbar.LENGTH_LONG).show();
                    finish();
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public String sendHttpRequest(String url, String name, String password, String outputData) {
        StringBuffer buffer = new StringBuffer();
        try {
            PrintStream printStream = System.out;
            printStream.println("URL [" + url + "] - Name [" + name + "] - Password [" + password + "] - Output [" + outputData + "]");
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            OutputStream outputStream = con.getOutputStream();
            outputStream.write(("id=" + name).getBytes());
            outputStream.write(("&key=" + password).getBytes());
            outputStream.write(("&output_data=" + outputData).getBytes());
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

    private void get_name(final int num){

        final Dialog changeNameDialog = new Dialog(Details.this);
        changeNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changeNameDialog.setCancelable(false);
        changeNameDialog.setContentView(R.layout.dialog_swname);
        changeNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ApplianceName = "";

        FrameLayout mDialogNo = changeNameDialog.findViewById(R.id.frmNo);
        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                changeNameDialog.dismiss();
            }
        });

        FrameLayout mDialogOk = changeNameDialog.findViewById(R.id.frmOk);
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Okay" ,Toast.LENGTH_SHORT).show();
                txtName = (EditText) changeNameDialog.findViewById(R.id.newApplianceName);

                //ApplianceName = txtName.getText().toString();

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Details.this).edit();

                if (num == 1){
                    swOnOff1.setText(txtName.getText().toString());
                    editor.putString(device_id+"N1",txtName.getText().toString());
                }
                else if (num == 2){
                    swOnOff2.setText(txtName.getText().toString());
                    editor.putString(device_id+"N2",txtName.getText().toString());
                }
                else if (num == 3){
                    swOnOff3.setText(txtName.getText().toString());
                    editor.putString(device_id+"N3",txtName.getText().toString());
                }
                else if (num == 4){
                    swOnOff4.setText(txtName.getText().toString());
                    editor.putString(device_id+"N4",txtName.getText().toString());
                }
                else ;

                editor.apply();

                changeNameDialog.dismiss();

            }
        });

        changeNameDialog.show();

    }

    private void switch_control(boolean sw1, boolean sw2, boolean sw3, boolean sw4){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Details.this);
        SwOnOff_label_1 = sharedPreferences.getString(device_id+"N1", "Switch 1");
        SwOnOff_label_2 = sharedPreferences.getString(device_id+"N2", "Switch 2");
        SwOnOff_label_3 = sharedPreferences.getString(device_id+"N3", "Switch 3");
        SwOnOff_label_4 = sharedPreferences.getString(device_id+"N4", "Switch 4");

        switchDialog = new Dialog(Details.this);
        switchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        switchDialog.setContentView(R.layout.dialog_switch);
        switchDialog.setTitle(R.string.action_control);
        switchDialog.setCancelable(false);
        switchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        txtNodeID = (TextView) switchDialog.findViewById(R.id.txtNodeID);
        txtNodeName= (TextView) switchDialog.findViewById(R.id.txtNodeName);
        //txtNodeInput = (TextView) switchDialog.findViewById(R.id.txtNodeInput);

        txtNodeID.setText(device_id);
        txtNodeName.setText(device_name);

        swOnOff1 = (SwitchCompat) switchDialog.findViewById(R.id.swOnOff1);
        swOnOff2 = (SwitchCompat) switchDialog.findViewById(R.id.swOnOff2);
        swOnOff3 = (SwitchCompat) switchDialog.findViewById(R.id.swOnOff3);
        swOnOff4 = (SwitchCompat) switchDialog.findViewById(R.id.swOnOff4);

        swOnOff1.setChecked(sw1);
        swOnOff2.setChecked(sw2);
        swOnOff3.setChecked(sw3);
        swOnOff4.setChecked(sw4);

        /*
        swOnOff1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "On click sw1");
            }
        });
        */

        CompoundButton.OnCheckedChangeListener multiListener = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {


                String output_data = (swOnOff1.isChecked() ? "1" : "0") + "," + (swOnOff2.isChecked() ? "1" : "0") + "," + (swOnOff3.isChecked() ? "1" : "0") + ","  + (swOnOff4.isChecked() ? "1" : "0");

                if (Constants.D) Log.d(TAG, "SW: "+output_data);

                progressDialog = new ProgressDialog(Details.this);
                progressDialog.setMessage("Loading...");
                //MainActivity.this.progressDialog.setTitle("ProgressDialog");
                progressDialog.setProgressStyle(0);
                progressDialog.show();
                progressDialog.setCancelable(false);

                new SendHttpRequestTask().execute(new String[]{Constants.SERVER_ADDRESS+Constants.DEVICE_CONTROL_PAGE, device_id, device_key, output_data});

            }
        };

        swOnOff1.setOnCheckedChangeListener(multiListener);
        swOnOff2.setOnCheckedChangeListener(multiListener);
        swOnOff3.setOnCheckedChangeListener(multiListener);
        swOnOff4.setOnCheckedChangeListener(multiListener);

        swOnOff1.setText(SwOnOff_label_1);
        swOnOff2.setText(SwOnOff_label_2);
        swOnOff3.setText(SwOnOff_label_3);
        swOnOff4.setText(SwOnOff_label_4);

        swOnOff1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "Long click sw1");

                get_name(1); //ApplianceName
                //txtSW1.setText(get_name());
                return true;
            }
        });

        swOnOff2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "Long click sw2");

                get_name(2); //ApplianceName
                //txtSW1.setText(get_name());
                return true;
            }
        });

        swOnOff3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "Long click sw3");

                get_name(3); //ApplianceName
                //txtSW1.setText(get_name());
                return true;
            }
        });

        swOnOff4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "Long click sw4");

                get_name(4); //ApplianceName
                //txtSW1.setText(get_name());
                return true;
            }
        });


        ImageView cancelButton = (ImageView) switchDialog.findViewById(R.id.cancelButton);
        if (cancelButton == null)
            Log.d(TAG, "Cancel");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            //@SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancel.onClick()");

                switchDialog.dismiss();
                finish();

            }
        });
        switchDialog.show();
    }

    private void lamp_control(){
        lampDialog = new Dialog(Details.this);
        lampDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lampDialog.setContentView(R.layout.dialog_lamp);
        //lampDialog.setTitle(R.string.action_control);
        lampDialog.setCancelable(false);
        lampDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        txtNodeID = (TextView) lampDialog.findViewById(R.id.txtNodeID);
        txtNodeName= (TextView) lampDialog.findViewById(R.id.txtNodeName);
        //txtNodeInput = (TextView) lampDialog.findViewById(R.id.txtNodeInput);

        txtNodeID.setText(device_id);
        txtNodeName.setText(device_name);

        lblBrightness = (SeekBar) lampDialog.findViewById(R.id.lblBrightness);
        lblBrightness.setProgress(pwm);

        imgColor = (ImageView) lampDialog.findViewById(R.id.colorImage);
        imgColor.setBackgroundColor(android.graphics.Color.rgb( red, green, blue));

        /*
        swOnOff1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "On click sw1");
            }
        });
        */
        colorButton = (Button) lampDialog.findViewById(R.id.colorButton);
        if (colorButton == null)
            Log.d(TAG, "Color");
        colorButton.setOnClickListener(new View.OnClickListener() {
            //@SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Color.onClick()");

                //int RGB = android.graphics.Color.rgb( r, g, b);

                ColorPickerDialogBuilder
                        .with(Details.this)
                        .initialColor(android.graphics.Color.rgb( red, green, blue))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .lightnessSliderOnly()
                        .density(9)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                // Nothing to do...
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int selectedColor, Integer[] allColors) {

                                red = Color.red(selectedColor);
                                green = Color.green(selectedColor);
                                blue = Color.blue(selectedColor);
                                int alpha = Color.alpha(selectedColor);

                                Log.d("RGB", "R [" + red + "] - G [" + green + "] - B [" + blue + "]");

                                imgColor.setBackgroundColor(selectedColor);

                                //sendColor(r, g, b);

                                int pwm = lblBrightness.getProgress();

                                String output_data = pwm + "," + red + "," + green + "," + blue;
                                if (Constants.D) Log.d(TAG, "SW: "+output_data);

                                progressDialog = new ProgressDialog(Details.this);
                                progressDialog.setMessage("Loading...");
                                //MainActivity.this.progressDialog.setTitle("ProgressDialog");
                                progressDialog.setProgressStyle(0);
                                progressDialog.show();
                                progressDialog.setCancelable(false);

                                new SendHttpRequestTask().execute(new String[]{Constants.SERVER_ADDRESS+Constants.DEVICE_CONTROL_PAGE, device_id, device_key, output_data});
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .build()
                        .show();
            }
        });

        ImageView cancelButton = (ImageView) lampDialog.findViewById(R.id.cancelButton);
        if (cancelButton == null)
            Log.d(TAG, "Cancel");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            //@SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancel.onClick()");

                lampDialog.dismiss();
                finish();

            }
        });
        lampDialog.show();
    }

    private void lock_control(boolean lockIsOpen){

        lockDialog = new Dialog(Details.this);
        lockDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lockDialog.setContentView(R.layout.dialog_smartlock);
        lockDialog.setCancelable(false);
        lockDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        txtNodeID = (TextView) lockDialog.findViewById(R.id.txtNodeID);
        txtNodeName= (TextView) lockDialog.findViewById(R.id.txtNodeName);
        //txtNodeInput = (TextView) lockDialog.findViewById(R.id.txtNodeInput);

        txtNodeID.setText(device_id);
        txtNodeName.setText(device_name);

        //imgLockStatus = (ImageView) lockDialog.findViewById(R.id.imgLockStatus);
        imgLockStatus = (ImageView)  lockDialog.findViewById(R.id.imgLockStatus);
        if (imgLockStatus == null)
            Log.d(TAG, "Color");
        imgLockStatus.setOnClickListener(new View.OnClickListener() {
            //@SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "LockStatus.onClick()");

                progressDialog = new ProgressDialog(Details.this);
                progressDialog.setMessage("Loading...");
                //MainActivity.this.progressDialog.setTitle("ProgressDialog");
                progressDialog.setProgressStyle(0);
                progressDialog.show();
                progressDialog.setCancelable(false);

                new SendHttpRequestTask().execute(new String[]{Constants.SERVER_ADDRESS + Constants.DEVICE_INFO_PAGE, device_id, device_key, ""});


            }
        });

        //imgLockControl = (ImageView) lockDialog.findViewById(R.id.imgLockControl);
        imgLockControl = (ImageView)  lockDialog.findViewById(R.id.imgLockControl);
        if (imgLockControl == null)
            Log.d(TAG, "Color");
        imgLockControl.setOnClickListener(new View.OnClickListener() {
            //@SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "LockControl.onClick()");

                progressDialog = new ProgressDialog(Details.this);
                progressDialog.setMessage("Loading...");
                //MainActivity.this.progressDialog.setTitle("ProgressDialog");
                progressDialog.setProgressStyle(0);
                progressDialog.show();
                progressDialog.setCancelable(false);

                new SendHttpRequestTask().execute(new String[]{Constants.SERVER_ADDRESS+Constants.DEVICE_CONTROL_PAGE, device_id, device_key, "1"});


            }
        });

        ImageView cancelButton = (ImageView) lockDialog.findViewById(R.id.cancelButton);
        if (cancelButton == null)
            Log.d(TAG, "Cancel");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            //@SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancel.onClick()");

                lockDialog.dismiss();
                finish();

            }
        });

        if (lockIsOpen){
            imgLockStatus.setBackgroundResource(R.drawable.unlock_icon);
        }
        else{
            imgLockStatus.setBackgroundResource(R.drawable.lock_icon);
        }

        lockDialog.show();
    }

    private void remote_control(){

        remoteDialog = new Dialog(Details.this);
        remoteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        remoteDialog.setContentView(R.layout.dialog_remote);
        //lampDialog.setTitle(R.string.action_control);
        remoteDialog.setCancelable(false);
        remoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnCmd[0] = (Button) remoteDialog.findViewById(R.id.btnCmd1);
        btnCmd[1] = (Button) remoteDialog.findViewById(R.id.btnCmd2);
        btnCmd[2]  = (Button) remoteDialog.findViewById(R.id.btnCmd3);
        btnCmd[3]  = (Button) remoteDialog.findViewById(R.id.btnCmd4);
        btnCmd[4]  = (Button) remoteDialog.findViewById(R.id.btnCmd5);
        btnCmd[5]  = (Button) remoteDialog.findViewById(R.id.btnCmd6);
        btnCmd[6]  = (Button) remoteDialog.findViewById(R.id.btnCmd7);
        btnCmd[7]  = (Button) remoteDialog.findViewById(R.id.btnCmd8);
        btnCmd[8]  = (Button) remoteDialog.findViewById(R.id.btnCmd9);

        btnCmd[9] = (Button) remoteDialog.findViewById(R.id.btnCmd10);
        btnCmd[10] = (Button) remoteDialog.findViewById(R.id.btnCmd11);
        btnCmd[11]  = (Button) remoteDialog.findViewById(R.id.btnCmd12);
        btnCmd[12]  = (Button) remoteDialog.findViewById(R.id.btnCmd13);
        btnCmd[13]  = (Button) remoteDialog.findViewById(R.id.btnCmd14);
        btnCmd[14]  = (Button) remoteDialog.findViewById(R.id.btnCmd15);
        btnCmd[15]  = (Button) remoteDialog.findViewById(R.id.btnCmd16);
        btnCmd[16]  = (Button) remoteDialog.findViewById(R.id.btnCmd17);
        btnCmd[17]  = (Button) remoteDialog.findViewById(R.id.btnCmd18);

        btnCmd[18] = (Button) remoteDialog.findViewById(R.id.btnCmd19);
        btnCmd[19] = (Button) remoteDialog.findViewById(R.id.btnCmd20);
        btnCmd[20]  = (Button) remoteDialog.findViewById(R.id.btnCmd21);
        btnCmd[21]  = (Button) remoteDialog.findViewById(R.id.btnCmd22);
        btnCmd[22]  = (Button) remoteDialog.findViewById(R.id.btnCmd23);
        btnCmd[23]  = (Button) remoteDialog.findViewById(R.id.btnCmd24);
        btnCmd[24]  = (Button) remoteDialog.findViewById(R.id.btnCmd25);
        btnCmd[25]  = (Button) remoteDialog.findViewById(R.id.btnCmd26);
        btnCmd[26]  = (Button) remoteDialog.findViewById(R.id.btnCmd27);

        for (int i=0; i<27; i++){
            if (sName[i].equals("")){
                btnCmd[i].setText(sName[i]);
                btnCmd[i].setEnabled(false);
            }
            else {
                btnCmd[i].setText(sName[i]);
                btnCmd[i].setEnabled(true);
            }
        }

        CompoundButton.OnClickListener multiListener = new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {

                // name[], sAddress[], sCommand[], sRepeat[];
               int cmd = 0;

                switch(view.getId()) {
                    case R.id.btnCmd1:
                        //write your code here
                        cmd = 0;
                        break;

                    case R.id.btnCmd2:
                        //write your code here
                        cmd = 1;
                        break;

                    case R.id.btnCmd3:
                        //write your code here
                        cmd = 2;
                        break;

                    case R.id.btnCmd4:
                        //write your code here
                        cmd = 3;
                        break;

                    case R.id.btnCmd5:
                        //write your code here
                        cmd = 4;
                        break;

                    case R.id.btnCmd6:
                        //write your code here
                        cmd = 5;
                        break;

                    case R.id.btnCmd7:
                        //write your code here
                        cmd = 6;
                        break;

                    case R.id.btnCmd8:
                        //write your code here
                        cmd = 7;
                        break;

                    case R.id.btnCmd9:
                        //write your code here
                        cmd = 8;
                        break;

                    case R.id.btnCmd10:
                        //write your code here
                        cmd = 9;
                        break;

                    case R.id.btnCmd11:
                        //write your code here
                        cmd = 10;
                        break;

                    case R.id.btnCmd12:
                        //write your code here
                        cmd = 11;
                        break;

                    case R.id.btnCmd13:
                        //write your code here
                        cmd = 12;
                        break;

                    case R.id.btnCmd14:
                        //write your code here
                        cmd = 13;
                        break;

                    case R.id.btnCmd15:
                        //write your code here
                        cmd = 14;
                        break;

                    case R.id.btnCmd16:
                        //write your code here
                        cmd = 15;
                        break;

                    case R.id.btnCmd17:
                        //write your code here
                        cmd = 16;
                        break;

                    case R.id.btnCmd18:
                        //write your code here
                        cmd = 17;
                        break;

                    case R.id.btnCmd19:
                        //write your code here
                        cmd = 18;
                        break;

                    case R.id.btnCmd20:
                        //write your code here
                        cmd = 19;
                        break;

                    case R.id.btnCmd21:
                        //write your code here
                        cmd = 20;
                        break;

                    case R.id.btnCmd22:
                        //write your code here
                        cmd = 21;
                        break;

                    case R.id.btnCmd23:
                        //write your code here
                        cmd = 22;
                        break;

                    case R.id.btnCmd24:
                        //write your code here
                        cmd = 23;
                        break;

                    case R.id.btnCmd25:
                        //write your code here
                        cmd = 24;
                        break;

                    case R.id.btnCmd26:
                        //write your code here
                        cmd = 25;
                        break;

                    case R.id.btnCmd27:
                        //write your code here
                        cmd = 26;
                        break;

                    default:

                        break;

                }

                String output_data = "true," + sType + "," + sAddress + "," + sCommand[cmd] + "," + sRepeat[cmd];
                //String output_data = (swOnOff1.isChecked() ? "1" : "0") + "," + (swOnOff2.isChecked() ? "1" : "0") + "," + (swOnOff3.isChecked() ? "1" : "0") + ","  + (swOnOff4.isChecked() ? "1" : "0");

                if (Constants.D) Log.d(TAG, "output: "+output_data);

                progressDialog = new ProgressDialog(Details.this);
                progressDialog.setMessage("Loading...");
                //MainActivity.this.progressDialog.setTitle("ProgressDialog");
                progressDialog.setProgressStyle(0);
                progressDialog.show();
                progressDialog.setCancelable(false);

                new SendHttpRequestTask().execute(new String[]{Constants.SERVER_ADDRESS+Constants.DEVICE_CONTROL_PAGE, device_id, device_key, output_data});

            }
        };

        for (int i=0; i<27; i++){
            btnCmd[i].setOnClickListener(multiListener);
        }

        ImageView cancelButton = (ImageView) remoteDialog.findViewById(R.id.cancelButton);
        if (cancelButton == null)
            Log.d(TAG, "Cancel");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            //@SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancel.onClick()");

                remoteDialog.dismiss();
                finish();

            }
        });
        remoteDialog.show();
    }

    //remoteListDialog
    private void remote_list(){

        remoteListDialog = new Dialog(Details.this);
        remoteListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        remoteListDialog.setContentView(R.layout.dialog_remote_list);
        //lampDialog.setTitle(R.string.action_control);
        remoteListDialog.setCancelable(false);
        remoteListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        txtNodeID = (TextView) remoteListDialog.findViewById(R.id.txtNodeID);
        txtNodeName= (TextView) remoteListDialog.findViewById(R.id.txtNodeName);
        //txtNodeInput = (TextView) remoteDialog.findViewById(R.id.txtNodeInput);

        txtNodeID.setText(device_id);
        txtNodeName.setText(device_name);

        RemoteList = (RecyclerView) remoteListDialog.findViewById(R.id.idRemoteList);

        courseRemoteModelArrayList = new ArrayList<>();

        // here we have created new array list and added data to it.
        courseRemoteModelArrayList.add(new cardRemoteModel("Samsung TV", "CV7UJ57G", device_name,device_id,device_key));
        courseRemoteModelArrayList.add(new cardRemoteModel("Walton Remote", "XC5RT53", device_name,device_id,device_key));
        courseRemoteModelArrayList.add(new cardRemoteModel("Tata Sky", "null", device_name,device_id,device_key));
        courseRemoteModelArrayList.add(new cardRemoteModel("LG AC", "null", device_name,device_id,device_key));

        // we are initializing our adapter class and passing our arraylist to it.
        cardRemoteAdapter RemoteAdapter = new cardRemoteAdapter(Details.this, courseRemoteModelArrayList);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Details.this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        RemoteList.setLayoutManager(linearLayoutManager);
        RemoteList.setAdapter(RemoteAdapter);

        /*
        this.lstRemote.setClickable(true);
        this.lstRemote.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = lstRemote.getItemAtPosition(position);
                String listitem = (String) o;//As you are using Default String Adapter
                //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();

                if (Constants.D) Log.d(TAG, "Click on listbox item: " + position);
                // name[], sAddress[], sCommand[], sRepeat[];

                for (int i=0;i<27;i++){
                    sName[i]="";
                    sCommand[i]="0";
                    sRepeat[i]="0";
                }

                switch(position) {
                    case 0: //samsung
                        //write your code here
                        sType="samsung";
                        sAddress="0707";

                        sName[0]="pow";
                        sCommand[0]="E6";
                        sRepeat[0]="2";

                        sName[1]="home";
                        sCommand[1]="79";
                        sRepeat[1]="1";

                        sName[2]="mute";
                        sCommand[2]="0F";
                        sRepeat[2]="1";

                        sName[3]="1";
                        sCommand[3]="04";
                        sRepeat[3]="1";

                        sName[4]="2";
                        sCommand[4]="05";
                        sRepeat[4]="1";

                        sName[5]="3";
                        sCommand[5]="06";
                        sRepeat[5]="1";

                        sName[6]="4";
                        sCommand[6]="08";
                        sRepeat[6]="1";

                        sName[7]="5";
                        sCommand[7]="09";
                        sRepeat[7]="1";

                        sName[8]="6";
                        sCommand[8]="0A";
                        sRepeat[8]="1";

                        sName[9]="7";
                        sCommand[9]="0C";
                        sRepeat[9]="1";

                        sName[10]="8";
                        sCommand[10]="0D";
                        sRepeat[10]="1";

                        sName[11]="9";
                        sCommand[11]="0E";
                        sRepeat[11]="1";

                        sName[12]="vol +";
                        sCommand[12]="07";
                        sRepeat[12]="1";

                        sName[13]="0";
                        sCommand[13]="11";
                        sRepeat[13]="1";

                        sName[14]="ch +";
                        sCommand[14]="12";
                        sRepeat[14]="1";

                        sName[15]="vol -";
                        sCommand[15]="0B";
                        sRepeat[15]="1";

                        sName[16]="UP";
                        sCommand[16]="60";
                        sRepeat[16]="1";

                        sName[17]="ch -";
                        sCommand[17]="10";
                        sRepeat[17]="1";

                        sName[18]="L";
                        sCommand[18]="65";
                        sRepeat[18]="1";

                        sName[19]="ok";
                        sCommand[19]="68";
                        sRepeat[19]="1";

                        sName[20]="R";
                        sCommand[20]="62";
                        sRepeat[20]="1";

                        sName[21]="back";
                        sCommand[21]="58";
                        sRepeat[21]="1";

                        sName[22]="DN";
                        sCommand[22]="61";
                        sRepeat[22]="1";

                        sName[23]="exit";
                        sCommand[23]="2D";
                        sRepeat[23]="1";

                        sName[24]="source";
                        sCommand[24]="01";
                        sRepeat[24]="1";

                        sName[25]="info";
                        sCommand[25]="1F";
                        sRepeat[25]="1";

                        sName[26]="set";
                        sCommand[26]="1A";
                        sRepeat[26]="1";

                        remote_control();

                        break;

                    case 1: //walton
                        //write your code here
                        sType="sony";
                        sAddress="1E";

                        sName[0]="pow";
                        sCommand[0]="01";
                        sRepeat[0]="1";

                        sName[1]="FAN";
                        sCommand[1]="02";
                        sRepeat[1]="1";

                        sName[3]="Fan +";
                        sCommand[3]="03";
                        sRepeat[3]="1";

                        sName[4]="Fan -";
                        sCommand[4]="04";
                        sRepeat[4]="1";

                        sName[6]="Light 1";
                        sCommand[6]="05";
                        sRepeat[6]="1";

                        sName[7]="Light 2";
                        sCommand[7]="06";
                        sRepeat[7]="1";

                        remote_control();
                        break;
                        
                    case 2:
                        //write your code here
                        //cmd = "02";
                        break;
                        
                    case 4:
                        //write your code here
                        //cmd = "02";
                        break;
                        
                    default:
                       ;
                }


            }
        });
        */
        ImageView cancelButton = (ImageView) remoteListDialog.findViewById(R.id.cancelButton);
        if (cancelButton == null)
            Log.d(TAG, "Cancel");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            //@SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cancel.onClick()");

                remoteListDialog.dismiss();
                finish();

            }
        });
        remoteListDialog.show();
    }

    public static String toString(boolean b) {
        return b ? "on" : "off";
    }

    boolean getValue(String value) {
        return ("Y".equals(value.toUpperCase())
                || "1".equals(value.toUpperCase())
                || "TRUE".equals(value.toUpperCase())
                || "OPEN".equals(value.toUpperCase())
                || "ON".equals(value.toUpperCase())
        );
    }

    public String firstTwo(String str) {

        if(str.length()<2){
            return str;
        }
        else{
            return str.substring(0,2);
        }
    }

    private void remote_data(String position){

        for (int i=0;i<27;i++){
            sName[i]="";
            sCommand[i]="0";
            sRepeat[i]="0";
        }

        switch(position) {
            case "CV7UJ57G": //samsung
                //write your code here
                sType="samsung";
                sAddress="0707";

                sName[0]="pow";
                sCommand[0]="E6";
                sRepeat[0]="2";

                sName[1]="home";
                sCommand[1]="79";
                sRepeat[1]="1";

                sName[2]="mute";
                sCommand[2]="0F";
                sRepeat[2]="1";

                sName[3]="1";
                sCommand[3]="04";
                sRepeat[3]="1";

                sName[4]="2";
                sCommand[4]="05";
                sRepeat[4]="1";

                sName[5]="3";
                sCommand[5]="06";
                sRepeat[5]="1";

                sName[6]="4";
                sCommand[6]="08";
                sRepeat[6]="1";

                sName[7]="5";
                sCommand[7]="09";
                sRepeat[7]="1";

                sName[8]="6";
                sCommand[8]="0A";
                sRepeat[8]="1";

                sName[9]="7";
                sCommand[9]="0C";
                sRepeat[9]="1";

                sName[10]="8";
                sCommand[10]="0D";
                sRepeat[10]="1";

                sName[11]="9";
                sCommand[11]="0E";
                sRepeat[11]="1";

                sName[12]="vol +";
                sCommand[12]="07";
                sRepeat[12]="1";

                sName[13]="0";
                sCommand[13]="11";
                sRepeat[13]="1";

                sName[14]="ch +";
                sCommand[14]="12";
                sRepeat[14]="1";

                sName[15]="vol -";
                sCommand[15]="0B";
                sRepeat[15]="1";

                sName[16]="UP";
                sCommand[16]="60";
                sRepeat[16]="1";

                sName[17]="ch -";
                sCommand[17]="10";
                sRepeat[17]="1";

                sName[18]="L";
                sCommand[18]="65";
                sRepeat[18]="1";

                sName[19]="ok";
                sCommand[19]="68";
                sRepeat[19]="1";

                sName[20]="R";
                sCommand[20]="62";
                sRepeat[20]="1";

                sName[21]="back";
                sCommand[21]="58";
                sRepeat[21]="1";

                sName[22]="DN";
                sCommand[22]="61";
                sRepeat[22]="1";

                sName[23]="exit";
                sCommand[23]="2D";
                sRepeat[23]="1";

                sName[24]="source";
                sCommand[24]="01";
                sRepeat[24]="1";

                sName[25]="info";
                sCommand[25]="1F";
                sRepeat[25]="1";

                sName[26]="set";
                sCommand[26]="1A";
                sRepeat[26]="1";

                //remote_control();

                break;

            case "XC5RT53": //walton
                //write your code here
                sType="sony";
                sAddress="1E";

                sName[0]="pow";
                sCommand[0]="01";
                sRepeat[0]="1";

                sName[1]="FAN";
                sCommand[1]="02";
                sRepeat[1]="1";

                sName[3]="Fan +";
                sCommand[3]="03";
                sRepeat[3]="1";

                sName[4]="Fan -";
                sCommand[4]="04";
                sRepeat[4]="1";

                sName[6]="Light 1";
                sCommand[6]="05";
                sRepeat[6]="1";

                sName[7]="Light 2";
                sCommand[7]="06";
                sRepeat[7]="1";

                //remote_control();
                break;

            case "null":
                //write your code here
                //cmd = "02";
                break;

            default:
                ;
        }
    }

}
