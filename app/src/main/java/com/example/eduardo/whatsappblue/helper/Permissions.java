package com.example.eduardo.whatsappblue.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissions {

    public static boolean validatePermissions(String[] permissions, Activity activity, int requestCode){
        if (Build.VERSION.SDK_INT >= 23){
            List<String> listPermissions = new ArrayList<>();

            /*Scroll past permissions, checking one by one */
            /*if you already have permission released*/
            for (String permission : permissions){
                boolean havePermission =  ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if (!havePermission){
                    listPermissions.add(permission);
                }
            }

            //Verify if list is empty
            if (listPermissions.isEmpty()) return true;

            String[] newPermissions = new String[listPermissions.size()];
            listPermissions.toArray(newPermissions);

            //Request Permission
            ActivityCompat.requestPermissions(activity, newPermissions, requestCode);
        }
        return true;
    }
}
