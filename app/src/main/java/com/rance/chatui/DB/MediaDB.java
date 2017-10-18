package com.rance.chatui.DB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rance.chatui.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 罗国都 on 2017/3/11.
 */

public class MediaDB {
    private static final String TAG = "MediaDB";
    private static SQLiteDatabase db;

    static {
        //db = MediaMainActivity.db;
    }


    /**
     * 插入数据
     * 在插入数据之后 , 先查询数据是否存在
     * @param url
     * @param media_plate_type
     */

    public static boolean mediaInsertData(String media_plate_type,String url,String media_type,String media_state,String media_plate_uid){
        boolean b = false ;

        String str = "insert into t_media values(null,'"+media_plate_type+"','"+url+"','"+media_plate_uid+"',null,'"+media_state+"','"+media_type+"')";

        String strQuery = "select count(*) from  t_media where " +
                "media_plate_type='"+media_plate_type
                +"' and media_url ='"+url
                +"' and media_plate_uid='"+media_plate_uid
                +"' and media_type='"+media_type
                +"'" ;

        db.beginTransaction();
        try{
            Cursor cursor =db.query(true,
                    "t_media" ,
                    new String[]{"media_plate_type"} ,
                    "media_plate_type=? and media_url=? and media_type=? and media_plate_uid=? ",
                    new String[]{media_plate_type+"",url+"",media_type+"",media_plate_uid+""},
                    null,null,null,null);
            if (!cursor.moveToNext()) {
                db.execSQL(str);
                b = true ;
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            //Toast.makeText(MediaMainActivity.this,"插入数据库失败！",Toast.LENGTH_SHORT).show();
            Log.i(TAG,"插入数据库失败！");
            try {
                throw(e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }finally {
            db.endTransaction();
        }

        return b ;
    }

    public static boolean fileIsUpload(String fileName){

        String updateSQL = "update t_media set media_state = '1' where media_url='"+fileName+"'";
        db.beginTransaction();
        try{

            db.execSQL(updateSQL);
            db.setTransactionSuccessful();

            Logger.i("fileIsUpload上传状态更新成功");
            return true;
        }catch (Exception e){
            return false;
        }finally {
            db.endTransaction();
        }

    }


    /**
     * 根据类型查找数据
     * @param type
     */
    public static List mediaSelectDb(String media_plate_type, String type){
        String str="select * from t_media where media_plate_type='"+media_plate_type+"'and media_type='"+type+"'";
        List list = null;
        db.beginTransaction();
        try{
            Cursor cursor=db.rawQuery(str, null);
            list=new ArrayList();
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
                int _id=cursor.getInt(cursor.getColumnIndex("_id"));
                String media_url=cursor.getString(cursor.getColumnIndex("media_url"));
                list.add(media_url);
                Log.i(_id+"db","media_url-->"+media_url);
            }
            cursor.close();
            db.setTransactionSuccessful();
        }catch (Exception e){
            try {
                throw(e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }finally {
            db.endTransaction();
        }

        return list;
    }

    /**
     * XXXXXXXXXXXXX根据同一板块中不同 item 显示的数据
     *
     *  通过记录 的ID和资源 的 类型来 查询数据
     * @param media_state  是否上传 0未上传  1上传
     * @param type 资源类型
     * @param media_plate_uid 记录的ID
     * @retur 从数据库中查询的资源记录
     */
    public static List<String> mediaSelectPlateDb(String media_state,String type,String media_plate_uid){
        String strSQL="select * from t_media where media_state='"+media_state+"'and media_type='"+type+"' and media_plate_uid='"+media_plate_uid+"'";

        List<String> list = null;//用来保存从数据库中读取的资源路径
        db.beginTransaction();
        try{
            Cursor cursor=db.rawQuery(strSQL, null);
            list=new ArrayList();
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())//
            {
                int _id=cursor.getInt(cursor.getColumnIndex("_id"));
                String media_url=cursor.getString(cursor.getColumnIndex("media_url"));//查询
                list.add(media_url);//添加
                Log.i("mediaSelectPlateDb","id号码"+_id+"根据同一板块中不同 item 显示的数据-->"+media_url);
            }
            cursor.close();
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.i(TAG,"查找数据失败！");
            try {
                throw(e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }finally {
            db.endTransaction();

        }

        return list;
    }



    /**
     * 删除t_media中的所有数据
     */
    public static void mediaDeleteALLData(){
        String sql="delete from t_media;";
        db.execSQL(sql);
    }



    /**
     * 建表
     */
    public static void mediaCreateTables(){
        db.execSQL("create table if not exists t_media(_id integer " +
                "primary key autoincrement," +
                "media_plate_type varchar(50) ," +//暂时保留
                "media_url varchar(50)," +//多媒体的文件路径
                "media_plate_uid varchar(50)," +//多媒体uid的  外键  保存JID,UID
                "media_plate_isSave varchar(50)," +//板块中是否保存（暂时保留）
                "media_state varchar(50)," +//是否上传 0未上传    1已上传
                "media_type varchar(50))");//媒体的类型photo,sound,video

        Log.i("TAG", "" + "media建表了");
    }


    /**
     * 更加用户的jid查询所有的多媒体
     * @param media_plate_uid
     * @return
     */
    public static List<String> mediaByJid(String media_plate_uid){
        String strSQL="select * from t_media where media_plate_uid='"+media_plate_uid+"'";

        List<String> list = null;//用来保存从数据库中读取的资源路径
        db.beginTransaction();
        try{
            Cursor cursor=db.rawQuery(strSQL, null);
            list=new ArrayList();
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())//
            {
                int _id=cursor.getInt(cursor.getColumnIndex("_id"));
                String media_url=cursor.getString(cursor.getColumnIndex("media_url"));//查询
                list.add(media_url);//添加
                Log.i("mediaByJid","id号码"+_id+"根据同一板块中不同 item 显示的数据-->"+media_url);
            }
            cursor.close();
            db.setTransactionSuccessful();
        }catch (Exception e){
            //Toast.makeText(MediaMainActivity.this,"查找数据失败！",Toast.LENGTH_SHORT).show();

            Log.i(TAG,"查找数据失败！");
            try {
                throw(e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }finally {
            db.endTransaction();

        }

        return list;
    }

}
