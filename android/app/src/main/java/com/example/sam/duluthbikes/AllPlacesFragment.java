package com.example.sam.duluthbikes;

/**
 * Created by 77026 on 2018/4/20.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class AllPlacesFragment extends Fragment implements View.OnClickListener{

    View myView;
    private Button prePage, nexPage,likeButton;
    private ImageView myImage;
    int index=1;
    private TextView pageNumber;
    Presenter mPresenter;
    final String index1 = "bridge";
    final String index2 = "tower";
    final String index3 = "park";






    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_all_places, container, false);
        mPresenter = new Presenter();
        prePage=(Button)myView.findViewById(R.id.previous_page_button);
        nexPage=(Button)myView.findViewById(R.id.next_page_button);
        likeButton=(Button)myView.findViewById(R.id.like_button);
        myImage=(ImageView)myView.findViewById(R.id.image_view);
        pageNumber=(TextView) myView.findViewById(R.id.page_number_text);
        prePage.setOnClickListener(this);
        nexPage.setOnClickListener(this);
        likeButton.setOnClickListener(this);
        myImage.setImageResource(R.drawable.image001);





        return myView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.previous_page_button:
                goPrePage();
                break;
            case R.id.next_page_button:
                goNexPage();
                break;
            case R.id.like_button:
                clickLike();
                break;
        }
    }
    private void goNexPage() {
        if(index<3) {
            index++;
            setItems();
            checkButton();
        }
    }

    private void clickLike() {
        JSONArray jsonArray=null;

        ArrayList<Integer> numberArray = new ArrayList<>();
        ArrayList<String> placeArray = new ArrayList<>();


        try {
            jsonArray=mPresenter.getClicksToServer();
            String name;
            int number=0;
            if(jsonArray!=null){
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    name =jsonArray.getJSONObject(i).getJSONObject("clicks").getString("placeName");
                    String times =jsonArray.getJSONObject(i).getJSONObject("clicks").getString("clickTimes");
                    number= Integer.parseInt(times);
                   // System.out.print(name+number);
                    Log.d(name,times);
                    numberArray.add(number);
                    placeArray.add(name);
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }


        likeButton.setEnabled(false);
        int ourindex = -1;
        int number;
        String ourPlace="";
        if (index==1){
            ourPlace=index1;
        }else if (index==2){
            ourPlace=index2;
        }else{
            ourPlace=index3;
        }
        ourindex = placeArray.indexOf(ourPlace);
        number=numberArray.get(ourindex)+1;
        likeButton.setText(""+number);
        mPresenter.deleteOneClickToServer(ourPlace);
        mPresenter.sendOneClickToServer(ourPlace,""+number);

    }



    private void checkButton() {
        likeButton.setEnabled(true);
        likeButton.setText("LIKE(?)");
        if (index<=1){
            prePage.setEnabled(false);
            nexPage.setEnabled(true);
        }else if (index>=3){
            nexPage.setEnabled(false);
            prePage.setEnabled(true);
        }else{
            prePage.setEnabled(true);
            nexPage.setEnabled(true);
        }
    }

    private void goPrePage() {
        if(index>1) {
            index--;
            setItems();
            checkButton();
        }
    }

    private void setItems(){
        pageNumber.setText("--"+index+"--");
        if(index==1){
            myImage.setImageResource(R.drawable.image001);
        }else if (index==2){
            myImage.setImageResource(R.drawable.image002);
        }else if (index==3){
            myImage.setImageResource(R.drawable.image003);
        }
    }
}
