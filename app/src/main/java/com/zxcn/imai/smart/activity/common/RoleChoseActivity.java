package com.zxcn.imai.smart.activity.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.pressure.MeasureTipActivity;
import com.zxcn.imai.smart.base.AppConstant;
import com.zxcn.imai.smart.base.PermissionsActivity;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.ui.GetPicDialog;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.PhotoUtils;
import com.zxcn.imai.smart.util.SpUtils;

import java.io.File;

/**
 * Created by ZXCN1 on 2017/9/25.
 */

public class RoleChoseActivity extends PermissionsActivity {

    private String TAG = "RoleChoseActivity";

    protected String[] needContactsPermissions = {
            Manifest.permission.CAMERA
    };

    // 拍照
    private static final int PHOTO_CAMERA = 1;
    // 相册中选择
    private static final int PHOTO_PICK = 2;

    private HeaderView headerView;

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;

    private ImageView camera1;
    private ImageView camera2;
    private ImageView camera3;
    private ImageView camera4;

    private GetPicDialog dialog;

    private String tempFile;

    private String userType;
    private ImageView choseIV;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_role_chose,null);
    }

    @Override
    protected void setEvent() {
        headerView = findViewById(R.id.headerView);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        camera1 = findViewById(R.id.camera1);
        camera2 = findViewById(R.id.camera2);
        camera3 = findViewById(R.id.camera3);
        camera4 = findViewById(R.id.camera4);

        Intent intent=new Intent(RoleChoseActivity.this,MeasureTipActivity.class);
        image1.setOnClickListener(view -> {
            SpUtils.saveString(SpConstant.USER_TYPE, AppConstant.USER_GRANDPA);
          //  MeasureTipActivity.newActivity(this);
            startActivity(intent);
        });
        image2.setOnClickListener(view -> {
            SpUtils.saveString(SpConstant.USER_TYPE, AppConstant.USER_GRANDMA);
//            MeasureTipActivity.newActivity(this);
            startActivity(intent);
        });
        image3.setOnClickListener(view -> {
            SpUtils.saveString(SpConstant.USER_TYPE, AppConstant.USER_FATHER);
//            MeasureTipActivity.newActivity(this);
            startActivity(intent);
        });
        image4.setOnClickListener(view -> {
            SpUtils.saveString(SpConstant.USER_TYPE, AppConstant.USER_MATHER);
//            MeasureTipActivity.newActivity(this);
            startActivity(intent);
        });

        camera1.setOnClickListener(view -> {
            choseIV = image1;
            userType = AppConstant.USER_GRANDPA;
            initDialog();
        });
        camera2.setOnClickListener(view -> {
            choseIV = image2;
            userType = AppConstant.USER_GRANDMA;
            initDialog();
        });
        camera3.setOnClickListener(view -> {
            choseIV = image3;
            userType = AppConstant.USER_FATHER;
            initDialog();
        });
        camera4.setOnClickListener(view -> {
            choseIV = image4;
            userType = AppConstant.USER_MATHER;
            initDialog();
        });

        headerView.setLeftClickListener(view -> finish());
    }


    @Override
    protected void getData() {
        String grandpaHeader = SpUtils.getValue(AppConstant.USER_GRANDPA+ SpConstant.USER_PHOTO, "");
        String grandmaHeader = SpUtils.getValue(AppConstant.USER_GRANDMA+ SpConstant.USER_PHOTO, "");
        String fatherHeader = SpUtils.getValue(AppConstant.USER_FATHER+ SpConstant.USER_PHOTO, "");
        String matherHeader = SpUtils.getValue(AppConstant.USER_MATHER+ SpConstant.USER_PHOTO, "");
        if(!TextUtils.isEmpty(grandpaHeader)) {
            Picasso.with(this).load(Uri.parse(grandpaHeader)).resizeDimen(R.dimen.img_role_width, R.dimen.img_role_height).into(image1);
        }
        if(!TextUtils.isEmpty(grandmaHeader)) {
            Picasso.with(this).load(Uri.parse(grandmaHeader)).resizeDimen(R.dimen.img_role_width, R.dimen.img_role_height).into(image2);
        }
        if(!TextUtils.isEmpty(fatherHeader)) {
            Picasso.with(this).load(Uri.parse(fatherHeader)).resizeDimen(R.dimen.img_role_width, R.dimen.img_role_height).into(image3);
        }
        if(!TextUtils.isEmpty(matherHeader)) {
            Picasso.with(this).load(Uri.parse(matherHeader)).resizeDimen(R.dimen.img_role_width, R.dimen.img_role_height).into(image4);
        }
    }

    @Override
    public void permissionSuccess(int requestCode) {
//        PhotoUtils.getInstance().doTakePhoto(this, tempFile);
//        CameraActivity.newActivity(this);
        CameraActivity.newActivity(this, tempFile);
    }

    @Override
    public void permissionFail(int requestCode) {
        super.permissionFail(requestCode);
    }

    private void initDialog() {
        if (null == dialog) {
            dialog = new GetPicDialog(RoleChoseActivity.this);
            dialog.setOnCameraClick(view -> {
                tempFile = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + System.currentTimeMillis()+".jpg";
                Log.e(TAG, String.valueOf(tempFile));
                requestPermission(needContactsPermissions, AppConstant.PERMISSION_CAMERA);
            });
            dialog.setOnPicClick(view -> {
                tempFile = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + System.currentTimeMillis()+".jpg";
                PhotoUtils.getInstance().selectPicture(this);
            });
        }
        dialog.show();
    }

    public static void newActivity(Context context){
        context.startActivity(new Intent(context, RoleChoseActivity.class));
    }

    private Long firstBackTime = 0l;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (System.currentTimeMillis() - firstBackTime > AppConstant.INTERVAL_BACK_PRESS*1000) {
//            Toast.makeText(this, R.string.toast_exit, Toast.LENGTH_SHORT).show();
//            firstBackTime = System.currentTimeMillis();
//        } else {
//            finish();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PhotoUtils.RESULT_IMAGE_CAPTURE_CUSTOM:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    SpUtils.saveString(userType+ SpConstant.USER_PHOTO, Uri.fromFile(new File(tempFile)).toString());
                } else {
                    SpUtils.saveString(userType+ SpConstant.USER_PHOTO, FileProvider.getUriForFile(this, getApplication().getPackageName() + ".FileProvider", new File(tempFile)).toString());
                }
                Picasso.with(this).load(Uri.fromFile(new File(tempFile))).resizeDimen(R.dimen.img_role_width, R.dimen.img_role_height).into(choseIV);
                break;
            case PhotoUtils.RESULT_IMAGE_CAPTURE://拍照
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    SpUtils.saveString(userType+ SpConstant.USER_PHOTO, Uri.fromFile(new File(tempFile)).toString());
                } else {
                    SpUtils.saveString(userType+ SpConstant.USER_PHOTO, FileProvider.getUriForFile(this, getApplication().getPackageName() + ".FileProvider", new File(tempFile)).toString());
                }
                Picasso.with(this).load(Uri.fromFile(new File(tempFile))).resizeDimen(R.dimen.img_role_width, R.dimen.img_role_height).into(choseIV);
                break;
            case PhotoUtils.RESULT_IMAGE_GALLERY_ABOVE://相册选择
                SpUtils.saveString(userType+ SpConstant.USER_PHOTO, data.getData().toString());
                Picasso.with(this).load(data.getData()).resizeDimen(R.dimen.img_role_width, R.dimen.img_role_height).into(choseIV);
//                PhotoUtils.saveImageFile(choseIV, tempFile);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    SpUtils.saveString(userType+SpConstant.USER_PHOTO, Uri.fromFile(new File(tempFile)).toString());
//                } else {
//                    SpUtils.saveString(userType+SpConstant.USER_PHOTO, FileProvider.getUriForFile(this, getApplication().getPackageName() + ".FileProvider", new File(tempFile)).toString());
//                }
                break;
            default:
                break;
        }
    }
}
