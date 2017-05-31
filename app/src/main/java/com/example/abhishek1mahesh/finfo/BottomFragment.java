package com.example.abhishek1mahesh.finfo;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**

 */
public class BottomFragment extends Fragment {

    TextView symbolTextView, openTextView, highTextView,lowTextView,peTextView,volTextView, avgTextView;
    int listViewPositionFromFragment;
    ArrayList<Security> securitiesArrayList = new ArrayList<Security>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.layout_fragment_bottom, container, false);




        //Log.d("FRAG",securitiesArrayList.size()+"");





        // ArrayList<Security> fragmentSecuritiesList = getArguments().getParcelableArrayList("securitiesList");

        symbolTextView = (TextView)view.findViewById(R.id.textViewName_id);
        openTextView = (TextView)view.findViewById(R.id.openTextView_id);
        highTextView = (TextView)view.findViewById(R.id.highTextView_id);
        lowTextView = (TextView)view.findViewById(R.id.lowTextView_id);
        peTextView = (TextView)view.findViewById(R.id.peRatioTextView_id);
        volTextView = (TextView)view.findViewById(R.id.volumeTextView_id);
        avgTextView = (TextView)view.findViewById(R.id.avgVolumeTextView_id);







            return view;

    }

    public void setFragmentTextViews(){
        securitiesArrayList = ((MainActivity)getActivity()).returnSecurities();
        listViewPositionFromFragment = ((MainActivity)getActivity()).returnListViewPosition();
        symbolTextView.setText(securitiesArrayList.get(listViewPositionFromFragment).getSymbol());
        openTextView.setText("Open "+securitiesArrayList.get(listViewPositionFromFragment).getOpenPrice());
        highTextView.setText("High "+securitiesArrayList.get(listViewPositionFromFragment).getHigh());
        lowTextView.setText("Low "+securitiesArrayList.get(listViewPositionFromFragment).getLow());
        peTextView.setText("P/E "+securitiesArrayList.get(listViewPositionFromFragment).getPeRatio());
        volTextView.setText("Vol "+securitiesArrayList.get(listViewPositionFromFragment).getVolume());
        avgTextView.setText("Avg Vol "+securitiesArrayList.get(listViewPositionFromFragment).getAvgVolume());

    }

}

