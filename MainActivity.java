package com.rapiddesign.stopwatch2_1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.example.stopwatch2.R;
import android.view.View;
import android.content.Intent;
import android.widget.ImageButton;



public class MainActivity extends Activity {
    private static final String LOG_TAG = null;
    private Handler mHandler = new Handler();
    TextView sWValue;
    private ListView mListView1;
    Button btnStartStop, btnReset, sendMessage;
    protected long mStartTime = 0, mStopped = 0;
    public static String mTimeStamp1S = "void";
    int nStarts = 0;
    int nStops = 0;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
    ArrayAdapter<String> adapter;
    boolean nNoTimestamp = false;   // prevents things from being listed that should not be listed


    // this section initializes and sets up activity's layout,associate a ListView with an adapter to display a list of strings and update the text of a textview.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView1 = (ListView) findViewById(R.id.my_listview);
        adapter = new ArrayAdapter<String>(this, R.layout.my_listview_text, listItems);
        mListView1.setAdapter(adapter);
        sWValue = (TextView) findViewById(R.id.sWValueID);
        sWValue.setText("00:00:00:000");

        // Add this code to handle the "About" button click
        ImageButton aboutButton = findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

// next lines of code control the behavior of the stopwatch during different lifecycle events of the activity
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }
    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onRestart() {
        if (nStops >0){
            mHandler.removeCallbacks(mUpdateTimeTask);
        }
        else {
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, 100);
        }
        super.onRestart();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    //if (nStops == 0){
                    if (nStarts <= 0){
                        mStartTime = System.currentTimeMillis();
                        nStarts = nStarts + 1;
                        nNoTimestamp = true;
                    } else
                    if ((nStarts >= 1)&&(nNoTimestamp == false)){
                        adapter.insert(nStarts + ":  " + mTimeStamp1S, 0);
                        adapter.notifyDataSetChanged();

                    }
                    if (nStops != 0){
                        mStartTime = System.currentTimeMillis()-mStopped;
                    }
                    if (nNoTimestamp == false){
                        nStarts = nStarts + 1;
                    }
                    nNoTimestamp = false;
                    nStops = 0;
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    mHandler.postDelayed(mUpdateTimeTask, 100);
                }

                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (nStops <= 0){
                        mStopped = System.currentTimeMillis() - mStartTime;
                        nNoTimestamp = true;
                        nStops = nStops + 1;
                        mHandler.removeCallbacks(mUpdateTimeTask);
                    } else
                    if (nStops >= 2){
                        nStarts = 0;
                        sWValue.setText("00:00:00:000");
                        adapter.clear();
                        mStopped = 0 ;
                        nNoTimestamp = true;
                        nStops = 2;
                    } else
                    if (nStops == 1){
                        nNoTimestamp = true;
                        nStops = nStops + 1;
                        mHandler.removeCallbacks(mUpdateTimeTask);
                    }
                    else {
                        nNoTimestamp = true;
                    }

                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            long elapsedTime = System.currentTimeMillis() - mStartTime ;
            String millisS = Long.toString(elapsedTime);
            String millstonesS = "000";
            String secondsS = Integer.toString((int) ((elapsedTime/1000) % 60));
            String minutesS = Integer.toString((int) (((elapsedTime/1000) % 3600) / 60));
            String hoursS = Integer.toString((int) ((elapsedTime/1000) / 3600));

            if (secondsS.length() < 2)
                secondsS = "0" + secondsS;

            if (minutesS.length() < 2)
                minutesS = "0" + minutesS;

            if (hoursS.length() < 2)
                hoursS = "0" + hoursS;


            millstonesS = millisS.substring(millisS.length() -3, (millisS.length()));
            millisS = millisS.substring(millisS.length() -3, (millisS.length()-2));
            sWValue.setText(hoursS +  ":" + minutesS +  ":" + secondsS + ":" + millstonesS);
            mTimeStamp1S = (hoursS +  ":" + minutesS +  ":" + secondsS + ":" + millstonesS  );
            mHandler.postAtTime(this,
                    mStartTime + ((((elapsedTime/1000) % 3600) / 60) + ((elapsedTime/1000) % 60) + 1) * 1000);
            mHandler.postDelayed(mUpdateTimeTask, 10);

        }
    };



    public File getStorageDir(Context context, String recordName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), recordName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    void createExternalStoragePrivateFile() {
        // Create a path where we will place our private file on external
        // storage.
        File file = new File(getExternalFilesDir(null), "DemoFile.txt");

        try {
            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            Object[] objs = new Object[mListView1.getCount()];

            for (int i = 0 ; i < mListView1.getCount();i++) {
                Object obj = (Object)mListView1.getItemAtPosition(i);
                objs[i] = obj;
            }
            Serializable is = objs;
            InputStream is1 = (InputStream) is;
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is1.available()];
            is1.read(data);
            os.write(data);
            is1.close();
            os.close();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }

// about activity


}

