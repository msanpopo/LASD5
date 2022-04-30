package com.example.hide.lasd5;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WordListFragment extends Fragment implements MainActivity.WordChangedListener{
    private static final String TAG = "WordListFragment";
    private static final String ARG_POSITION = "position";
    private int position;

    private RecyclerView mRecyclerView;
    private WordListAdapter adapter;

    public static WordListFragment newInstance(int position) {
        WordListFragment fragment = new WordListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WordListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }

        Log.d(TAG, "onCreate:" + position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView:" + position);

        MainActivity activity = (MainActivity)getActivity();
        activity.addWordChangeListener(this);

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        adapter = new WordListAdapter();
        adapter.setDic(activity.getDictionary());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart:" + position);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume:" + position);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause:" + position);
    }


    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop:" + position);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

        MainActivity activity = (MainActivity)getActivity();
        activity.removeWordChangeListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy:" + position);
    }

    @Override
    public void wordChanged() {
        Log.d(TAG, "wordChanged: pos:" + position);

        MainActivity a = (MainActivity)getActivity();
        int pos = a.getListPosition();

        Log.d(TAG, "wordChanged: list_pos:" + pos);

        mRecyclerView.scrollToPosition(0);
        mRecyclerView.scrollToPosition(pos);
    }

    @Override
    public int getPosition(){
        return position;
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnItemClickedListener {
        public void onItemClicked(String id);
    }
}