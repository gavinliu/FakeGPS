package com.github.fakegps.pokemon.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.fakegps.pokemon.event.BroadcastEvent;
import com.github.fakegps.pokemon.model.LocationPoint;
import com.github.fakegps.pokemon.util.DbUtils;
import com.github.fakegps.pokemon.FakeGpsApp;
import com.github.fakegps.pokemon.util.FakeGpsUtils;
import com.github.fakegps.pokemon.joystick.JoyStickManager;
import com.github.fakegps.pokemon.R;
import com.github.fakegps.pokemon.model.LocationMark;

import java.util.ArrayList;

public class FlyToActivity extends AppCompatActivity implements View.OnClickListener {
    private final double LAT_DEFAULT = 23.151637;
    private final double LON_DEFAULT = 113.344721;

    public static final int DELETE_ID = 1001;

    private EditText mLocationLatEditText;
    private EditText mLocationLonEditText;

    private RadioGroup mMoveStepGroup;

    private ListView mListView;
    private MarkAdapter mAdapter;

    private Button mBtnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fly);

        mLocationLatEditText = (EditText) findViewById(R.id.inputLat);
        mLocationLonEditText = (EditText) findViewById(R.id.inputLon);

        LocationPoint currentLocPoint = JoyStickManager.get().getCurrentLocPoint();
        if (currentLocPoint == null) {
            String lastLocPoint = DbUtils.getLastLocPoint(this);
            if (!TextUtils.isEmpty(lastLocPoint)) {
                currentLocPoint = FakeGpsUtils.getLocPointFromString(lastLocPoint);
            } else {
                currentLocPoint = new LocationPoint(LAT_DEFAULT, LON_DEFAULT);
            }
        }

        mLocationLatEditText.setText(String.valueOf(currentLocPoint.getLatitude()));
        mLocationLonEditText.setText(String.valueOf(currentLocPoint.getLongitude()));

        mLocationLatEditText.setSelection(mLocationLatEditText.getText().length());
        mLocationLonEditText.setSelection(mLocationLonEditText.getText().length());

        mMoveStepGroup = (RadioGroup) findViewById(R.id.inputStep);
        mMoveStepGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                double step = JoyStickManager.STEP_WALK;

                switch (checkedId) {
                    case R.id.inputStepBike:
                        step = JoyStickManager.STEP_BIKE;
                        break;

                    case R.id.inputStepCar:
                        step = JoyStickManager.STEP_CAR;
                }

                JoyStickManager.get().setMoveStep(step);
            }
        });

        double step = JoyStickManager.get().getMoveStep();
        if (step == JoyStickManager.STEP_CAR) mMoveStepGroup.check(R.id.inputStepCar);
        else if (step == JoyStickManager.STEP_WALK) mMoveStepGroup.check(R.id.inputStepWalk);
        else if (step == JoyStickManager.STEP_BIKE) mMoveStepGroup.check(R.id.inputStepBike);

        mListView = (ListView) findViewById(R.id.list_bookmark);

        mBtnStart = (Button) findViewById(R.id.btn_fly);
        mBtnStart.setOnClickListener(this);
        updateBtn();

        initListView();

        registerBroadcastReceiver();
    }

    @Override
    public void onClick(View view) {
        LocationPoint point = FakeGpsUtils.getLocPointFromInput(mLocationLatEditText, mLocationLonEditText);

        switch (view.getId()) {

            case R.id.btn_fly:
                if (JoyStickManager.get().isStarted()) {
                    if (JoyStickManager.get().isFlyMode()) {
                        JoyStickManager.get().stopFlyMode();
                    } else {
                        if (point != null) {
                            JoyStickManager.get().flyToLocation(point);
                        } else {
                            Toast.makeText(this, "Input is not valid!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                updateBtn();
                break;
        }
    }

    private void updateBtn() {
        if (JoyStickManager.get().isFlyMode()) {
            mBtnStart.setText(R.string.btn_fly_stop);
        } else {
            mBtnStart.setText(R.string.btn_fly_start);
        }
    }

    private void initListView() {
        mAdapter = new MarkAdapter(this);
        ArrayList<LocationMark> allBookmark = DbUtils.getAllBookmark();
        mAdapter.setLocBookmarkList(allBookmark);
        mListView.setAdapter(mAdapter);

        View emptyView = findViewById(R.id.empty_view);
        mListView.setEmptyView(emptyView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationPoint locPoint = mAdapter.getItem(position).getLocPoint();

                mLocationLatEditText.setText(String.valueOf(locPoint.getLatitude()));
                mLocationLonEditText.setText(String.valueOf(locPoint.getLongitude()));
            }
        });

        registerForContextMenu(mListView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                delete(info.position);
                return true;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void delete(final int position) {
        if (position < 0) return;
        final LocationMark bookmark = mAdapter.getItem(position);
        new AlertDialog.Builder(this)
                .setTitle("Delete " + bookmark.toString())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DbUtils.deleteBookmark(bookmark);
                        ArrayList<LocationMark> allBookmark = DbUtils.getAllBookmark();
                        mAdapter.setLocBookmarkList(allBookmark);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(BroadcastEvent.BookMark.ACTION_BOOK_MARK_UPDATE);
        LocalBroadcastManager.getInstance(FakeGpsApp.get()).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void unregisterBroadcastReceiver() {
        LocalBroadcastManager.getInstance(FakeGpsApp.get()).unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastEvent.BookMark.ACTION_BOOK_MARK_UPDATE.equals(action)) {
                ArrayList<LocationMark> allBookmark = DbUtils.getAllBookmark();
                mAdapter.setLocBookmarkList(allBookmark);
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterBroadcastReceiver();
        super.onDestroy();
    }

    public static void startPage(Context context) {
        Intent intent = new Intent(context, FlyToActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

}
