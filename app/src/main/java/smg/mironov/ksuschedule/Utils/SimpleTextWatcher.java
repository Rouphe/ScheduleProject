package smg.mironov.ksuschedule.Utils;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // По умолчанию ничего не делаем
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // По умолчанию ничего не делаем
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Этот метод будет реализован в дочерних классах или анонимных реализациях
    }
}

