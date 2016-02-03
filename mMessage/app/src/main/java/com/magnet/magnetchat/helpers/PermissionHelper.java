package com.magnet.magnetchat.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {

    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public static final String STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String LOCATION_PERMISSION1 = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String LOCATION_PERMISSION2 = Manifest.permission.ACCESS_FINE_LOCATION;

    public static boolean checkPermission(Activity activity, int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsToAdd = new ArrayList<>();
            for (String permission : permissions) {
                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToAdd.add(permission);
                }
            }
            if (!permissionsToAdd.isEmpty()) {
                activity.requestPermissions(permissionsToAdd.toArray(new String[permissionsToAdd.size()]), requestCode);
                return true;
            }
        }
        return false;
    }

}
