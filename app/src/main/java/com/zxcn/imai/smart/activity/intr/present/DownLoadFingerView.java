package com.zxcn.imai.smart.activity.intr.present;


import com.zxcn.imai.smart.http.result.ResUploadInfoBean;

import java.util.List;

/**
 * Created by xtich on 2017/10/31.
 */

public interface DownLoadFingerView {
    void getSucess(List<ResUploadInfoBean> list);
    void  error(String str);
}
