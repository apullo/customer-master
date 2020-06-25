package co.cvdcc.brojekcustomer.utils;

import android.view.View;

import androidx.annotation.StringRes;

/**
 * Created by bradhawk on 10/17/2016.
 */

public interface SnackbarController {
    void showSnackbar(@StringRes int stringRes, int duration, @StringRes int actionResText, View.OnClickListener onClickListener);
}
