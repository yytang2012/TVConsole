package edu.wing.yytang.tvconsole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import edu.wing.yytang.common.Constants;
import edu.wing.yytang.common.StateMachine;
import edu.wing.yytang.services.SessionService;

public class MainActivity extends AppCompatActivity implements Constants{
    private final static String TAG = MainActivity.class.getName();
    private MainActivity mActivity;
    private ImageButton connectButton;
    private ImageButton addFavoriteButton;
    private EditText hostEditText;
    private ListView hostListView;
    private SharedPreferences sharedPref;

    private String keyprefHost;
    private String keyprefHostList;
    private ArrayList<String> hostList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyprefHost = getString(R.string.pref_host_key);
        keyprefHostList = getString(R.string.pref_host_list_key);

        setContentView(R.layout.activity_main);
        mActivity = this;

        connectButton = (ImageButton) findViewById(R.id.connect_button);
        addFavoriteButton = (ImageButton) findViewById(R.id.add_favorite_button);
        hostEditText = (EditText) findViewById(R.id.host_edittext);

        hostEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    addFavoriteButton.performClick();
                    return true;
                }
                return false;
            }
        });
        hostEditText.requestFocus();

        hostListView = (ListView) findViewById(R.id.host_listview);
        hostListView.setEmptyView(findViewById(android.R.id.empty));
        hostListView.setOnItemClickListener(hostListClickListener);
        registerForContextMenu(hostListView);

        connectButton.setOnClickListener(mOnClickListener);
        addFavoriteButton.setOnClickListener(addFavoriteListener);
    }

    public View.OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startDaemonService();
            switch(v.getId()) {
                case R.id.connect_button:
                    startActivity(new Intent(MainActivity.this, TouchScreenActivity.class));
                    break;
//                case R.id.exit:
//                    stopConnection();
//                    break;
                default:
                    break;
            }
        }
    };

    private final AdapterView.OnItemClickListener hostListClickListener =
        new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String hostIP = ((TextView) view).getText().toString();
//                connectToRoom(roomId, false, false, false, 0);
                Log.i(TAG, "Connecting to: " + hostIP);
            }
      };

    public final OnClickListener addFavoriteListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String hostIPAddr = hostEditText.getText().toString();
            Log.d(TAG, "host ip is: " + hostIPAddr);
            if (hostIPAddr.length() > 0 && !hostList.contains(hostIPAddr)) {
                adapter.add(hostIPAddr);
                adapter.notifyDataSetChanged();
            }
        }
    };
//
//  private final OnClickListener addFavoriteListener1 = new OnClickListener() {
//    @Override
//    public void onClick(View view) {
//      String newRoom = roomEditText.getText().toString();
//      if (newRoom.length() > 0 && !roomList.contains(newRoom)) {
//        adapter.add(newRoom);
//        adapter.notifyDataSetChanged();
//      }
//    }
//  };

    public void startDaemonService() {
        if(SessionService.getState() == StateMachine.STATE.NEW) {
            Intent intent = new Intent(MainActivity.this, SessionService.class).putExtra("connectionID", 0);
            startService(intent);
        }
    }

    private void stopConnection() {// Broadcast the custom intent
        Intent intent = new Intent();
        intent.setAction(ACTION_STOP_SERVICE);
        sendBroadcast(intent);
        stopService(new Intent(MainActivity.this, SessionService.class));

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.host_listview) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                menu.setHeaderTitle(hostList.get(info.position));
                String[] menuItems = getResources().getStringArray(R.array.hostListContextMenu);
                for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        } else {
            super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        String host = hostEditText.getText().toString();
        String hostListJson = new JSONArray(hostList).toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(keyprefHost, host);
        editor.putString(keyprefHostList, hostListJson);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        String host = sharedPref.getString(keyprefHost, "");
        hostEditText.setText(host);
        hostList = new ArrayList<String>();
        String hostListJson = sharedPref.getString(keyprefHostList, null);
        if (hostListJson != null) {
            try {
                JSONArray jsonArray = new JSONArray(hostListJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    hostList.add(jsonArray.get(i).toString());
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to load host list: " + e.toString());
            }
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, hostList);
        hostListView.setAdapter(adapter);
        if(adapter.getCount() > 0) {
            hostListView.requestFocus();
            hostListView.setItemChecked(0, true);
        }
    }


}
