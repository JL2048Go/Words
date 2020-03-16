package com.jialin.words;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordsFragment extends Fragment {
    private WordViewModel wordViewModel;
    private MyAdapter myAdapter1;
    private MyAdapter myAdapter2;
    private RecyclerView recyclerView;
    private FloatingActionButton addNewWord;
    private NavController navController;

    public WordsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false);
    }

    @Override
    public void onResume() {
        InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wordViewModel = new ViewModelProvider(requireActivity()).get(WordViewModel.class);
        recyclerView = requireActivity().findViewById(R.id.recycleView);
        addNewWord = requireActivity().findViewById(R.id.addNewWord);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        myAdapter1 = new MyAdapter(false, wordViewModel);
        myAdapter2 = new MyAdapter(true, wordViewModel);
        recyclerView.setAdapter(myAdapter1);
        wordViewModel.getAllWordsLive().observe(requireActivity(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int tmp = myAdapter1.getItemCount();
                myAdapter1.setAllWords(words);
                myAdapter2.setAllWords(words);
                if (tmp != words.size()){
                    myAdapter1.notifyDataSetChanged();
                    myAdapter2.notifyDataSetChanged();
                }
            }
        });
        addNewWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_wordsFragment_to_addFragment);
            }
        });

    }
}
