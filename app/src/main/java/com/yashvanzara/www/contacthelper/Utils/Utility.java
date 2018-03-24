package com.yashvanzara.www.contacthelper.Utils;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.yashvanzara.www.contacthelper.R;

/**
 * Created by User on 22-03-2018.
 */

public final class Utility {
    /*Class for frequently used functionalities throughout the application*/

    public static void changeFragment(Fragment targetFragment, FragmentActivity fa){
        /*Utility function for changing fragments*/
        fa.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    public static void SnackShort(String message, View v){
        /*Snackbar for Short Length*/
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT).show();
    }
    private Utility() throws InstantiationException {
        throw new InstantiationException("This utility class is not created for instantiation");
    }
}
