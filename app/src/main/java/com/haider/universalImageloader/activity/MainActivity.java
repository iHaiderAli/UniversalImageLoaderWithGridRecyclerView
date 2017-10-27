package com.haider.universalImageloader.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.haider.universalImageloader.R;
import com.haider.universalImageloader.adapters.UserAdapter;
import com.haider.universalImageloader.Model.UserModel;
import com.haider.universalImageloader.utils.ClickListener;
import com.haider.universalImageloader.utils.Constants;
import com.haider.universalImageloader.utils.RecyclerTouchListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<UserModel> usersList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersList = Constants.getIMAGES();

        mAdapter=new UserAdapter(usersList);

        recyclerView = (RecyclerView) findViewById(R.id.recyle_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,3);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                UserModel user = usersList.get(position);
                Toast.makeText(getApplicationContext(), position + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), position + " is selected!", Toast.LENGTH_SHORT).show();
            }
        }));
    }

}
