package com.sachintha.posapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sachintha.posapp.database.entity.User;

/**
 * Session Manager for handling user login sessions
 */
public class SessionManager {

    private static final String PREF_NAME = "POSSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_ROLE = "role";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    private static SessionManager instance;

    private SessionManager(Context context) {
        this.context = context.getApplicationContext();
        prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    /**
     * Create login session
     */
    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putString(KEY_ROLE, user.getRole());
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get logged in user ID
     */
    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    /**
     * Get logged in username
     */
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    /**
     * Get logged in user's full name
     */
    public String getFullName() {
        return prefs.getString(KEY_FULL_NAME, "");
    }

    /**
     * Get logged in user's role
     */
    public String getRole() {
        return prefs.getString(KEY_ROLE, "");
    }

    /**
     * Check if logged in user is admin
     */
    public boolean isAdmin() {
        return "ADMIN".equals(getRole());
    }

    /**
     * Clear session (logout)
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
