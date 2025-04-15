package com.gipstech.androidtestapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

object PermissionHelper {
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null

    fun checkPermissions(activity: ComponentActivity, completed: (Boolean) -> Unit) {
        val permissions = getPermissions()

        val checkNext: () -> Unit = {
            var done = true
            while (permissions.isNotEmpty()) {
                val permission = permissions.removeAt(0)
                if (ContextCompat.checkSelfPermission(activity, permission!!) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher!!.launch(permission)
                    done = false
                    break
                }
            }
            if (done) {
                completed(true)
            }
        }

        requestPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted ->
            run {
                if (isGranted) {
                    checkNext()
                } else {
                    completed(false)
                }
            }
        }

        // Start permission checking
        checkNext()
    }

    fun hasAllPermissions(context: Context?): Boolean {
        for (permission in getPermissions()) {
            if (ContextCompat.checkSelfPermission(context!!, permission!!) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    private fun getPermissions(): ArrayList<String?> {
        val permissions: ArrayList<String?> = arrayListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //31
            permissions.add(android.Manifest.permission.BLUETOOTH_SCAN)
        } else {
            permissions.add(android.Manifest.permission.BLUETOOTH)
            permissions.add(android.Manifest.permission.BLUETOOTH_ADMIN)
        }

        return permissions
    }
}