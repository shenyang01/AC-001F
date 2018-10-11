package com.zxcn.imai.smart.http;





import com.zxcn.imai.smart.http.result.ResTokenBean;
import com.zxcn.imai.smart.http.result.ResUploadInfoBean;
import com.zxcn.imai.smart.http.result.ResUserInfo;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * service统一接口数据
 * Created by WZG on 2016/7/16.
 */
public interface HttpService {
    //获取验证码
    @FormUrlEncoded
    @POST("iss/rest/pub/common/getSMSAuthCode")
    Observable<BaseData> getCode(@FieldMap Map<String, String> map);

    //验证验证码
    @FormUrlEncoded
    @POST("iss/rest/pub/common/checkAuthCode")
    Observable<BaseData> checkCode(@FieldMap Map<String, String> map);

   // 测量结果上传
    @FormUrlEncoded
    @POST("iss/rest/pub/imai/saveMeasuringResult")
    Observable<BaseData> uploadData(@FieldMap Map<String, String> map);

    //TOKEN
    @FormUrlEncoded
    @POST("iss/rest/pub/imai/checkDevice")
    Observable<BaseData<ResTokenBean>> getToken(@FieldMap Map<String, String> map);

    //指纹绑定
    @FormUrlEncoded
    @POST("iss/rest/pub/imai/megerFinger")
    Observable<BaseData> fingerBind(@FieldMap Map<String, String> map);

     //会员信息查询
     @FormUrlEncoded
     @POST("iss/rest/pub/imai/eh/queryElderlyInfo")
     Observable<BaseData<ResUserInfo>> queryByVip(@FieldMap Map<String, String> map);

    //会员信息同步
     @FormUrlEncoded
     @POST("iss/rest/pub/imai/queryElderlyInfoList")
     Observable<BaseData<ResUserInfo>> downloadByCardid(@FieldMap Map<String, String> map);


    //指纹数据同步
    @FormUrlEncoded
    @POST("iss/rest/pub/imai/queryUserInfoList")
    Observable<BaseData<ResUploadInfoBean>> syncData(@FieldMap Map<String, String> map);


}
