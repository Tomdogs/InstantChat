package com.rance.chatui.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rance.chatui.R;
import com.rance.chatui.base.BaseFragment;
import com.rance.chatui.enity.MessageInfo;
import com.rance.chatui.util.Constants;
import com.rance.chatui.util.DateUtil;
import com.rance.chatui.util.Logger;
import com.rance.chatui.util.XmppTool;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rance.chatui.base.MyApplication.screenHeight;
import static com.rance.chatui.base.MyApplication.screenWidth;

import static com.rance.chatui.ui.activity.MainActivity.toUser;
import static com.rance.chatui.ui.mainview.MainFragmentActivity.database;
import static com.rance.chatui.ui.mainview.MainFragmentActivity.mediaPath;


/**
 * 作者：Rance on 2016/12/13 16:01
 * 邮箱：rance935@163.com
 */
public class ChatFunctionFragment extends BaseFragment {
    private View rootView;
    private static final int VIDEO_CAPTURE = 0;
    private static final int CROP_FILE = 1;
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE3 = 8;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE4 = 9;
    private File output;
    private Uri imageUri;
    private boolean checkfPermission;
    private String videoPathUrl,videoName,videoPhotoThumbnail;
    private  int predateTime = (int) System.currentTimeMillis();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        checkfPermission = checkSelfPermission(getActivity());

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_chat_function, container, false);
            ButterKnife.bind(this, rootView);
        }
        return rootView;
    }

    @OnClick({R.id.chat_function_photo, R.id.chat_function_photograph,R.id.chat_function_file,R.id.chat_function_video})
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.chat_function_photograph:

                if (!checkfPermission) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    takePhoto();
                }
                break;
            case R.id.chat_function_photo:

                if (!checkfPermission) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE2);

                } else {
                    choosePhoto();
                }
                break;

            case   R.id.chat_function_file:

                if (!checkfPermission) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE3);

                } else {
                    /*Toast.makeText(getActivity(),"暂时还不支持该功能",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity(),FileTransferActivity.class);
                    intent.putExtra("userJID",toUser);
                    startActivity(intent);*/
                    chooseFile();
                }
                break;

            case R.id.chat_function_video:

                if (!checkfPermission) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE4);
                } else {
                    takeVedio();
                }

              /*  Intent intent=new Intent(getActivity(), MediaMainActivity.class);
                startActivity(intent);
*/
                break;
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        /**
         * 最后一个参数是文件夹的名称，可以随便起
         */
        File file = new File(mediaPath+"/photos/");
        if (!file.exists()) {
            file.mkdirs();
        }
        /**
         * 这里将时间作为不同照片的名称
         */
        output = new File(file, DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg");

        /**
         * 如果该文件夹已经存在，则删除它，否则创建一个
         */
        try {
            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 隐式打开拍照的Activity，并且传入CROP_PHOTO常量作为拍照结束后回调的标志
         */
        imageUri = Uri.fromFile(output);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CROP_PHOTO);

    }

    /**
     * 录像
     */
    private void takeVedio(){

        videoName = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".mp4";
        videoPathUrl = mediaPath+"/video/";
        File videoFile = new File(videoPathUrl);
        if (!videoFile.exists()) {
            videoFile.mkdirs();
        }

        File videoOut = new File(videoFile,videoName);

        try {
            if (videoOut.exists()) {
                videoOut.delete();
            }else {
                videoOut.createNewFile();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        Uri uri=Uri.fromFile(videoOut);
        Intent intentVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //此值在最低质量最小文件尺寸时是0，在最高质量最大文件尺寸时是１.
        intentVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intentVideo.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //intentVideo.putExtra(MediaStore.EXTRA_OUTPUT, 45000);

        startActivityForResult(intentVideo, VIDEO_CAPTURE);

    }

    /**
     * 从相册选取图片
     */
    private void choosePhoto() {
        /**
         * 打开选择图片的界面
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

    }

    /**
     * 选择文件
     */
    private void chooseFile(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置文件类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,CROP_FILE);

    }



    public void onActivityResult(int req, int res, Intent data) {

        switch (req) {

            case CROP_PHOTO:
                if (res == Activity.RESULT_OK) {

                    try {

                        MessageInfo messageInfo = new MessageInfo();
                        messageInfo.setImageUrl(imageUri.getPath());
                        messageInfo.setTime(DateUtil.getTimeDiffDesc(new Date(System.currentTimeMillis())));

                        EventBus.getDefault().post(messageInfo);

                        //添加图片发送
                        Log.i("照相时图片路径：",""+imageUri.getPath());
                        XmppTool.fileTransfer(toUser,imageUri.getPath());
                        System.out.print("发送成功。。。。。。。");

                        //存储在数据中
                        ContentValues values = new ContentValues();
                        values.put("jid",toUser);
                        values.put("time",System.currentTimeMillis());
                        values.put("filepath",imageUri.getPath());
                        values.put("send_accept_state",0);
                        values.put("type", Constants.CHAT_ITEM_TYPE_RIGHT);
                        database.insert("chat",null,values);
                    } catch (Exception e) {
                    }
                } else {
                    Log.i(Constants.TAG, "失败");
                }

                break;

            case REQUEST_CODE_PICK_IMAGE:
                if (res == Activity.RESULT_OK) {
                    try {

                        Uri uri = data.getData();
                        String photoAbsolutPath = getAbsoluteFilePath(getActivity(),uri);
                        if(photoAbsolutPath.equals("")){
                            Toast.makeText(getActivity(),"选取文件出错！",Toast.LENGTH_LONG).show();
                        }else {
                            MessageInfo messageInfo = new MessageInfo();
                            messageInfo.setTime(DateUtil.getTimeDiffDesc(new Date(System.currentTimeMillis())));
                            Logger.i("从相册中选取图片的路径："+uri.getPath());
                            messageInfo.setImageUrl(photoAbsolutPath);
                            Logger.i("从相册中选取图片的路径2："+photoAbsolutPath);
                            EventBus.getDefault().post(messageInfo);

                            //发送图片
                            XmppTool.fileTransfer(toUser,photoAbsolutPath);
                            System.out.print("从相册中选取图片的发送成功。。。。。。。");


                            //存储在数据中
                            ContentValues values = new ContentValues();
                            values.put("jid",toUser);
                            values.put("time",System.currentTimeMillis());
                            values.put("filepath",photoAbsolutPath);
                            values.put("send_accept_state",0);
                            values.put("type", Constants.CHAT_ITEM_TYPE_RIGHT);
                            database.insert("chat",null,values);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(Constants.TAG, e.getMessage());
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }

                break;

            case CROP_FILE:
                if (res == Activity.RESULT_OK) {

                    try {

                        Uri uri = data.getData();
                        String fileAbsolutPath = getAbsoluteFilePath(getActivity(),uri);
                        if(fileAbsolutPath.equals("")){
                            Toast.makeText(getActivity(),"选取文件出错！",Toast.LENGTH_LONG).show();
                        }else {
                            MessageInfo messageInfo = new MessageInfo();
                            messageInfo.setTime(DateUtil.getTimeDiffDesc(new Date(System.currentTimeMillis())));
                            Logger.i("从文件系统中选取文件的路径："+uri.getPath());
                            messageInfo.setFilepath(fileAbsolutPath);
                            Logger.i("从文件系统中选取文件的路径2："+fileAbsolutPath);
                            EventBus.getDefault().post(messageInfo);

                            //发送文件
                            XmppTool.fileTransfer(toUser,fileAbsolutPath);
                            System.out.print("从文件系统中选取文件发送成功。。。。。。。");

                            //存储在数据中
                            ContentValues values = new ContentValues();
                            values.put("jid",toUser);
                            values.put("time",System.currentTimeMillis());
                            values.put("filepath",fileAbsolutPath);
                            values.put("send_accept_state",0);
                            values.put("type", Constants.CHAT_ITEM_TYPE_RIGHT);
                            database.insert("chat",null,values);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(Constants.TAG, e.getMessage());
                    }
                }
                break;
            case VIDEO_CAPTURE:
                if (res == Activity.RESULT_OK) {

                    String videoPath = videoPathUrl + videoName;

                    Logger.i("视屏的路径：："+videoPath);
                    Bitmap bitmap=getVideoThumbnail(videoPath, screenWidth / 6, screenHeight / 6, MediaStore.Video.Thumbnails.MICRO_KIND);
                    saveVideoPhoto(bitmap);

                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setTime(DateUtil.getTimeDiffDesc(new Date(System.currentTimeMillis())));
                    Logger.i("视频缩略图的路径："+videoPathUrl+videoPhotoThumbnail);
                    messageInfo.setImageUrl(videoPathUrl+videoPhotoThumbnail);
                    EventBus.getDefault().post(messageInfo);

                    //发送文件
                    XmppTool.fileTransfer(toUser,videoPath);
                    System.out.print("从视频发送成功。。。。。。。");


                    //存储在数据中
                    ContentValues values = new ContentValues();
                    values.put("jid",toUser);
                    values.put("time",System.currentTimeMillis());
                    values.put("filepath",videoPath);
                    values.put("send_accept_state",0);
                    values.put("type", Constants.CHAT_ITEM_TYPE_RIGHT);
                    database.insert("chat",null,values);


                }
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                toastShow("请同意系统权限后继续");
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                toastShow("请同意系统权限后继续");
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseFile();
            } else {
                toastShow("请同意系统权限后继续");
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE4) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeVedio();
            } else {
                toastShow("请同意系统权限后继续");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 存储视频缩略图
     *  图片数据
     */
    public void saveVideoPhoto(Bitmap bitmap) {


        FileOutputStream fos = null;
        videoPhotoThumbnail=videoName.substring(0, videoName.length() - 3);
        videoPhotoThumbnail+="jpg";
        try {
            fos = new FileOutputStream(videoPathUrl+videoPhotoThumbnail);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                    int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
    /**
     * 从URI获取本地路径
     */
    public static  String getAbsoluteFilePath(Activity activity,Uri contentUri) {

        //如果是对媒体文件，在android开机的时候回去扫描，然后把路径添加到数据库中。
        //由打印的contentUri可以看到：2种结构。正常的是：content://那么这种就要去数据库读取path。
        //另外一种是Uri是 file:///那么这种是 Uri.fromFile(File file);得到的
        System.out.println("getAbsoluteImagePath:" + contentUri);

        String urlpath;
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(activity, contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        String isFile = contentUri.toString().split(":")[0];

        //如果是文件。转化为对应的文件
        if (isFile.compareTo("file")==0) {

            urlpath = contentUri.toString().replace("file://","");

            System.out.println("如果是文件fa1：" + Uri.decode(urlpath));

            return Uri.decode(urlpath);//解决中文乱码问题

        } else {
            try {
                int column_index = cursor.getColumnIndex(
                        MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                urlpath = cursor.getString(column_index);
                //如果是正常的查询到数据库。然后返回结构
                System.out.println("如果是文件fa2：" + urlpath);
                //数据可能查不到返回为空
                if(urlpath==null){
                    return "";
                }else {
                    return  Uri.decode(urlpath);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }

            }
            urlpath = contentUri.getPath();
            System.out.println("如果是文件fa3：" + urlpath);
            return "";
        }


    }

    public static boolean checkSelfPermission(Context context){

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED ){

            return true;
        }else {
            return false;
        }

    }


}
