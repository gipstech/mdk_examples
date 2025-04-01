package com.gipstech.kmmtestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gipstech.mdk.GiPStech

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PermissionHelper.checkPermissions(this) { allGranted ->
            if (allGranted) {
                setContent {
                    App(this)
                }
            } else {
                setContent {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "This app requires all permissions to work")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (GiPStech.isInitialized) {
            GiPStech.shutdown()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(null)
}