package com.rance.chatui.ui.activity;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rance.chatui.R;
import com.rance.chatui.util.Logger;
import com.rance.chatui.util.XmppTool;

import org.jivesoftware.smackx.filetransfer.FileTransfer;

import static com.rance.chatui.util.XmppTool.outgoingFileTransfer;

//import comn.example.imforsmack.util.Logger;
//import comn.example.imforsmack.util.XmppTool;
//
//import static comn.example.imforsmack.util.XmppTool.outgoingFileTransfer;

public class FileTransferActivity extends AppCompatActivity {
    private TextView path;
    private String userJID;
    private String filePath;

    //该程序模拟天成长度为100的数组
    private int[] data = new int[100];
    int hasData = 0;
    // 记录ProgressBar的完成进度
    private double progressStatus = 0;

    private ProgressBar bar;
    double progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_transfer);

        path = (TextView) findViewById(R.id.path);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置文件类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);


        Intent intentJID = getIntent();
        userJID = intentJID.getStringExtra("userJID");
        System.out.println("文件传输的userJID:" + userJID);


         bar = (ProgressBar) findViewById(R.id.bar);



    }


    // 创建一个复杂更新进度的Handler
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x111) {

                bar.setProgress((int) progressStatus);
                Logger.i("handler文件传输的状态："+progressStatus);

            }
        }
    };


    public void confirmeFile(View view) throws InterruptedException {

        bar.setVisibility(View.VISIBLE);

        boolean isTransfer = XmppTool.fileTransfer(userJID,filePath);
        if(isTransfer){

            // 启动线程来执行任务
            new Thread() {
                public void run() {
                    while (progressStatus < 100) {
                        // 获取耗时的完成百分比

                        while(!outgoingFileTransfer.isDone()) {

                            if (outgoingFileTransfer.getStatus().equals(FileTransfer.Status.error)) {
                                //Logger.e("fa文件传输出错了：" + outgoingFileTransfer.getError());
                                return;
                            } else {

                                progress = outgoingFileTransfer.getProgress();

                                progressStatus = progress * 100;

                                Message m = new Message();
                                m.what = 0x111;
                                // 发送消息到Handler
                                handler.sendMessage(m);

                                Logger.i("fffa文件传输的状态：" + progress);
                                Logger.i("fffa文件传输的状态：" + outgoingFileTransfer.getStatus());

                            }
                        }

                    }
                }
            }.start();

        }else {

            Toast.makeText(FileTransferActivity.this,"你已处于离线状态，请检测网络！", Toast.LENGTH_LONG).show();
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode==1) {//是否选择，没选择就不会继续

            Uri uri = data.getData();

            path.setText(getAbsoluteImagePath(this,uri));
            filePath = getAbsoluteImagePath(this,uri);
            System.out.println("文件选中并发送的的路径fa："+getAbsoluteImagePath(this,uri));
        }
    }

    /**
     * 从URI获取本地路径
     */
    private String getAbsoluteImagePath(Activity activity, Uri contentUri) {

        //如果是对媒体文件，在android开机的时候回去扫描，然后把路径添加到数据库中。
        //由打印的contentUri可以看到：2种结构。正常的是：content://那么这种就要去数据库读取path。
        //另外一种是Uri是 file:///那么这种是 Uri.fromFile(File file);得到的
        System.out.println("66:" + contentUri);

        String urlpath;
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(activity, contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        String isFile = contentUri.toString().split(":")[0];

        //如果是文件。转化为对应的文件
        if (isFile.compareTo("file")==0) {

            urlpath = contentUri.toString().replace("file://","");
            System.out.println("如果是文件fa：" + urlpath);
            return urlpath;

        } else {
            try {
                int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                urlpath = cursor.getString(column_index);
                //如果是正常的查询到数据库。然后返回结构
                return urlpath;
            } catch (Exception e) {

                e.printStackTrace();
                // TODO: handle exception
                //return urlpath;

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        }

        urlpath = contentUri.getPath();
        //contentUri.fromFile();
        return urlpath;
    }
}
