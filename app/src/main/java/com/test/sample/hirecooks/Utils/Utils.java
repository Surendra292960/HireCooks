package com.test.sample.hirecooks.Utils;

import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;

import com.test.sample.hirecooks.Fragments.ContactUsFragment;
import com.test.sample.hirecooks.Fragments.Home.HomeFragment;
import com.test.sample.hirecooks.Fragments.Home.MenuListFragment;
import com.test.sample.hirecooks.Fragments.MessageFragment;
import com.test.sample.hirecooks.Fragments.SettingsFragment;
import com.test.sample.hirecooks.R;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static final String ATTRIBUTE_TTF_KEY = "ttf_name";

    public static final String ATTRIBUTE_SCHEMA = "http://schemas.android.com/apk/lib/com.hitesh_sahu.retailapp.util";

    public final static String SHOPPING_LIST_TAG = "SHoppingListFragment";
    public static final String PRODUCT_OVERVIEW_FRAGMENT_TAG = "ProductOverView";
    public static final String HOME_FRAGMENT = "HomeFragment";
    public static final String PROFILE_FRAGMENT = "ProfileFragment";
    public static final String MESSAGE_FRAGMENT = "MessageFragment";
    public static final String MENUELIST_FRAGMENT = "MenuListFragment";
    public static final String COOKS_FRAGMENT = "OrdersFragment";
    public static final String SEARCH_FRAGMENT = "NotificationFragment";
    public static final String SETTINGS_FRAGMENT = "SettingsFragment";
    public static final String CONTACT_US_FRAGMENT = "ContactUs";
    public static final String WISHLIST_FRAGMENT = "WishlistFragment";
    public static final String MYORDERS_FRAGMENT = "MyordersFragment";

    private static final String PREFERENCES_FILE = "materialsample_settings";
    private static String CURRENT_TAG = null;
    private static Map<String, Typeface> TYPEFACE = new HashMap<String, Typeface>();

    public static int getToolbarHeight(Context context) {
        int height = (int) context.getResources().getDimension(
                R.dimen.abc_action_bar_default_height_material);
        return height;
    }

    public static int getStatusBarHeight(Context context) {
        int height = (int) context.getResources().getDimension(
                R.dimen.statusbar_size);
        return height;
    }

    public static Drawable tintMyDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static String getRealPathFromURI(Uri contentUri, Context mContext) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(mContext, contentUri, proj,
                null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    /**
     * Convert milliseconds into time hh:mm:ss
     *
     * @param milliseconds
     * @return time in String
     */
    public static String getDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if (hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static void switchFragmentWithAnimation(int id, Fragment fragment, FragmentActivity activity, String TAG, AnimationType transitionStyle) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (transitionStyle != null) {
            switch (transitionStyle) {
                case SLIDE_DOWN:
                    fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                    break;
                case SLIDE_UP:
                    //fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                    break;
                case SLIDE_LEFT:
                    //fragmentTransaction.setCustomAnimations(R.anim.slide_left, R.anim.slide_out_left);
                    break;
                case SLIDE_RIGHT:
                    //fragmentTransaction.setCustomAnimations(R.anim.slide_right, R.anim.slide_out_right);
                    break;
                case FADE_IN:
                    //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                case FADE_OUT:
                    //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.donot_move);
                    break;
                case SLIDE_IN_SLIDE_OUT:
                  //  fragmentTransaction.setCustomAnimations(R.anim.slide_left, R.anim.slide_out_left);
                    break;
                default:
                    break;
            }
        }
        CURRENT_TAG = TAG;
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    public static void switchContent(int id, String TAG, FragmentActivity baseActivity, AnimationType transitionStyle) {
        Fragment fragmentToReplace = null;
        FragmentManager fragmentManager = baseActivity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // If our current fragment is null, or the new fragment is different, we
        // need to change our current fragment
        if (CURRENT_TAG == null || !TAG.equals(CURRENT_TAG)) {
            if (transitionStyle != null) {
                switch (transitionStyle) {
                    case SLIDE_DOWN:
                        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                        break;
                    case SLIDE_UP:
                        //transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                        break;
                    case SLIDE_LEFT:
                        //transaction.setCustomAnimations(R.anim.slide_left, R.anim.slide_out_left);
                        break;
                    case SLIDE_RIGHT:
                        //transaction.setCustomAnimations(R.anim.slide_right, R.anim.slide_out_right);
                        break;
                    case FADE_IN:
                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    case FADE_OUT:
                        //transaction.setCustomAnimations(R.anim.fade_in, R.anim.donot_move);
                        break;
                    case SLIDE_IN_SLIDE_OUT:
                      //  transaction.setCustomAnimations(R.anim.slide_left, R.anim.slide_out_left);
                        break;
                    default:
                        break;
                }
            }

            // Try to find the fragment we are switching to
            Fragment fragment = fragmentManager.findFragmentByTag(TAG);
            // If the new fragment can't be found in the manager, create a new
            // one
            if (fragment == null) {

                if (TAG.equals(HOME_FRAGMENT)) {
                    fragmentToReplace = new HomeFragment();
                } else if (TAG.equals(MENUELIST_FRAGMENT)) {
                    fragmentToReplace = new MenuListFragment();
                } else if (TAG.equals(SETTINGS_FRAGMENT)) {
                    fragmentToReplace = new SettingsFragment();
                } else if (TAG.equals(CONTACT_US_FRAGMENT)) {
                    fragmentToReplace = new ContactUsFragment();
                } else if (TAG.equals(MESSAGE_FRAGMENT)) {
                    fragmentToReplace = new MessageFragment();
                }

            } else {
                if (TAG.equals(HOME_FRAGMENT)) {
                    fragmentToReplace = (HomeFragment) fragment;
                } else if (TAG.equals(MENUELIST_FRAGMENT)) {
                    fragmentToReplace = (MenuListFragment) fragment;
                } else if (TAG.equals(SETTINGS_FRAGMENT)) {
                    fragmentToReplace = (SettingsFragment) fragment;
                } else if (TAG.equals(CONTACT_US_FRAGMENT)) {
                    fragmentToReplace = (ContactUsFragment) fragment;
                }else if (TAG.equals(MESSAGE_FRAGMENT)) {
                    fragmentToReplace = (MessageFragment) fragment;
                }
            }

            CURRENT_TAG = TAG;

            // Replace our current fragment with the one we are changing to
            transaction.replace(id, fragmentToReplace, TAG);
            transaction.commit();

        } else {
            // Do nothing since we are already on the fragment being changed to
        }
    }

    public static void vibrate(Context context) {
        // Get instance of Vibrator from current Context and Vibrate for 400
        // milliseconds
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE))
                .vibrate(100);
    }

    public static String getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            return String.valueOf(pInfo.versionCode) + " " + pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            return "1.0.1";
        }
    }

    public static Typeface getFonts(Context context, String fontName) {
        Typeface typeface = TYPEFACE.get(fontName);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "font/"
                    + fontName);
            TYPEFACE.put(fontName, typeface);
        }
        return typeface;
    }

    public enum AnimationType {
        SLIDE_LEFT, SLIDE_RIGHT, SLIDE_UP, SLIDE_DOWN, FADE_IN, SLIDE_IN_SLIDE_OUT, FADE_OUT
    }
}