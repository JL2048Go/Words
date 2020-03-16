package com.jialin.words;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {
    private EditText editTextEnglish;
    private EditText editTextChinese;
    private Button buttonSubmit;
    private  WordViewModel wordViewModel;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FragmentActivity activity = requireActivity();
        editTextEnglish = activity.findViewById(R.id.editTextEnglish);
        editTextChinese = activity.findViewById(R.id.editTextChinese);
        buttonSubmit = activity.findViewById(R.id.buttonSubmit);
        wordViewModel = new ViewModelProvider(requireActivity()).get(WordViewModel.class);
        buttonSubmit.setEnabled(false);
        //自动弹起软键盘
        editTextEnglish.requestFocus();
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextEnglish, 0);
        //监听输入，输入完之后添加按钮可以点击
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String english = editTextEnglish.getText().toString().trim();
                String chinese = editTextChinese.getText().toString().trim();
                buttonSubmit.setEnabled(!english.isEmpty() && !chinese.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editTextChinese.addTextChangedListener(textWatcher);
        editTextEnglish.addTextChangedListener(textWatcher);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english = editTextEnglish.getText().toString().trim();
                String chinese = editTextChinese.getText().toString().trim();
                Word word = new Word(english, chinese);
                wordViewModel.insertWords(word);
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

}
