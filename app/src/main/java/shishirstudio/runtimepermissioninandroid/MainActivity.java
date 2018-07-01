package shishirstudio.runtimepermissioninandroid;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PermissionManager permissionManager;
    private Button openCameraButton;
    private ImageView imageView;
    private final int REQUEST_CAMERA = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        permissionManager = new PermissionManager(this);
        openCameraButton = findViewById(R.id.openCameraBtn);
        imageView = findViewById(R.id.imageView);

        openCameraButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        permissionManager.checkPermission(this, Manifest.permission.CAMERA, new PermissionManager.PermissionAskListener() {
            @Override
            public void onNeedPermission() {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }

            @Override
            public void onPermissionPreviouslyDenied() {
                showCameraRational();
            }

            @Override
            public void onPermissionPreviouslyDeniedWithNeverAskAgain() {
                dialogForSettings("Permission Denied", "Now you must allow camera access from settings.");
            }

            @Override
            public void onPermissionGranted() {
                openCamera();
            }
        });
    }

    private void goToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + getPackageName());
        intent.setData(uri);
        startActivity(intent);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void showCameraRational() {
        new AlertDialog.Builder(this).setTitle("Permission Denied").setMessage("Without this permission this app is unable to open camera to take your photo. Are you sure you want to deny this permission.")
                .setCancelable(false)
                .setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
                        dialog.dismiss();
                    }
                }).show();

    }

    private void dialogForSettings(String title, String msg) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToSettings();
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    // Permission was denied.......
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }
}
