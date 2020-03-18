package com.jialin.words;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
    private LiveData<List<Word>> filteredWords;
    private static final String VIEW_TYPE_SHP= "view_type_shp";
    private static final String IS_USING_CARD  = "is_using_card";
    private List<Word> allWords;
    private boolean undoAction;
    private DividerItemDecoration dividerItemDecoration;

    public WordsFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false);
    }
    //显示标题栏的搜索按钮
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu,menu);
        //控制搜索区域大小
        SearchView searchView = (SearchView)menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(750);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String patten = newText.trim();
                //每次搜索前移除观察observer
                filteredWords.removeObservers(getViewLifecycleOwner());
                //创建观察
                filteredWords = wordViewModel.findWordsWithPatten(patten);
                filteredWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int tmp = myAdapter1.getItemCount();
//                        myAdapter1.setAllWords(words);
//                        myAdapter2.setAllWords(words);
                        allWords = words;
                        if (tmp != words.size()) {
//                            myAdapter1.notifyDataSetChanged();
//                            myAdapter2.notifyDataSetChanged();
                            myAdapter1.submitList(words);
                            myAdapter2.submitList(words);
                        }
                    }
                });
                return true;
            }
        });
    }
    //直接返回的时候，收起键盘
    @Override
    public void onResume() {
        InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.clearData:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wordViewModel.deleteAllWords();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.switchViewType:
                SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
                boolean viewType = shp.getBoolean(IS_USING_CARD, false);
                SharedPreferences.Editor editor = shp.edit();
                if (viewType){
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setAdapter(myAdapter1);
                    editor.putBoolean(IS_USING_CARD, false);
                } else {
                    recyclerView.removeItemDecoration(dividerItemDecoration);
                    recyclerView.setAdapter(myAdapter2);
                    editor.putBoolean(IS_USING_CARD, true);
                }
                editor.apply();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //recyclerView显示数据
        wordViewModel = new ViewModelProvider(requireActivity()).get(WordViewModel.class);
        recyclerView = requireActivity().findViewById(R.id.recycleView);
        addNewWord = requireActivity().findViewById(R.id.addNewWord);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        dividerItemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        myAdapter1 = new MyAdapter(false, wordViewModel);
        myAdapter2 = new MyAdapter(true, wordViewModel);
        recyclerView.setItemAnimator(new DefaultItemAnimator(){
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null){
                    int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = firstPosition; i <= lastPosition; i++){
                        MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if(holder != null) {
                            holder.textViewNumber.setText(String.valueOf(i + 1));
                        }
                    }
                }
            }
        });
        //读取视图的设置
        SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
        boolean viewType = shp.getBoolean(IS_USING_CARD, false);
        if (viewType){
            recyclerView.setAdapter(myAdapter2);
        } else {
            recyclerView.setAdapter(myAdapter1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
        filteredWords = wordViewModel.getAllWordsLive();
        filteredWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int tmp = myAdapter1.getItemCount();
//                myAdapter1.setAllWords(words);
//                myAdapter2.setAllWords(words);
                allWords = words;
                if (tmp != words.size()){
//                    myAdapter1.notifyDataSetChanged();
//                    myAdapter2.notifyDataSetChanged();
                    //删除的话，就不向下滚动
                    if (tmp < words.size() && !undoAction){
                        recyclerView.smoothScrollBy(0, -200);
                    }
                    undoAction = false;
                    myAdapter1.submitList(words);
                    myAdapter2.submitList(words);
                }
            }
        });
        //元素滑动删除
        //第一个参数是上下拖动(UP DOWN)，第二个参数是左右滑动
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                Word wordFrom = allWords.get(viewHolder.getAdapterPosition());
//                Word wordTo = allWords.get(target.getAdapterPosition());
//                int tmp = wordFrom.getId();
//                wordFrom.setId(wordTo.getId());
//                wordTo.setId(tmp);
//                wordViewModel.updateWord(wordFrom, wordTo);
//                myAdapter1.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//                myAdapter2.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Word wordToDelete = allWords.get(viewHolder.getAdapterPosition());
                wordViewModel.deleteWords(wordToDelete);
                Snackbar.make(requireActivity().findViewById(R.id.wordFragmentView), "删除了一个单词", Snackbar.LENGTH_SHORT)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undoAction = true;
                                wordViewModel.insertWords(wordToDelete);
                            }
                        }).show();
            }
            //滑动的时候添加背景颜色和图标
            Drawable icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_delete_forever_black_24dp);
            Drawable background = new ColorDrawable(Color.GRAY);

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight())/2;
                int iconLeft, iconRight, iconTop, iconBottom;
                int backLeft, backRight, backTop, backBottom;
                backTop = itemView.getTop();
                backBottom = itemView.getBottom();
                iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight())/2;
                iconBottom = iconTop + icon.getIntrinsicHeight();
                if (dX > 0){
                    backLeft = itemView.getLeft();
                    backRight = (int) (itemView.getLeft() + dX);
                    background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = iconLeft + icon.getIntrinsicHeight();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else if (dX < 0){
                    backRight = itemView.getRight();
                    backLeft = (int) (itemView.getRight() + dX);
                    background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconRight = itemView.getRight() - iconMargin;
                    iconLeft = iconRight - icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else {
                    background.setBounds(0,0,0,0);
                    icon.setBounds(0,0,0,0);
                }
                background.draw(c);
                icon.draw(c);
            }
        }).attachToRecyclerView(recyclerView);
        //添加按钮跳转
        addNewWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_wordsFragment_to_addFragment);
            }
        });

    }
}
