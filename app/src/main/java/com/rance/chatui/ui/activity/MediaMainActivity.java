package com.rance.chatui.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rance.chatui.R;
import com.rance.chatui.util.Logger;
import com.rance.chatui.util.MediaDB;
import com.rance.chatui.util.XmppTool;
import com.readystatesoftware.viewbadger.BadgeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.rance.chatui.util.MediaDB.fileIsUpload;
import static com.rance.chatui.util.MediaDB.mediaByJid;
import static com.rance.chatui.util.XmppTool.outgoingFileTransfer;

//import static com.admin.videotest.MediaDB.mediaByJid;


public class MediaMainActivity extends Activity {
    private Context mContext;
    private LayoutInflater  mLayoutInflater;
    public LinearLayout vedioLinearLayout;
    BadgeView badge;

    public String photoPathUrl;
    public String photoThumbnailPathUrl;

    //获取context实例
    //Context con= App.getInstance();
    /**
     * getApplication()
     * getApplicationContext()
     * getBaseContext()
     */

    String mediaPath;

    public int screenWidth;
    public int screenHeight;

    /*BitmapFactory.Options options = new BitmapFactory.Options();*/
    View itemView;
    ImageView imageView;

    //************************************录像********************************************

    public String videoPathUrl;
    File vedioFile;
    public String /*vedioPath,*/videoName;
    public static final int VIDEO_CAPTURE = 0;
    File videoOut;
    //################################数据库#######################################################
    public static SQLiteDatabase db;
    //################################数据库#######################################################


    private  List<String> vedioPathse = new ArrayList<>();//存放录像的路径
    private List<String> vedioTempPathse =null;//Temp存放录像的路径


    private String toUserJID;
    //private String userJID = LoginActivity.connection.getUser();
    private static final String mediaVideo = "video";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_media);

        mediaPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/"+this.getPackageName();

        videoPathUrl = mediaPath+"/Video/";
        vedioFile=new File(videoPathUrl);

        // 获取屏幕宽高
        screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
        screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）

        if(!vedioFile.exists()){
            vedioFile.mkdirs();
        }

        //创建或者打开数据库(使用绝对路径)
        //String DBPath=this.getApplicationContext().getFilesDir().toString()+"/Media.db" ;
        String DBPath=this.mediaPath+"/Media.db" ;

        db = SQLiteDatabase.openOrCreateDatabase(DBPath, null);
        MediaDB.mediaCreateTables();


        Intent intentJID = getIntent();
        toUserJID = intentJID.getStringExtra("userJID");
        System.out.println("多媒体的toUserJID:" + toUserJID);

        initVideoView();
        this.vedioTempPathse=new ArrayList<>();

        /**
         * 初始化并且显示录像的数据
         */
        vedioLinearLayout.removeAllViews();
        this.vedioPathse=getVedioPictureListData("0",mediaVideo,toUserJID);
        initVideoData(vedioPathse,"0",mediaVideo);

        //saveToDB();

        List<String> list = mediaByJid(toUserJID);
        for(String s : list){
            System.out.println(toUserJID+"的所示的多媒体的数据为："+s);
            Logger.i(toUserJID+"的所示的多媒体的数据为："+s);
        }

    }
//    public void sendAllContent(View view){
//        Intent intent=new Intent(this,videoactivity.class);
//        startActivity(intent);
//
//    }

    /**
     * desc: 拍照之后 ,不在保存在数据库中   保存在内存中
     * 照完相和录完像后的回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super. onActivityResult(requestCode, resultCode, data);

        //系统录像
        if (resultCode == Activity.RESULT_OK && requestCode == VIDEO_CAPTURE) {

            Bitmap bitmap=getVideoThumbnail(videoPathUrl + videoName, screenWidth / 6, screenHeight / 6, MediaStore.Video.Thumbnails.MICRO_KIND);
            saveVideoPhoto(bitmap);

            vedioLinearLayout.removeAllViews();
            //将视频的文件名保存list缓存中
            this.vedioPathse.add(videoPathUrl + videoName);
//          this.vedioPathse.add(videoPathUrl + videoName); this.vedioPathse.add(videoPathUrl + videoName);
            initVideoData(this.vedioPathse,"",mediaVideo);

        }
    }

    public void buttonVedio(View view){
        Intent intentVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //此值在最低质量最小文件尺寸时是0，在最高质量最大文件尺寸时是１.
        intentVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        videoName = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".mp4";
        videoOut = new File(videoPathUrl+videoName);
        Uri uri=Uri.fromFile(videoOut);
        intentVideo.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intentVideo, VIDEO_CAPTURE);
    }

//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$录像$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

    /***
     * 遍历video数据库中的所有的视频
     */
    public List<String>  getVedioPictureListData(String media_state,String type,String media_plate_uid){

        List videoList= MediaDB.mediaSelectPlateDb(media_state, type, media_plate_uid);
        Log.i("video：",""+videoList.size());

        List listPictureName =new ArrayList();
        for(int i=0;i<videoList.size();i++){
            Log.i("video：", "" + videoList.get(i).toString());
            listPictureName.add(videoPathUrl +videoList.get(i));
            Log.i("video的图片有：", "" + videoPathUrl + videoList.get(i));
        }
        return listPictureName;
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
     * 存储视频缩略图
     *  图片数据
     */
    public void saveVideoPhoto(Bitmap bitmap) {


        FileOutputStream fos = null;
        String videoPhotoThumbnail=videoName.substring(0, videoName.length() - 3);
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
     * 视频的视图加载
     */
    public void initVideoView(){
        mContext=this;
        vedioLinearLayout=(LinearLayout) findViewById(R.id.vedioLinearLayout);
        mLayoutInflater=LayoutInflater.from(mContext);
    }


    /**
     * list缓存，视频列表的数据填充
     * @param temp
     * @param media_plate_type
     * @param type
     */
    public void initVideoData(List<String> temp,String media_plate_type,String type){

        List list=temp;

        Log.i("视频的数量",""+list.size());

        for (int i = 0; i <list.size(); i++) {
            Log.i("视频的数量", "" + list.get(i).toString());
            String vedpicName = list.get(i).toString().replace("mp4","jpg");
            Log.i("视频的图片", "" +vedpicName);
            itemView=mLayoutInflater.inflate(R.layout.gallery_item, null);
            imageView=(ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(vedpicName));

            badge = new BadgeView(this, imageView);
            badge.setBackgroundResource(R.drawable.cancel2);
            badge.setOnClickListener(new DeleteCatcheVedioOnClickListenter(i, imageView, media_plate_type, type,temp));
            imageView.setOnClickListener(new SelectCatcheVedioOnClickListenter(i, imageView, media_plate_type, type,temp));
            badge.show();
            vedioLinearLayout.addView(itemView);

        }

    }



    /**
     * list缓存，视频删除
     */
    public class DeleteCatcheVedioOnClickListenter implements View.OnClickListener{

        private int q=0;
        private String media_plate_type=null;
        private String type=null;
        private List<String> tmp;
        public DeleteCatcheVedioOnClickListenter(int q,View view,String uid,String type,List<String> tmp){
            this.q=q;
            this.media_plate_type=uid;
            this.type=type;
            this.tmp=tmp;
        }
        @Override
        public void onClick(View view) {
            String fileStringName[]=vedioFile.list();
            List list=tmp;

            String split[]=list.get(q).toString().split("/");
            String vedioName=split[split.length-1];
            //在数据库中删除相应的视频名
            db.execSQL("delete from t_media where media_url='"+vedioName+"'");

            //选中的视频文件绝对路径
            String s= (String) list.get(q);
            // String vedpicName = s.substring(0, s.length() - 3);
            String vedpicName=s.replace("mp4","jpg");
            Log.i("视频图片的删除",""+s);
            File f1=new File(vedpicName);
            File f2=new File(s);

            //删除视图
            vedioLinearLayout.removeAllViews();

            tmp.remove(q) ;

            f1.delete();
            f2.delete();

            initVideoData(tmp, media_plate_type,type);
            Toast.makeText(MediaMainActivity.this,"删除了"+ vedpicName+".mp4"+fileStringName[q],Toast.LENGTH_LONG).show();

        }
    }


    /**
     * list缓存，视频播放
     */
    public class SelectCatcheVedioOnClickListenter implements View.OnClickListener{

        private int q=0;
        private String media_plate_type=null;
        private String type=null;
        private List<String> tmp;

        public SelectCatcheVedioOnClickListenter(int q,View View,String media_plate_type,String type,List<String> tmp){
            this.q=q;
            this.media_plate_type=media_plate_type;
            this.type=type;
            this.tmp=tmp;
        }
        @Override
        public void onClick(View view) {

            List list=tmp;
            String s= (String) list.get(q);

            //String vedpicName = s.substring(0, s.length() - 3);
            String vedpicName=s.replace("mp4","jpg");
            Log.i("视频图片的路径", "" + vedpicName);
            //vedioPath =vedpicName + "mp4";
            Log.i("视频的路径", "" + s);

            Uri uri = Uri.parse("file://"+s);
            Log.i("视频uri的路径",""+uri);

            //调用系统播放器
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/mp4");
            startActivity(intent);
        }
    }

    public void saveToDB(){
        /**
         *保存缓存的多媒体资源
         */

        //录像
        //List vedioList=this.vedioPathse;
        this.vedioPathse.addAll(vedioTempPathse);
        for(int i=0;i<vedioPathse.size();i++){
            String videoSplit[]=vedioPathse.get(i).toString().split("/");
            String videoName=videoSplit[videoSplit.length-1];
            Logger.i("存到数据库中的录像像的名字："+videoName);
            MediaDB.mediaInsertData(null,videoName,mediaVideo,"0",toUserJID);
        }
        this.vedioTempPathse.clear();

    }
    public void sendAllContent(View view){

//        saveToDB();
//
//        //查询当前用户所存取的所有的用户
//        for(String fileName : mediaByJid(toUserJID)){
//
//            String [] splitType = fileName.split("\\.");
//            String fileType  = splitType[splitType.length-1];
//            Logger.i("分割fileType:"+fileType);
//
////            if(fileType.equals("jpg")){
////                //传输照片,并更新数据库
////                if( !XmppTool.fileTransfer(toUserJID,photoPathUrl+fileName) && !fileIsUpload(fileName)){
////                    Toast.makeText(MediaMainActivity.this,"传输出错了！",Toast.LENGTH_SHORT).show();
////                    outgoingFileTransfer.cancel();
////                    return;
////                }
////
////                Logger.i("传输照片的路径："+photoPathUrl+fileName);
////            }else if(fileType.equals("amr")){
////                //传输录音
////                XmppTool.fileTransfer(toUserJID,soundPathUrl+fileName);
////                Logger.i("传输录音的路径："+soundPathUrl+fileName);
////            }else if(fileType.equals("mp4")){
//                //传输视频
//                XmppTool.fileTransfer(toUserJID,videoPathUrl+fileName);
//                Logger.i("传输视频的路径："+videoPathUrl+fileName);
//            }

        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);

        }


    }




