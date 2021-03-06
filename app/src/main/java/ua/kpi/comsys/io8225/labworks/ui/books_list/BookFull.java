package ua.kpi.comsys.io8225.labworks.ui.books_list;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RawRes;
import androidx.annotation.RequiresApi;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import ua.kpi.comsys.io8225.labworks.R;
import ua.kpi.comsys.io8225.labworks.ui.db.App;
import ua.kpi.comsys.io8225.labworks.ui.db.AppDatabase;
import ua.kpi.comsys.io8225.labworks.ui.gallery.GalleryFragment;

public class BookFull {
    private static View popupView;
    private static ProgressBar loadingImage;
    private static ImageView bookImage;
    private static Book book;
    private static AppDatabase database;

    public void showPopupWindow(final View view, Book book) {
        view.getContext();
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.book_full_view, null);
        BookFull.book = book;

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;

        loadingImage = popupView.findViewById(R.id.loadingImageInfo);
        bookImage = popupView.findViewById(R.id.book_info_image);

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        database = App.getInstance().getDatabase();

        AsyncLoadBookInfo aTask = new AsyncLoadBookInfo();
        aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BookFull.book.bookIsbn13);
    }

    protected static void setInfoData(){
        if (book.bookImagePath != null) {
            bookImage.setVisibility(View.INVISIBLE);
            loadingImage.setVisibility(View.VISIBLE);
            new GalleryFragment.DownloadImageTask(bookImage, loadingImage, popupView.getContext()).execute(book.bookImagePath);
        }
        else {
            bookImage.setVisibility(View.INVISIBLE);
            loadingImage.setVisibility(View.VISIBLE);
            bookImage.setImageBitmap(book.bookImage);
            loadingImage.setVisibility(View.GONE);
            bookImage.setVisibility(View.VISIBLE);
        }
        bookImage.setVisibility(View.INVISIBLE);
        loadingImage.setVisibility(View.VISIBLE);
        new GalleryFragment.DownloadImageTask(bookImage, loadingImage, popupView.getContext()).execute(book.bookImagePath);

        ((TextView) popupView.findViewById(R.id.book_info_authors)).setText(book.bookAuthors);
        ((TextView) popupView.findViewById(R.id.book_info_description)).setText(book.bookDescription);
        ((TextView) popupView.findViewById(R.id.book_info_pages)).setText(book.bookPages);
        ((TextView) popupView.findViewById(R.id.book_info_publisher)).setText(book.bookPublisher);
        ((TextView) popupView.findViewById(R.id.book_info_rating)).setText(book.bookRating);
        ((TextView) popupView.findViewById(R.id.book_info_subtitle)).setText(book.bookSubtitle);
        ((TextView) popupView.findViewById(R.id.book_info_title)).setText(book.bookTitle);
        ((TextView) popupView.findViewById(R.id.book_info_year)).setText(book.bookYear);
    }

    public static class AsyncLoadBookInfoToDB extends AsyncTask<Book, Void, Void> {
        @Override
        protected Void doInBackground(Book... books) {
            database.bookDao().setInfoByIsbn13(Long.parseLong(books[0].bookIsbn13),
                    books[0].bookAuthors,
                    books[0].bookDescription,
                    Long.parseLong(books[0].bookPages),
                    books[0].bookPublisher,
                    books[0].bookRating,
                    Long.parseLong(books[0].bookYear));
            return null;
        }
    }

    private static class AsyncLoadBookInfo extends AsyncTask<String, Void, Void> {
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

        private void parseBookInfo(String jsonText) throws ParseException {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);
            book.bookAuthors = (String) jsonObject.get("authors");
            book.bookPublisher = (String) jsonObject.get("publisher");
            book.bookDescription = (String) jsonObject.get("desc");
            book.bookPages = (String) jsonObject.get("pages");
            book.bookRating = jsonObject.get("rating") + "/5";
            book.bookYear = (String) jsonObject.get("year");
        }

        private void fromDB(long isbn){
            book = database.bookDao().getByIsbn13(isbn).makeBookInfo();
        }

        private void toDb(){
            new AsyncLoadBookInfoToDB().execute(book);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private void search(String isbn13) {
            String jsonResponse = String.format("https://api.itbook.store/1.0/books/%s", isbn13);
            try {
                String req = getRequest(jsonResponse);
                parseBookInfo(req);
                toDb();
            } catch (MalformedURLException e) {
                System.err.println(String.format("Incorrect URL <%s>!", jsonResponse));
                e.printStackTrace();
            } catch (UnknownHostException e) {
                System.err.println("Request timeout!");
                fromDB(Long.parseLong(isbn13));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                System.err.println("Incorrect content of JSON file!");
                e.printStackTrace();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Void doInBackground(String... strings) {
            search(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setInfoData();
        }
    }
}