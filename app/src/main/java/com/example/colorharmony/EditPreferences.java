package com.example.colorharmony;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;


public class EditPreferences extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, new Prefs()).commit();
        }

    }


    public static class Prefs extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        SwitchPreference themeToggle;
        SharedPreferences prefs;
        Boolean is_dark = false;
        AppCompatDelegate mDelegate = null;
        public boolean onAttachSwitchState;
        public boolean onDetachSwitchState;
        MainActivity listener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            listener = new MainActivity();


            addPreferencesFromResource(R.xml.preferences);

        }

        @Override
        public void onResume() {
            super.onResume();

            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
            super.onPause();
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            onAttachSwitchState = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("theme_switch", false);
        }

        @Override
        public void onDetach() {

            onDetachSwitchState =PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("theme_switch", false);

            if(onDetachSwitchState != onAttachSwitchState) {

                //first check if theme was changed
                PackageManager packageManager = getActivity().getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(getActivity().getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(), R.anim., ); //add animations
                    //getActivity().startActivity(mainIntent, options.toBundle());
                    getActivity().startActivity(mainIntent);
                } else {
                    getActivity().startActivity(mainIntent);
                }
                System.exit(0);
            }

            super.onDetach();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Toast.makeText(getActivity(), "KEY: " + key, Toast.LENGTH_SHORT).show();
            switch (key) {
                case "theme_switch":
                    Toast.makeText(getActivity(), "SWITCH", Toast.LENGTH_SHORT).show();
                    is_dark = prefs.getBoolean("dark_switch", false);


            }
        }

        private AppCompatDelegate getDelegate() {
            if (mDelegate == null) {
                mDelegate = AppCompatDelegate.create(getActivity(), null);
            }
            return mDelegate;
        }

        public static void doRestart(Context c) {
            try {
                //check if the context is given
                if (c != null) {
                    //fetch the packagemanager so we can get the default launch activity
                    // (you can replace this intent with any other activity if you want
                    PackageManager pm = c.getPackageManager();
                    //check if we got the PackageManager
                    if (pm != null) {
                        //create the intent with the default start activity for your application
                        Intent mStartActivity = pm.getLaunchIntentForPackage(
                                c.getPackageName()
                        );
                        if (mStartActivity != null) {
                            mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //create a pending intent so the application is restarted after System.exit(0) was called.
                            // We use an AlarmManager to call this intent in 100ms
                            int mPendingIntentId = 223344;
                            PendingIntent mPendingIntent = PendingIntent
                                    .getActivity(c, mPendingIntentId, mStartActivity,
                                            PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            //kill the application
                            System.exit(0);
                        } else {
                        }
                    } else {
                    }
                }
            } catch (Exception ex) {
            }
        }
    }
}
