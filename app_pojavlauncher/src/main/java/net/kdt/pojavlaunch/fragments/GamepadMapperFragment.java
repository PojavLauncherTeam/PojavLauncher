package net.kdt.pojavlaunch.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.customcontrols.EditorExitable;
import net.kdt.pojavlaunch.customcontrols.gamepad.Gamepad;
import net.kdt.pojavlaunch.customcontrols.gamepad.GamepadMapperAdapter;

import fr.spse.gamepad_remapper.GamepadHandler;
import fr.spse.gamepad_remapper.Remapper;
import fr.spse.gamepad_remapper.RemapperManager;
import fr.spse.gamepad_remapper.RemapperView;

public class GamepadMapperFragment extends Fragment implements View.OnKeyListener, View.OnGenericMotionListener, EditorExitable, AdapterView

        .OnItemSelectedListener {
    public static final String TAG = "GamepadMapperFragment";
    private final RemapperView.Builder mRemapperViewBuilder = new RemapperView.Builder(null)
            .remapA(true)
            .remapB(true)
            .remapX(true)
            .remapY(true)
            .remapLeftJoystick(true)
            .remapRightJoystick(true)
            .remapStart(true)
            .remapSelect(true)
            .remapLeftShoulder(true)
            .remapRightShoulder(true)
            .remapLeftTrigger(true)
            .remapRightTrigger(true);
    private RemapperManager mInputManager;
    private RecyclerView mButtonRecyclerView;
    private Spinner mGrabStateSpinner;
    private GamepadMapperAdapter mMapperAdapter;
    private Gamepad mGamepad;
    public GamepadMapperFragment() {
        super(R.layout.fragment_controller_remapper);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButtonRecyclerView = view.findViewById(R.id.gamepad_remapper_recycler);
        mMapperAdapter = new GamepadMapperAdapter(view.getContext(), this);
        mButtonRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mButtonRecyclerView.setAdapter(mMapperAdapter);
        mButtonRecyclerView.setOnKeyListener(this);
        mButtonRecyclerView.setOnGenericMotionListener(this);
        mButtonRecyclerView.requestFocus();
        mInputManager = new RemapperManager(view.getContext(), mRemapperViewBuilder);
        mGrabStateSpinner = view.findViewById(R.id.gamepad_remapper_mode_spinner);
        ArrayAdapter<String> mGrabStateAdapter = new ArrayAdapter<>(view.getContext(), R.layout.item_centered_textview);
        mGrabStateAdapter.addAll(getString(R.string.customctrl_visibility_in_menus), getString(R.string.customctrl_visibility_ingame));
        mGrabStateSpinner.setAdapter(mGrabStateAdapter);
        mGrabStateSpinner.setSelection(0);
        mGrabStateSpinner.setOnItemSelectedListener(this);
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        Log.i("onKey", keyEvent.toString());
        View mainView = getView();
        if(!Gamepad.isGamepadEvent(keyEvent) || mainView == null) return false;
        if(mGamepad == null) mGamepad = new Gamepad(mainView, keyEvent.getDevice(), mMapperAdapter);
        mInputManager.handleKeyEventInput(mainView.getContext(), keyEvent, mGamepad);
        return true;
    }

    @Override
    public boolean onGenericMotion(View view, MotionEvent motionEvent) {
        Log.i("onGenericMotion", motionEvent.toString());
        View mainView = getView();
        if(!Gamepad.isGamepadEvent(motionEvent) || mainView == null) return false;
        if(mGamepad == null) mGamepad = new Gamepad(mainView, motionEvent.getDevice(), mMapperAdapter);
        mInputManager.handleMotionEventInput(mainView.getContext(), motionEvent, mGamepad);
        return true;
    }

    @Override
    public void exitEditor() {
        Activity activity = getActivity();
        if(activity == null) return;
        activity.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        boolean grab = i == 1;
        mMapperAdapter.setGrabState(grab);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
