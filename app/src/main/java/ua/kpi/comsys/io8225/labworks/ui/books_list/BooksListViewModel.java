package ua.kpi.comsys.io8225.labworks.ui.books_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BooksListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BooksListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}