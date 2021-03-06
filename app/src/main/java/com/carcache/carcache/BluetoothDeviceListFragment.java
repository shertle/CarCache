package com.carcache.carcache;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.carcache.carcache.services.BluetoothListenerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class BluetoothDeviceListFragment extends Fragment implements AbsListView.OnItemClickListener {

    public static final String BLUETOOTH_LOGGER = "BLUETOOTH_LOGGER";
    public static final String PREFS_KEY_SAVEDDEVICE = "CarCache Saved Device";

    private Button mRefreshButton;

    private List<DeviceListItem> mDeviceList;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static BluetoothDeviceListFragment newInstance(String param1, String param2) {
        BluetoothDeviceListFragment fragment = new BluetoothDeviceListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BluetoothDeviceListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        mRefreshButton = (Button) this.getActivity().findViewById(R.id.refreshButton);
        mRefreshButton.setVisibility(View.INVISIBLE);

        /*
        List<String> s = new ArrayList<String>();
        for (BluetoothDevice bt : pairedDevices) {
            s.add(bt.getName());
        }

        for (String st : s) {
            Log.v(BLUETOOTH_LOGGER, st);
        }
        */

        mDeviceList = new ArrayList<DeviceListItem>();
        for (BluetoothDevice bt : pairedDevices) {
            mDeviceList.add(new DeviceListItem(bt));
        }



        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<DeviceListItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, mDeviceList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetoothdevicelist, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice bd = mDeviceList.get(position).getDevice();
        Log.v(BLUETOOTH_LOGGER, "The device is: " + bd.getName());
        Log.v(BLUETOOTH_LOGGER, "The Mac address is: " + bd.getAddress());

        saveDevice(bd);
        mRefreshButton.setVisibility(View.VISIBLE);

        getActivity().startService(new Intent(getActivity(), BluetoothListenerService.class));


        Fragment toRemove = this;
        this.getActivity().getFragmentManager().beginTransaction().remove(toRemove).commit();

        /*
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(s.get(position).id);
        }
        */
    }

    /**
     * Save the hashcode of the device for later
     * @param bd the BluetoothDevice to save
     */
    public void saveDevice(BluetoothDevice bd) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        Log.e("ASDF",bd.getAddress());

        editor.putString(PREFS_KEY_SAVEDDEVICE, bd.getAddress());
        editor.putBoolean(MapsActivity.PREFS_KEY_FIRSTLAUNCH, false);
        editor.apply();

    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
