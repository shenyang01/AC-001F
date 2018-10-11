package com.zxcn.imai.smart.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.litesuits.orm.BuildConfig;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.SQLiteHelper;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.db.module.SmartBean;
import com.zxcn.imai.smart.db.module.UserInfo;
import com.zxcn.imai.smart.util.SpUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZXCN1 on 2017/8/4.
 */

public class DbUtils implements SQLiteHelper.OnUpdateListener{
//    private static final String DB_NAME_PATH = "zxcn";
    private static final String DB_NAME = "zxcg.db";
    @Override
    public void onUpdate(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private static LiteOrm mLiteOrm;
    private static DbUtils instance;

    public static void init(Context context) {
        if (null == instance) {
            new DbUtils(context);
        }
    }




    private DbUtils(Context context) {
        if (null == instance) {
            synchronized (DbUtils.class) {
                if (null == instance) {
                    DataBaseConfig config = new DataBaseConfig(context);
                    config.dbName = DB_NAME;
                    config.dbVersion = 1;
                    config.onUpdateListener = this;
                    config.debugged = BuildConfig.DEBUG;
                    //可替换为 newCascadeInstance支持级联操作
                    mLiteOrm = LiteOrm.newSingleInstance(config);

                    instance = this;
                }
            }
        }
    }

    public static long save(Object o) {
        if (o == null) {
            return -1;
        }

        return mLiteOrm.save(o);
    }

    public static long insert(Object o){
        if (null == o) {
            return -1;
        }
        return mLiteOrm.insert(o);
    }

    public static <T> int save(List<T> collection) {
        if (null == collection || collection.isEmpty()) {
            return -1;
        }
        return mLiteOrm.save(collection);
    }

    /**
     * 仅在存在时候更新
     * @param t
     * @param <T>
     * @return
     */
    public static <T>int update(T t) {
        return mLiteOrm.update(t, ConflictAlgorithm.Replace);
    }


    public static UserInfo getUserInfo() {
        QueryBuilder<UserInfo> queryBuilder = new QueryBuilder<>(UserInfo.class);
        queryBuilder.where("user_type = ? ",
                (Object[]) new String[] { SpUtils.getValue(SpConstant.USER_TYPE, "")});
        List<UserInfo> result = mLiteOrm.query(queryBuilder);
        if (null == result || result.size() ==0) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public static boolean hasUser() {
        try {
            return mLiteOrm.queryCount(UserInfo.class) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<SmartBean> getUploadData() {
        QueryBuilder<SmartBean> queryBuilder = new QueryBuilder<>(SmartBean.class);
        queryBuilder.where("data_time > ? ", (Object[]) new String[] { SpUtils.getValue(SpConstant.UPLOAD_TIME, "")});
        List<SmartBean> result = mLiteOrm.query(queryBuilder);
        return result;
    }

    /**
     * 查询所有
     *
     * @param cla
     * @return
     */
    public static  <T> List<T> QueryAll(Class<T> cla) {
        return mLiteOrm.query(cla);
    }

    public static ArrayList<UserInfo> queryUserInfo(String field, String condition) throws SQLException {
        QueryBuilder<UserInfo> queryBuilder = new QueryBuilder<>(UserInfo.class);
        queryBuilder.where(field + " = ? ",
                (Object[]) new String[] { condition});
        ArrayList<UserInfo> result = mLiteOrm.query(queryBuilder);
        if (null == result || result.size() ==0) {
            return null;
        } else {
            return result;
        }
    }


    public static void clearAll(){
        mLiteOrm.deleteAll(UserInfo.class);
    }

}
