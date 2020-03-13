package com.jialin.wordtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    WordViewModel wordViewModel;
    RecyclerView recyclerView;
    MyAdapter myAdapter, myAdapter1;
    Button insert, clear;
    Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        insert = findViewById(R.id.button);
        clear = findViewById(R.id.button3);
        recyclerView = findViewById(R.id.recyclerView);
        aSwitch = findViewById(R.id.switch1);
        myAdapter = new MyAdapter(false);
        myAdapter1 = new MyAdapter(true);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    recyclerView.setAdapter(myAdapter1);
                } else {
                    recyclerView.setAdapter(myAdapter);
                }
            }
        });
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        wordViewModel.getAllWordsLive().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                myAdapter.setAllWords(words);
                myAdapter1.setAllWords(words);
                myAdapter.notifyDataSetChanged();
                myAdapter1.notifyDataSetChanged();
            }
        });

        insert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String[] chinese = {
                        "你好",
                        "世界",
                        "今天",
                        "天气",
                        "很好",
                        "太阳",
                        "很大",
                        "很圆"
                };
                String[] english = {
                        "hello",
                        "world",
                        "today",
                        "the weather",
                        "is good",
                        "the sun",
                        "is big",
                        "is round"
                };
                for (int i = 0; i <chinese.length; i++){
                    wordViewModel.insertWords(new Word(english[i], chinese[i]));
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                wordViewModel.deleteAllWords();
            }
        });
    }


}
