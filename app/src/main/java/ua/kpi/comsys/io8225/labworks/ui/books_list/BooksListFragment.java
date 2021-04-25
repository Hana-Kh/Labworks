package ua.kpi.comsys.io8225.labworks.ui.books_list;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.daimajia.swipe.SwipeLayout;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ua.kpi.comsys.io8225.labworks.R;
import ua.kpi.comsys.io8225.labworks.ui.db.App;
import ua.kpi.comsys.io8225.labworks.ui.db.AppDatabase;
import ua.kpi.comsys.io8225.labworks.ui.db.BooksEntity;
import ua.kpi.comsys.io8225.labworks.ui.db.SearchEntity;
import ua.kpi.comsys.io8225.labworks.ui.gallery.GalleryFragment;

public class BooksListFragment extends Fragment {

    static LinearLayout mainLayout;
    static View root;
    static TextView noBooks;
    static ProgressBar loadingBar;
    static HashMap<ConstraintLayout, Book> booksMap;
    static Set<ConstraintLayout> removeSet;
    private static AppDatabase database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_bookslist, container, false);
        setRetainInstance(true);

        mainLayout = root.findViewById(R.id.linear_main);

        SearchView searchView = root.findViewById(R.id.search_view);
        loadingBar = root.findViewById(R.id.no_items_progressbar);
        noBooks = root.findViewById(R.id.no_books_view);

        booksMap = new HashMap<>();
        removeSet = new HashSet<>();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                removeSet.addAll(booksMap.keySet());
                if (newText.length() >= 3) {
                    AsyncLoadBooks aTask = new AsyncLoadBooks();
                    loadingBar.setVisibility(View.VISIBLE);
                    noBooks.setVisibility(View.GONE);
                    aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newText);
                }
                else {
                    for (ConstraintLayout constraintLayout : removeSet) {
                        binClicked(constraintLayout);
                    }
                    removeSet.clear();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                removeSet.addAll(booksMap.keySet());
                if (newText.length() >= 3) {
                    AsyncLoadBooks aTask = new AsyncLoadBooks();
                    loadingBar.setVisibility(View.VISIBLE);
                    noBooks.setVisibility(View.GONE);
                    aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newText);
                }
                else {
                    for (ConstraintLayout constraintLayout : removeSet) {
                        binClicked(constraintLayout);
                    }
                    removeSet.clear();
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

                    noBooks.setVisibility(View.GONE);
                    orientChange();

                    popupWindow.dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "Incorrect data!",
                            Toast.LENGTH_LONG).show();
                }
            });
        });

        database = App.getInstance().getDatabase();

        return root;
    }

    protected static void loadBooks(ArrayList<Book> books){
        if (books != null) {
            for (ConstraintLayout constraintLayout : removeSet) {
                binClicked(constraintLayout);
            }
            removeSet.clear();
            if (books.size() > 0) {
                noBooks.setVisibility(View.GONE);
                for (Book book :
                        books) {
                    addNewBook(book);
                }
            } else {
                noBooks.setVisibility(View.VISIBLE);
            }
        }
        else {
            Toast.makeText(root.getContext(), "Cannot load data!", Toast.LENGTH_LONG).show();
            noBooks.setVisibility(View.VISIBLE);
        }
        loadingBar.setVisibility(View.GONE);
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
                booksMap.keySet()) {
            bookshelf.getChildAt(0).setLayoutParams(
                    new ConstraintLayout.LayoutParams(width/3, width/3));
        }
    }

    private static void binClicked(ConstraintLayout key){
        booksMap.remove(key);
        mainLayout.removeView((SwipeLayout) key.getParent());
        if (booksMap.keySet().isEmpty())
            noBooks.setVisibility(View.VISIBLE);
    }

    private static void addNewBook(Book newBook){
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

        deleteButton.setOnClickListener(v -> binClicked(bookShelf));
        bookShelf.setOnClickListener(v -> {
            if (newBook.bookIsbn13.length() != 0 && !newBook.bookIsbn13.equals("noid")) {
                BookFull popUpClass = new BookFull();
                popUpClass.showPopupWindow(v, newBook);
            }
        });

        ProgressBar loadingImageBar = new ProgressBar(root.getContext());
        loadingImageBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(root.getContext(), R.color.purple_500),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        loadingImageBar.setVisibility(View.GONE);
        loadingImageBar.setId(loadingImageBar.hashCode());
        bookShelf.addView(loadingImageBar);

        ImageView bookPic = new ImageView(root.getContext());
        bookPic.setId(bookPic.hashCode());
        if (newBook.bookImagePath != null) {
            if (newBook.bookImagePath.length() != 0) {
                bookPic.setVisibility(View.INVISIBLE);
                loadingImageBar.setVisibility(View.VISIBLE);
                new GalleryFragment.DownloadImageTask(bookPic, loadingImageBar, root.getContext()).execute(newBook.bookImagePath);
            }
        }else {
            bookPic.setVisibility(View.INVISIBLE);
            loadingImageBar.setVisibility(View.VISIBLE);
            bookPic.setImageBitmap(newBook.bookImage);
            loadingImageBar.setVisibility(View.GONE);
            bookPic.setVisibility(View.VISIBLE);
        }
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

        bookLayTmpSet.connect(loadingImageBar.getId(), ConstraintSet.START,
                bookPic.getId(), ConstraintSet.START);
        bookLayTmpSet.connect(loadingImageBar.getId(), ConstraintSet.TOP,
                bookPic.getId(), ConstraintSet.TOP);
        bookLayTmpSet.connect(loadingImageBar.getId(), ConstraintSet.END,
                bookPic.getId(), ConstraintSet.END);
        bookLayTmpSet.connect(loadingImageBar.getId(), ConstraintSet.BOTTOM,
                bookPic.getId(), ConstraintSet.BOTTOM);

        bookLayTmpSet.applyTo(bookShelf);

        booksMap.put(bookShelf, newBook);
    }

    private static class AsyncLoadBookToDB extends AsyncTask<Book, Void, Void> {
        @Override
        protected Void doInBackground(Book... books) {
            BooksEntity booksEntity = new BooksEntity(Long.parseLong(books[0].bookIsbn13),
                    books[0].bookTitle,
                    books[0].bookSubtitle,
                    books[0].bookPrice,
                    "", "", 0, "", "", 0);
            String urldisplay = books[0].bookImagePath;
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            if (mIcon11 != null) {
                booksEntity.setImage(mIcon11);

                database.bookDao().insert(booksEntity);
                SearchEntity search = database.searchTableDao().getLastSearch();
                ArrayList<Long> booksIsbn = new ArrayList<>(search.searchedBooks);
                booksIsbn.add(booksEntity.getIsbn13());
                search.searchedBooks = booksIsbn;

                database.searchTableDao().update(search);
            }
            return null;
        }
    }

    private static class AsyncLoadBooks extends AsyncTask<String, Void, ArrayList<Book>> {

        private String getRequest(String url) throws IOException{
            StringBuilder result = new StringBuilder();

            URL getReq = new URL(url);
            URLConnection bookConnection = getReq.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(bookConnection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                result.append(inputLine).append("\n");

            in.close();

            return result.toString();
        }

        private ArrayList<Book> parseBooks(String jsonText) throws ParseException {
            ArrayList<Book> result = new ArrayList<>();

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);

            JSONArray books = (JSONArray) jsonObject.get("books");
            for (Object book : books) {
                JSONObject tmp = (JSONObject) book;
                result.add(new Book(
                        (String) tmp.get("title"),
                        (String) tmp.get("subtitle"),
                        (String) tmp.get("price"),
                        (String) tmp.get("isbn13"),
                        (String) tmp.get("image")
                ));
            }
            return result;
        }

        private ArrayList<Book> offlineLoad(String query){
            ArrayList<Book> newBooks = new ArrayList<>();
            ArrayList<Long> isbns = database.searchTableDao().getLastByQuery(query).searchedBooks;
            for (long isbn :
                    isbns) {
                newBooks.add(database.bookDao().getByIsbn13(isbn).makeBook());
            }
            return newBooks;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private ArrayList<Book> search(String newText){
            String jsonResponse = String.format("https://api.itbook.store/1.0/search/\"%s\"", newText);
            try {
                ArrayList<Book> books = parseBooks(getRequest(jsonResponse));

                SearchEntity newSearch = new SearchEntity();
                newSearch.searchQueue = newText;
                newSearch.searchedBooks = new ArrayList<>();
                database.searchTableDao().insert(newSearch);

                for (Book book :
                        books) {
                    new AsyncLoadBookToDB().execute(book);
                }
                return books;
            } catch (UnknownHostException e) {

                System.err.println("Request timeout!");
                if (database.searchTableDao().getLastByQuery(newText) != null) {
                    return offlineLoad(newText);
                }

            } catch (MalformedURLException e) {
                System.err.println(String.format("Incorrect URL <%s>!", jsonResponse));
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                System.err.println("Incorrect content of JSON file!");
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected ArrayList<Book> doInBackground(String... strings) {
            String searchQueue = strings[0];
            return search(searchQueue);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            super.onPostExecute(books);
            if (books == null)
                Toast.makeText(root.getContext(), "Cannot load data!", Toast.LENGTH_SHORT).show();
            loadBooks(books);
        }
    }
}