package com.zxcn.imai.smart.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 专业用于拍摄的工具类 兼容5.0的
 * @author heqinglin
 *
 */
public class PhotoUtils {
	/**
	 *  从照相机跳转标识
	 *  4.4以下(也就是kitkat以下)的版本
	 */
	public final static int RESULT_IMAGE_CAPTURE = 1;
	/**
	 */
	public final static int RESULT_IMAGE_PHOTO = 2;
	/***
	 * 从照相机跳转标识
	 * 4.4以上(也就是kitkat以上)的版本,当然也包括最新出的5.0棒棒糖
	 */
//	public final static int RESULT_IMAGE_CAPTURE_ABOVE = 2;
	/**
	 *  从相册跳转标识
	 *  4.4以下(也就是kitkat以下)的版本
	 */
	public final static int RESULT_IMAGE_GALLERY_LESS = 3;
	/***
	 * 从相册跳转标识
	 * 4.4以上(也就是kitkat以上)的版本,当然也包括最新出的5.0棒棒糖
	 */
	public final static int RESULT_IMAGE_GALLERY_ABOVE = 4;
	
	//从图片剪裁跳转标识(裁剪图片成功后返回)
	public final static int RESULT_IMAGE_PHOTO_ZOOM = 5;

	//自定义相机
	public final static int RESULT_IMAGE_CAPTURE_CUSTOM = 6;
	
	private static PhotoUtils utils;

	public static PhotoUtils getInstance() {
		if (utils == null) {
			utils = new PhotoUtils();
		}
		return utils;
	}
	
	
	/***
	 * 选择一张图片
	 * 图片类型，这里是image/*，当然也可以设置限制
	 * 如：image/jpeg等
	 * @param context
	 */
	@SuppressLint("InlinedApi")
	public void selectPicture(Activity context) {
		if (Build.VERSION.SDK_INT < 19) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			//由于startActivityForResult()的第二个参数"requestCode"为常量，
			context.startActivityForResult(intent, RESULT_IMAGE_GALLERY_LESS);
		} else {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
			context.startActivityForResult(intent, RESULT_IMAGE_GALLERY_ABOVE);
		}
	}
	
	
	/***
	 * 裁剪图片
	 * @param context Activity
	 * @param uri 图片的Uri
	 */
	public void cropPicture(Activity context, Uri uri ,String targetUrl) {
		Intent innerIntent = new Intent("com.android.camera.action.CROP");
		innerIntent.setDataAndType(uri, "image/*");
		innerIntent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
		innerIntent.putExtra("aspectX", 1); // 放大缩小比例的X
		innerIntent.putExtra("aspectY", 1);// 放大缩小比例的X   这里的比例为：   1:1
		innerIntent.putExtra("outputX", 320);  //这个是限制输出图片大小
		innerIntent.putExtra("outputY", 320); 
		innerIntent.putExtra("return-data", false);  //这里设置为false data才不会又返回，就不会出现bandle过大的情况
		innerIntent.putExtra("scale", true);
		innerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		innerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(targetUrl)));//设置截取完成后保存图片的地址
		context.startActivityForResult(innerIntent, RESULT_IMAGE_PHOTO_ZOOM);
	}
	
	 /**
     * 打开照相机
     */
	public void doTakePhoto(Activity context,String picUrl) {
//		if(getOutputMediaFile(context)==null){  //getOutputMediaFile() == null表示不存在sd卡
//			Toast.makeText(context, "获取照片存储地址失败", Toast.LENGTH_LONG).show();
//			return ;
//		}
		 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (Build.VERSION.SDK_INT < 24) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(picUrl)));
		} else {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, context.getApplication().getPackageName() + ".FileProvider", new File(picUrl)));
		}
         intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); //相片质量，0,1
         context.startActivityForResult(intent, RESULT_IMAGE_CAPTURE);  
	}

	public void doTakePhoto(Activity context, File file) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file));
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); //相片质量，0,1
		context.startActivityForResult(intent, RESULT_IMAGE_CAPTURE);
	}

	/**
	 * 下面几个方法来自于stackoverflow
	 * 看不懂的地方挨个百度。
	 * -----------------------割-------------------------
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @author paulburke
	 */
	@SuppressLint("NewApi")
	public String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}
	
	
	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}
	
	/***********************  my  ***************************/
	/**
	 * 获取图片存放的地址
	 * @return
	 */
	 public static String getOutputMediaFile(Activity activity){
		String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? activity.getExternalCacheDir()
				.getPath() : activity.getCacheDir().getPath();
				cachePath += cachePath.endsWith(File.separator) ? "" : File.separator;
				Log.i("Utils","url:"+cachePath);
		return cachePath;
	}
	
	/**
	 * 获取图片的名字
	 * @return
	 */
	 public static String getPhotoFileName() {  
        Date date = new Date(System.currentTimeMillis());  
        //格式化拍摄的照片
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");  
        return dateFormat.format(date) + ".jpg";  
    }

    public static String getFilePath(Context context, Uri uri) {
		if ( null == uri ) return null;
		final String scheme = uri.getScheme();
		String data = null;
		if ( scheme == null )
			data = uri.getPath();
		else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
			data = uri.getPath();
		} else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
			Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
			if ( null != cursor ) {
				if ( cursor.moveToFirst() ) {
					int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
					if ( index > -1 ) {
						data = cursor.getString( index );
					}
				}
				cursor.close();
			}
		}
		return data;
	}

	public static boolean saveImageFile(ImageView iv, String path){
		BitmapDrawable d = (BitmapDrawable) iv.getDrawable();
		Bitmap img = d.getBitmap();
		try{
			OutputStream os = new FileOutputStream(path);
			img.compress(Bitmap.CompressFormat.PNG, 100, os);
			os.close();
		}catch(Exception e){
			Log.e("TAG", "", e);
		}
		return false;
	}

	public static boolean saveBitmapAsFile(Bitmap bitmap, String path) {
		try{
			OutputStream os = new FileOutputStream(path);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			os.flush();
			os.close();
			return true;
		}catch(Exception e){
			Log.e("TAG", "", e);
		}
		return false;
	}
}
