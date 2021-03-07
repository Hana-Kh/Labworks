package ua.kpi.comsys.io8225.labworks.ui.books_list;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.daimajia.swipe.SwipeLayout;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import ua.kpi.comsys.io8225.labworks.R;

public class BooksListFragment extends Fragment {

    LinearLayout mainLayout;
    View root;
    ArrayList<ConstraintLayout> arrConstraint;
    ArrayList<Book> arrBook;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_bookslist, container, false);

        mainLayout = root.findViewById(R.id.linear_main);

        arrConstraint = new ArrayList<>();
        arrBook = new ArrayList<>();

        try {
            for (Book book:
                    parseBooks(readTextFile(root.getContext(), R.raw.bookslist))) {
                addNewBook(book);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SearchView searchView = root.findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                int countResults = 0;
                for (ConstraintLayout book :
                        arrConstraint) {
                    if (query == null){
                        ((SwipeLayout) book.getParent()).setVisibility(View.VISIBLE);
                        countResults++;
                    }
                    else {
                        if (arrBook.get(arrConstraint.indexOf(book)).bookTitle.toLowerCase()
                                .contains(query.toLowerCase()) || query.length() == 0){
                            ((SwipeLayout) book.getParent()).setVisibility(View.VISIBLE);
                            countResults++;
                        }
                        else
                            ((SwipeLayout) book.getParent()).setVisibility(View.GONE);
                    }
                }

                if (countResults == 0){
                    root.findViewById(R.id.no_books_view).setVisibility(View.VISIBLE);
                }
                else {
                    root.findViewById(R.id.no_books_view).setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                int countResults = 0;
                for (ConstraintLayout book :
                        arrConstraint) {
                    if (query == null){
                        ((SwipeLayout) book.getParent()).setVisibility(View.VISIBLE);
                        countResults++;
                    }
                    else {
                        if (arrBook.get(arrConstraint.indexOf(book)).bookTitle.toLowerCase()
                                .contains(query.toLowerCase()) || query.length() == 0){
                            ((SwipeLayout) book.getParent()).setVisibility(View.VISIBLE);
                            countResults++;
                        }
                        else
                            ((SwipeLayout) book.getParent()).setVisibility(View.GONE);
                    }
                }

                if (countResults == 0){
                    root.findViewById(R.id.no_books_view).setVisibility(View.VISIBLE);
                }
                else {
                    root.findViewById(R.id.no_books_view).setVisibility(View.GONE);
                }
                return false;
            }
        });

        Button btnAddBook = root.findViewById(R.id.button_add_book);
        btnAddBook.setOnClickListener(v -> {
            NewBook popUpClass = new NewBook();
            Object[] popups = popUpClass.showPopupWindow(v);

            View popupView = (View) popups[0];
            PopupWindow popupWindow = (PopupWindow) popups[1];

            EditText inputTitle = popupView.findViewById(R.id.input_title);
            EditText inputSubtitle = popupView.findViewById(R.id.input_subtitle);
            EditText inputPrice = popupView.findViewById(R.id.input_price);

            Button buttonAdd = popupView.findViewById(R.id.add_book_button);
            buttonAdd.setOnClickListener(v1 -> {
                if (inputTitle.getText().toString().length() != 0 &&
                        inputSubtitle.getText().toString().length() != 0 &&
                        inputPrice.getText().toString().length() != 0) {

                    addNewBook(new Book(inputTitle.getText().toString(),
                                    inputSubtitle.getText().toString(),
                                    inputPrice.getText().toString(), "", ""));
                    orientChange();

                    popupWindow.dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "Incorrect data!",
                            Toast.LENGTH_LONG).show();
                }
            });
        });

        return root;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        orientChange();
    }

    private void orientChange(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) root.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        for (ConstraintLayout bookshelf :
                arrConstraint) {
            bookshelf.getChildAt(0).setLayoutParams(
                    new ConstraintLayout.LayoutParams(width/3, width/3));
        }
    }

    public static String readTextFile(Context context, @RawRes int id){
        InputStream inputStream = context.getResources().openRawResource(id);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int size;
        try {
            while ((size = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, size);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            System.err.println("FIle cannot be reading!");
            e.printStackTrace();
        }
        return outputStream.toString();
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private ArrayList<Book> parseBooks(String jsonText) throws ParseException {
        ArrayList<Book> result = new ArrayList<>();

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);

        JSONArray books = (JSONArray) jsonObject.get("books");
        for (Object book: books) {
            JSONObject tmp = (JSONObject) book;
            result.add(new Book(
                    (String) tmp.get("title"),
                    (String) tmp.get("subtitle"),
                    (String) tmp.get("isbn13"),
                    (String) tmp.get("price"),
                    (String) tmp.get("image")
            ));
        }

        return result;
    }

    private void binClicked(SwipeLayout swipeLayout, ConstraintLayout key){
        arrBook.remove(arrConstraint.indexOf(key));
        arrConstraint.remove(key);
        mainLayout.removeView(swipeLayout);
    }

    private void addNewBook(Book newBook){
        SwipeLayout swipeLay = new SwipeLayout(root.getContext());
        swipeLay.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLay.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT));
        mainLayout.addView(swipeLay);

        ImageButton deleteButton = new ImageButton(root.getContext());
        deleteButton.setImageResource(R.drawable.ic_delete_forever_white_48dp);
        deleteButton.setBackgroundColor(Color.RED);
        deleteButton.setPadding(50, 0, 50, 0);
        LinearLayout.LayoutParams btnBinParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        btnBinParams.gravity = Gravity.RIGHT;
        swipeLay.setShowMode(SwipeLayout.ShowMode.PullOut);
        //swipeLay.addDrag(SwipeLayout.DragEdge.Right, deleteButton);
        swipeLay.addView(deleteButton, 0, btnBinParams);

        ConstraintLayout bookShelf = new ConstraintLayout(root.getContext());
        bookShelf.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        swipeLay.addView(bookShelf, 1);

        deleteButton.setOnClickListener(v -> binClicked(swipeLay, bookShelf));
        bookShelf.setOnClickListener(v -> {
            if (newBook.bookIsbn13.length() != 0 && !newBook.bookIsbn13.equals("noid")) {
                BookFull popUpClass = new BookFull();
                popUpClass.showPopupWindow(v, newBook);
            }
        });

        ImageView bookPic = new ImageView(root.getContext());
        if (newBook.bookImagePath.length() != 0)
            bookPic.setImageResource(
                    getResId(newBook.bookImagePath.toLowerCase()
                            .split("\\.")[0], R.drawable.class));
        bookPic.setId(bookPic.hashCode());
        ConstraintLayout.LayoutParams imgParams = new ConstraintLayout.LayoutParams(300, 300);
        bookShelf.addView(bookPic, imgParams);

        TextView textTitle = new TextView(root.getContext());
        textTitle.setTextColor(Color.BLACK);
        textTitle.setText(newBook.bookTitle);
        textTitle.setEllipsize(TextUtils.TruncateAt.END);
        textTitle.setMaxLines(1);
        textTitle.setId(textTitle.hashCode());
        ConstraintLayout.LayoutParams textTitleParams =
                new ConstraintLayout.LayoutParams(ConstraintSet.MATCH_CONSTRAINT,
                        ConstraintSet.WRAP_CONTENT);
        bookShelf.addView(textTitle, textTitleParams);

        TextView textSubtitle = new TextView(root.getContext());
        textSubtitle.setTextColor(Color.BLACK);
        textSubtitle.setText(newBook.bookSubtitle);
        textSubtitle.setEllipsize(TextUtils.TruncateAt.END);
        textSubtitle.setMaxLines(4);
        textSubtitle.setId(textSubtitle.hashCode());
        ConstraintLayout.LayoutParams textSubtitleParams =
                new ConstraintLayout.LayoutParams(ConstraintSet.MATCH_CONSTRAINT,
                        ConstraintSet.WRAP_CONTENT);
        bookShelf.addView(textSubtitle, textSubtitleParams);

        TextView textPrice = new TextView(root.getContext());
        textPrice.setTextColor(Color.BLACK);
        textPrice.setText(newBook.bookPrice);
        textPrice.setId(textPrice.hashCode());
        ConstraintLayout.LayoutParams textPriceParams =
                new ConstraintLayout.LayoutParams(ConstraintSet.MATCH_CONSTRAINT,
                        ConstraintSet.WRAP_CONTENT);
        bookShelf.addView(textPrice, textPriceParams);

        ConstraintSet bookLayTmpSet = new ConstraintSet();
        bookLayTmpSet.clone(bookShelf);

        bookLayTmpSet.connect(bookPic.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        bookLayTmpSet.connect(bookPic.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        bookLayTmpSet.connect(bookPic.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        bookLayTmpSet.connect(textTitle.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        bookLayTmpSet.connect(textTitle.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        bookLayTmpSet.connect(textTitle.getId(), ConstraintSet.START,
                bookPic.getId(), ConstraintSet.END);
        bookLayTmpSet.connect(textTitle.getId(), ConstraintSet.BOTTOM,
                textSubtitle.getId(), ConstraintSet.TOP);
        bookLayTmpSet.setMargin(textTitle.getId(), ConstraintSet.START, 8);
        bookLayTmpSet.setMargin(textTitle.getId(), ConstraintSet.END, 8);

        bookLayTmpSet.connect(textSubtitle.getId(), ConstraintSet.TOP,
                textTitle.getId(), ConstraintSet.BOTTOM);
        bookLayTmpSet.connect(textSubtitle.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        bookLayTmpSet.connect(textSubtitle.getId(), ConstraintSet.START,
                bookPic.getId(), ConstraintSet.END);
        bookLayTmpSet.connect(textSubtitle.getId(), ConstraintSet.BOTTOM,
                textPrice.getId(), ConstraintSet.TOP);
        bookLayTmpSet.setMargin(textSubtitle.getId(), ConstraintSet.START, 8);
        bookLayTmpSet.setMargin(textSubtitle.getId(), ConstraintSet.END, 8);

        bookLayTmpSet.connect(textPrice.getId(), ConstraintSet.TOP,
                textSubtitle.getId(), ConstraintSet.BOTTOM);
        bookLayTmpSet.connect(textPrice.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        bookLayTmpSet.connect(textPrice.getId(), ConstraintSet.START,
                bookPic.getId(), ConstraintSet.END);
        bookLayTmpSet.connect(textPrice.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        bookLayTmpSet.setMargin(textPrice.getId(), ConstraintSet.START, 8);
        bookLayTmpSet.setMargin(textPrice.getId(), ConstraintSet.END, 8);

        bookLayTmpSet.applyTo(bookShelf);

        arrConstraint.add(bookShelf);
        arrBook.add(newBook);
    }
}