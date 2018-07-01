package shishirstudio.runtimepermissioninandroid;

import android.content.Context;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ShiShir on 01/07/2018.
 */

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String MY_PREF = "my_preference";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(MY_PREF, MODE_PRIVATE);
    }

    public void firstTimeAskingPermission(String permission, boolean isFirstTime) {
        doEdit();
        editor.putBoolean(permission, isFirstTime);
        doCommit();
    }

    public boolean isFirstTimeAskingPermission(String permission) {
        return sharedPreferences.getBoolean(permission, true);
    }

    private void doEdit() {
        if (editor == null) {
            editor = sharedPreferences.edit();
        }
    }

    private void doCommit() {
        if (editor != null) {
            editor.commit();
            editor = null;
        }
    }
}
