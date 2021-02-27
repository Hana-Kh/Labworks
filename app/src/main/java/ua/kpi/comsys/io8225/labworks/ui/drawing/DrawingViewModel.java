package ua.kpi.comsys.io8225.labworks.ui.drawing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DrawingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DrawingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}