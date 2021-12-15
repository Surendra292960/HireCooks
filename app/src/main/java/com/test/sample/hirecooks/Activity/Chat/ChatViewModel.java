package com.test.sample.hirecooks.Activity.Chat;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class ChatViewModel extends AndroidViewModel {

    private final Repository repository;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }
}
