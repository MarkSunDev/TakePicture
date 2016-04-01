package com.bibi.mark.takepicture;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_REQUEST_CAMERA_PHOTO = 200;
    private static final int CODE_REQUEST_CROP_PHOTO = 201;
    private static final int CODE_REQUEST_PICTURE = 202;
    private ImageView pictureImg;
    private String mCurrentPhotoName;
    private String mNewCurrentPhotoName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_main);
        Button cameraBtn = (Button) findViewById(R.id.btn_take_camera);
        Button photoBtn = (Button) findViewById(R.id.btn_take_photo);
        pictureImg = (ImageView) findViewById(R.id.img_picture);
        assert cameraBtn != null;
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mCurrentPhotoName = System.currentTimeMillis() + ".jpg";
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), mCurrentPhotoName)));
                startActivityForResult(intent, CODE_REQUEST_CAMERA_PHOTO);
            }
        });
        assert photoBtn != null;
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFromGallery = new Intent();
                intentFromGallery.setType("image/*");//选择图片
                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentFromGallery, CODE_REQUEST_PICTURE);
            }
        });
    }

    private void checkPermission(){
        String permission1 = "android.permission.CAMERA";
        String permission2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        String permission3 = "android.permission.READ_EXTERNAL_STORAGE";
        String[] permissionArray  = {permission1, permission2, permission3};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArray, 123);
        }
    }

    /**
     * 判断是否有存储卡，有返回TRUE，否则FALSE
     * @return
     */
    public static boolean isSDcardExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_REQUEST_CAMERA_PHOTO){
            if(resultCode == Activity.RESULT_OK){
                if(data == null) {
                    return;
                }
                File tempFile = new File(
                        Environment.getExternalStorageDirectory(),
                        mCurrentPhotoName);
                mNewCurrentPhotoName = "pz_" + mCurrentPhotoName;
                String outPath = Environment.getExternalStorageDirectory() + File.separator + mNewCurrentPhotoName;
                CropPhotoUtil.cropRawPhoto(this, Uri.fromFile(tempFile), CODE_REQUEST_CROP_PHOTO, outPath, 200, 200);
            }
        }else if(requestCode == CODE_REQUEST_CROP_PHOTO){
            if(resultCode == Activity.RESULT_OK){
                String path =  Environment.getExternalStorageDirectory() + File.separator + mNewCurrentPhotoName;
                ImageUtils.getInstance(this).setHeadViewFile(pictureImg, path, ImageUtils.ImageType.IMAGE_TYPE_CRICLE);
            }
        }else if(requestCode == CODE_REQUEST_PICTURE && resultCode == Activity.RESULT_OK){
            if(data == null) return;
            mNewCurrentPhotoName = System.currentTimeMillis() + ".jpg";
            String outPath = Environment.getExternalStorageDirectory() + File.separator + mNewCurrentPhotoName;
            CropPhotoUtil.cropRawPhoto(this, data.getData(), CODE_REQUEST_CROP_PHOTO, outPath, 200, 200);
        }
    }
}
