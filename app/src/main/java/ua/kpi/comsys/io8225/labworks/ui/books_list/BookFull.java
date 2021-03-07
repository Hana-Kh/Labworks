package ua.kpi.comsys.io8225.labworks.ui.books_list;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RawRes;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import ua.kpi.comsys.io8225.labworks.R;

public class BookFull {

    public void showPopupWindow(final View view, Book book) {
        view.getContext();
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.book_full_view, null);

        try {
            parseBookInfo(readTextFile(popupView.getContext(),
                    getResId("b" + book.bookIsbn13, R.raw.class)), book);
        } catch (ParseException e) {
            System.err.println("Incorrect content of JSON file!");
            e.printStackTrace();
        }

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        ((ImageView) popupView.findViewById(R.id.book_info_image)).setImageResource(
                (book.bookImagePath.length() == 0)? R.drawable.image_01 :
                        getResId(book.bookImagePath.toLowerCase()
                                .split("\\.")[0], R.drawable.class));
        ((TextView) popupView.findViewById(R.id.book_info_authors)).setText(book.bookAuthors);
        ((TextView) popupView.findViewById(R.id.book_info_description)).setText(book.bookDescription);
        ((TextView) popupView.findViewById(R.id.book_info_pages)).setText(book.bookPages);
        ((TextView) popupView.findViewById(R.id.book_info_publisher)).setText(book.bookPublisher);
        ((TextView) popupView.findViewById(R.id.book_info_rating)).setText(book.bookRating);
        ((TextView) popupView.findViewById(R.id.book_info_subtitle)).setText(book.bookSubtitle);
        ((TextView) popupView.findViewById(R.id.book_info_title)).setText(book.bookTitle);
        ((TextView) popupView.findViewById(R.id.book_info_year)).setText(book.bookYear);
    }

    private void parseBookInfo(String jsonText, Book book) throws ParseException {
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);
        book.bookAuthors = (String) jsonObject.get("authors");
        book.bookPublisher = (String) jsonObject.get("publisher");
        book.bookDescription = (String) jsonObject.get("desc");
        book.bookPages = (String) jsonObject.get("pages");
        book.bookRating = jsonObject.get("rating") + "/5";
        book.bookYear = (String) jsonObject.get("year");
    }

    private static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static String readTextFile(Context context, @RawRes int id){
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
}