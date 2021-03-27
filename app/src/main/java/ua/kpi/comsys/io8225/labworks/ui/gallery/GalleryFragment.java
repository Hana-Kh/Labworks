package ua.kpi.comsys.io8225.labworks.ui.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

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
import java.util.List;
import java.util.concurrent.ExecutionException;

import ua.kpi.comsys.io8225.labworks.R;
import ua.kpi.comsys.io8225.labworks.ui.db.App;
import ua.kpi.comsys.io8225.labworks.ui.db.AppDatabase;
import ua.kpi.comsys.io8225.labworks.ui.db.GalleryEntity;

public class GalleryFragment extends Fragment {
    private static final int RESULT_LOAD_IMAGE = 2;

    private static View root;
    private static LinearLayout scrollMain;
    private static ArrayList<ImageView> allImages;
    private static ArrayList<ArrayList<Object>> placeholderList;
    private static AppDatabase database;
    private static HashMap<String, Long> urlToIdImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gallery, container, false);
        setRetainInstance(true);

        scrollMain = root.findViewById(R.id.scroll_linear);

        allImages = new ArrayList<>();
        placeholderList = new ArrayList<>();
        urlToIdImage = new HashMap<>();

        Button btnAddImage = root.findViewById(R.id.button_new_photo);
        btnAddImage.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
            gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            gallery.setType("image/*");
            startActivityForResult(gallery, RESULT_LOAD_IMAGE);
        });

        database = App.getInstance().getDatabase();

        AsyncLoadGallery aTask = new AsyncLoadGallery();
        aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                "20698765-8f303d7937e0db8dc16839d9b",
                "\"red+cars\"",
                "21");

        return root;
    }

    protected static void loadImages(ArrayList<String> images){
        if (images != null) {
            for (String img :
                    images) {
                new AsyncLoadBitmapToDB().execute(img);
                addImage(false, null, img);
            }
        }
        else {
            Toast.makeText(root.getContext(), "Cannot load data!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK){
            Uri imageUri = data.getData();
            ArrayList<Uri> uriList = new ArrayList<>();
            if (imageUri != null) {
                uriList.add(imageUri);
            }
            else {
                if (data.getClipData() != null){
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        uriList.add(mClipData.getItemAt(i).getUri());
                    }
                }
            }
            for (Uri uri :
                    uriList) {
                addImage(true, uri, null);
            }
        }
    }

    private static void addImage(boolean isLocal, Uri imageUri, String imageUrl) {

        ProgressBar loadingImageBar = new ProgressBar(root.getContext());
        loadingImageBar.setLayoutParams(
                new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        loadingImageBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(root.getContext(), R.color.purple_500),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        loadingImageBar.setVisibility(View.GONE);
        loadingImageBar.setId(loadingImageBar.hashCode());

        ImageView newImage = new ImageView(root.getContext());
        if (isLocal)
            newImage.setImageURI(imageUri);
        else {
            loadingImageBar.setVisibility(View.VISIBLE);
            new AsyncLoadImageFromDB(newImage, loadingImageBar).execute(imageUrl);
        }
        newImage.setBackgroundColor(Color.parseColor("#bb9c9c9c"));
        ConstraintLayout.LayoutParams imageParams =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        imageParams.dimensionRatio = "1";
        newImage.setLayoutParams(imageParams);
        newImage.setId(newImage.hashCode());

        setImagePlace(newImage, loadingImageBar);
        allImages.add(newImage);
    }

    private static void setImagePlace(ImageView newImage, ProgressBar loadBar){
        ConstraintLayout tmpLayout = null;
        ConstraintSet tmpSet = null;
        if (allImages.size() > 0) {
            tmpLayout = (ConstraintLayout) getConstraintArrayList(0, placeholderList);

            if (allImages.size() % 7 != 0) {
                tmpLayout.addView(newImage);
                tmpLayout.addView(loadBar);
            }

            tmpSet = (ConstraintSet) getConstraintArrayList(1, placeholderList);

            tmpSet.clone(tmpLayout);

            tmpSet.setMargin(newImage.getId(), ConstraintSet.START, 3);
            tmpSet.setMargin(newImage.getId(), ConstraintSet.TOP, 3);
            tmpSet.setMargin(newImage.getId(), ConstraintSet.END, 3);
            tmpSet.setMargin(newImage.getId(), ConstraintSet.BOTTOM, 3);
        }

        if (allImages.size() % 7 != 0) {
            tmpSet.connect(loadBar.getId(), ConstraintSet.START, newImage.getId(), ConstraintSet.START);
            tmpSet.connect(loadBar.getId(), ConstraintSet.TOP, newImage.getId(), ConstraintSet.TOP);
            tmpSet.connect(loadBar.getId(), ConstraintSet.END, newImage.getId(), ConstraintSet.END);
            tmpSet.connect(loadBar.getId(), ConstraintSet.BOTTOM, newImage.getId(), ConstraintSet.BOTTOM);
        }

        switch (allImages.size() % 7){
            case 0:{
                placeholderList.add(new ArrayList<>());

                ConstraintLayout newConstraint = new ConstraintLayout(root.getContext());
                placeholderList.get(placeholderList.size()-1).add(newConstraint);
                newConstraint.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                scrollMain.addView(newConstraint);

                Guideline vertical_20 = makeGuideline(ConstraintLayout.LayoutParams.VERTICAL,0.2f);
                Guideline vertical_80 = makeGuideline(ConstraintLayout.LayoutParams.VERTICAL,0.8f);

                Guideline horizontal_33 = makeGuideline(ConstraintLayout.LayoutParams.HORIZONTAL,1f);
                Guideline horizontal_66 = makeGuideline(ConstraintLayout.LayoutParams.HORIZONTAL,1f);

                newConstraint.addView(vertical_20, 0);
                newConstraint.addView(vertical_80, 1);
                newConstraint.addView(horizontal_33, 2);
                newConstraint.addView(horizontal_66, 3);

                newConstraint.addView(newImage);
                newConstraint.addView(loadBar);

                ConstraintSet newConstraintSet = new ConstraintSet();
                placeholderList.get(placeholderList.size()-1).add(newConstraintSet);
                newConstraintSet.clone(newConstraint);

                newConstraintSet.setMargin(newImage.getId(), ConstraintSet.START, 3);
                newConstraintSet.setMargin(newImage.getId(), ConstraintSet.TOP, 3);
                newConstraintSet.setMargin(newImage.getId(), ConstraintSet.END, 3);
                newConstraintSet.setMargin(newImage.getId(), ConstraintSet.BOTTOM, 3);

                newConstraintSet.connect(newImage.getId(), ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START);
                newConstraintSet.connect(newImage.getId(), ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                newConstraintSet.connect(newImage.getId(), ConstraintSet.END,
                        vertical_20.getId(), ConstraintSet.START);
                newConstraintSet.connect(newImage.getId(), ConstraintSet.BOTTOM,
                        horizontal_33.getId(), ConstraintSet.TOP);

                newConstraintSet.connect(loadBar.getId(), ConstraintSet.START, newImage.getId(), ConstraintSet.START);
                newConstraintSet.connect(loadBar.getId(), ConstraintSet.TOP, newImage.getId(), ConstraintSet.TOP);
                newConstraintSet.connect(loadBar.getId(), ConstraintSet.END, newImage.getId(), ConstraintSet.END);
                newConstraintSet.connect(loadBar.getId(), ConstraintSet.BOTTOM, newImage.getId(), ConstraintSet.BOTTOM);

                newConstraintSet.applyTo(newConstraint);
                break;
            }

            case 1: {
                tmpSet.setGuidelinePercent(tmpLayout.getChildAt(2).getId(), 0.333333f);
                tmpSet.setGuidelinePercent(tmpLayout.getChildAt(3).getId(), 0.666666f);

                tmpSet.connect(newImage.getId(), ConstraintSet.START,
                        tmpLayout.getChildAt(0).getId(), ConstraintSet.END);
                tmpSet.connect(newImage.getId(), ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                tmpSet.connect(newImage.getId(), ConstraintSet.END,
                        tmpLayout.getChildAt(1).getId(), ConstraintSet.START);
                tmpSet.connect(newImage.getId(), ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 2: {
                tmpSet.connect(newImage.getId(), ConstraintSet.START,
                        tmpLayout.getChildAt(1).getId(), ConstraintSet.END);
                tmpSet.connect(newImage.getId(), ConstraintSet.TOP,
                        ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                tmpSet.connect(newImage.getId(), ConstraintSet.END,
                        ConstraintSet.PARENT_ID, ConstraintSet.END);
                tmpSet.connect(newImage.getId(), ConstraintSet.BOTTOM,
                        tmpLayout.getChildAt(2).getId(), ConstraintSet.TOP);

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 3: {
                tmpSet.connect(newImage.getId(), ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START);
                tmpSet.connect(newImage.getId(), ConstraintSet.TOP,
                        tmpLayout.getChildAt(2).getId(), ConstraintSet.BOTTOM);
                tmpSet.connect(newImage.getId(), ConstraintSet.END,
                        tmpLayout.getChildAt(0).getId(), ConstraintSet.START);
                tmpSet.connect(newImage.getId(), ConstraintSet.BOTTOM,
                        tmpLayout.getChildAt(3).getId(), ConstraintSet.TOP);

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 4: {
                tmpSet.connect(newImage.getId(), ConstraintSet.START,
                        tmpLayout.getChildAt(1).getId(), ConstraintSet.END);
                tmpSet.connect(newImage.getId(), ConstraintSet.TOP,
                        tmpLayout.getChildAt(2).getId(), ConstraintSet.BOTTOM);
                tmpSet.connect(newImage.getId(), ConstraintSet.END,
                        ConstraintSet.PARENT_ID, ConstraintSet.END);
                tmpSet.connect(newImage.getId(), ConstraintSet.BOTTOM,
                        tmpLayout.getChildAt(3).getId(), ConstraintSet.TOP);

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 5: {
                tmpSet.connect(newImage.getId(), ConstraintSet.START,
                        ConstraintSet.PARENT_ID, ConstraintSet.START);
                tmpSet.connect(newImage.getId(), ConstraintSet.TOP,
                        tmpLayout.getChildAt(3).getId(), ConstraintSet.BOTTOM);
                tmpSet.connect(newImage.getId(), ConstraintSet.END,
                        tmpLayout.getChildAt(0).getId(), ConstraintSet.START);
                tmpSet.connect(newImage.getId(), ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

                tmpSet.applyTo(tmpLayout);
                break;
            }

            case 6: {
                tmpSet.connect(newImage.getId(), ConstraintSet.START,
                        tmpLayout.getChildAt(1).getId(), ConstraintSet.END);
                tmpSet.connect(newImage.getId(), ConstraintSet.TOP,
                        tmpLayout.getChildAt(3).getId(), ConstraintSet.BOTTOM);
                tmpSet.connect(newImage.getId(), ConstraintSet.END,
                        ConstraintSet.PARENT_ID, ConstraintSet.END);
                tmpSet.connect(newImage.getId(), ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

                tmpSet.applyTo(tmpLayout);
                break;
            }
        }
    }

    private static Guideline makeGuideline(int orientation, float percent){
        Guideline guideline = new Guideline(root.getContext());
        guideline.setId(guideline.hashCode());

        ConstraintLayout.LayoutParams guideline_Params =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        guideline_Params.orientation = orientation;

        guideline.setLayoutParams(guideline_Params);

        guideline.setGuidelinePercent(percent);

        return guideline;
    }

    private static Object getConstraintArrayList(int index, ArrayList<ArrayList<Object>> list){
        return list.get(list.size()-1).get(index);
    }

    private static Bitmap getBitmapByUrl(String url){
        try {
            return Glide.with(root.getContext()).asBitmap().load(url).submit().get();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            try {
                if (urlToIdImage.containsKey(url)){
                    long id = urlToIdImage.get(url);
                    if (database.galleryDao().getById(id) != null &&
                            database.galleryDao().getById(id).imageData != null)
                        return database.galleryDao().getById(id).getBitmapImage(root.getContext());
                }
                else {
                    if (database.galleryDao().getByUrl(url) != null &&
                            database.galleryDao().getByUrl(url).imageData != null)
                        return database.galleryDao().getByUrl(url).getBitmapImage(root.getContext());
                }
            } catch (ExecutionException | InterruptedException executionException) {
                executionException.printStackTrace();
            }
        }
        return null;
    }
    
    private static class AsyncLoadImageFromDB extends AsyncTask<String, Void, Void> {
        @SuppressLint("StaticFieldLeak")
        ImageView imageView;
        @SuppressLint("StaticFieldLeak")
        ProgressBar progressBar;
        Bitmap bitmap;

        public AsyncLoadImageFromDB(ImageView imageView, ProgressBar progressBar) {
            this.imageView = imageView;
            this.progressBar = progressBar;
        }

        @Override
        protected Void doInBackground(String... urls) {
            bitmap = getBitmapByUrl(urls[0]);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(root.getContext().getResources(), R.drawable._09243289_stock_vector_no_image_available_sign);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageView.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private static class AsyncLoadBitmapToDB extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            Bitmap mIcon11 = getBitmapByUrl(urls[0]);

            if (urlToIdImage.containsKey(urls[0])) {
                long id = urlToIdImage.get(urls[0]);
                if (database.galleryDao().getById(id) != null &&
                        database.galleryDao().getById(id).imageData == null && mIcon11 != null)
                    database.galleryDao().setImageBitmapById(id, GalleryEntity.getBitmapAsByteArray(mIcon11));
            }
            else {
                if (database.galleryDao().getByUrl(urls[0]) != null &&
                        database.galleryDao().getByUrl(urls[0]).imageData == null && mIcon11 != null)
                    database.galleryDao().setImageBitmapByUrl(urls[0], GalleryEntity.getBitmapAsByteArray(mIcon11));
            }
            return null;
        }
    }

    private static class AsyncLoadGallery extends AsyncTask<String, Void, ArrayList<String>> {
        private String getRequest(String url) throws UnknownHostException {
            StringBuilder result = new StringBuilder();
            BufferedReader in = null;
            long stopTime = System.currentTimeMillis() + 6000;

            while (in == null && System.currentTimeMillis() < stopTime) {
                try {
                    URL getReq = new URL(url);
                    URLConnection bookConnection = getReq.openConnection();
                    in = new BufferedReader(new InputStreamReader(bookConnection.getInputStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null)
                        result.append(inputLine).append("\n");

                    in.close();
                } catch (UnknownHostException e) {
                    throw new UnknownHostException();
                }catch (MalformedURLException e) {
                    System.err.println(String.format("Incorrect URL <%s>!", url));
                    //e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in == null){
                throw new UnknownHostException();
            }
            return result.toString();
        }

        private ArrayList<String> parseImages(String jsonText) throws ParseException {
            ArrayList<String> result = new ArrayList<>();

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);

            JSONArray images = (JSONArray) jsonObject.get("hits");
            for (Object img : images) {
                JSONObject tmp = (JSONObject) img;
                String url = (String) tmp.get("webformatURL");

                long id = -1;
                try {
                    id = (Long) tmp.get("id");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                GalleryEntity galleryEntity = new GalleryEntity(id, url, null);
                urlToIdImage.put(url, (Long) tmp.get("id"));
                if (database.galleryDao().getByUrl(url) == null &&
                        database.galleryDao().getById((Long) tmp.get("id")) == null){

                    database.galleryDao().insert(galleryEntity);
                }

                result.add(url);
            }

            return result;
        }

        private ArrayList<String> search(String api, String req, String count){
            String jsonResponse = String.format("https://pixabay.com/api/?key=%s&q=%s&image_type=photo&per_page=%s",
                    api, req, count);
            try {
                return parseImages(getRequest(jsonResponse));
            } catch (UnknownHostException e) {
                System.err.println("Request timeout!");
            } catch (ParseException e) {
                System.err.println("Incorrect content of JSON file!");
                e.printStackTrace();
            }
            return (ArrayList<String>) database.galleryDao().getAllUrls();
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            return search(strings[0], strings[1], strings[2]);
        }

        @Override
        protected void onPostExecute(ArrayList<String> images) {
            super.onPostExecute(images);
            if (images == null || images.size() == 0)
                Toast.makeText(root.getContext(), "Cannot load data!", Toast.LENGTH_SHORT).show();
            GalleryFragment.loadImages(images);
        }
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ProgressBar loadingBar;
        Context context;

        public DownloadImageTask(ImageView bmImage, ProgressBar loadingBar, Context context) {
            this.bmImage = bmImage;
            this.loadingBar = loadingBar;
            this.context = context;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null)
                bmImage.setImageBitmap(result);
            else {
                Toast.makeText(context, "Cannot load data!", Toast.LENGTH_LONG).show();
            }
            loadingBar.setVisibility(View.GONE);
            bmImage.setVisibility(View.VISIBLE);
        }
    }
}
