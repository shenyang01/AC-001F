package com.zxcn.imai.smart.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;


import com.zxcn.imai.smart.base.SpConstant;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by WZG on 2016/7/16.
 */
public class ProgressSubscriber<T> extends Subscriber<T> {
    //    回调接口
    private HttpOnNextListener mSubscriberOnNextListener;

    private Activity context;
    //    是否能取消请求
    public boolean cancel = true;
    //    加载框可自己定义
    private ProgressDialog pd;
    private HttpErrorListener mErrorListener ;


    public ProgressSubscriber(Activity context , HttpOnNextListener mSubscriberOnNextListener, HttpErrorListener mErrorListener, boolean cancel) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        this.cancel = cancel;
        this.mErrorListener = mErrorListener;
        initProgressDialog();
    }

    public ProgressSubscriber(Activity context, HttpOnNextListener mSubscriberOnNextListener) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        initProgressDialog();
    }

    public ProgressSubscriber(Activity context , HttpOnNextListener mSubscriberOnNextListener, boolean cancel) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = (context);
        this.cancel = cancel;
        initProgressDialog();
    }

    public ProgressSubscriber( HttpOnNextListener mSubscriberOnNextListener) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
    }




    /**
     * 初始化加载框
     */
    private void initProgressDialog() {
     //   Context context = mActivity.get();
        if (pd == null && context != null&& cancel) {
            pd = new ProgressDialog(context);
            pd.setCancelable(cancel);
            if (cancel) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        onCancelProgress();
                    }
                });
            }
        }
    }


    /**
     * 显示加载框
     */
    private void showProgressDialog() {

        if (pd == null || context == null ) return;
        if (!pd.isShowing()) {
          //  pd.show();

        }
        if(cancel){
            //DialogUtil.showProgressDialog("加载中...",context);
        }
    }


    /**
     * 隐藏
     */
    private void dismissProgressDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
      //  DialogUtil.cancleProgressDialog();
    }


    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        showProgressDialog();
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (context == null)
            return;

//           if (e.toString().contains("401")){
//               ToastUtil.showToast("登陆超时重新登陆");
//               Intent intent = new Intent(context, LoginActivity.class);
//               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//               context.startActivity(intent);
//           }else {
               if (e instanceof SocketTimeoutException) {
                   if (mErrorListener != null){
                       mErrorListener.onError(e);
                   }

               } else if (e instanceof ConnectException) {
                   if (mErrorListener != null){
                       mErrorListener.onError(e);
                   }

               } else {
                   //Toast.makeText(context, "错误" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                SnackBarUtils.show(context,e.toString());
                   Log.e(SpConstant.API, "---error--(数据错误)---" + e.toString());
               }
//
//           }

        dismissProgressDialog();
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}