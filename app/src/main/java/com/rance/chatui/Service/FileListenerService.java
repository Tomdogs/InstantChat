package com.rance.chatui.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.rance.chatui.ui.activity.LoginActivity;
import com.rance.chatui.util.Logger;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import java.io.File;
import java.io.IOException;

import static com.rance.chatui.ui.mainview.MainFragmentActivity.mediaPath;

public class FileListenerService extends Service {

    public FileListenerService() {
    }

    /* 重写Binder的onBind函数，返回派生类 */
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        FileManager fileManager = new FileManager(getBaseContext());
        fileManager.initialize(LoginActivity.xmppconnection);

        System.out.println("----------------fileservice start");

        return START_STICKY;
    }



    private class FileManager implements FileTransferListener {

        public FileTransferManager fileTransferManager;
        public IncomingFileTransfer incomingFileTransfer;

        Context context;

        public FileManager(Context context) {

            this.context = context;

        }

        public void initialize(XMPPConnection connection) {

            fileTransferManager = FileTransferManager.getInstanceFor(connection);
            fileTransferManager.addFileTransferListener(this);

        }


        @Override
        public void fileTransferRequest(FileTransferRequest fileTransferRequest) {

            incomingFileTransfer = fileTransferRequest.accept();
            String fromWhere = fileTransferRequest.getRequestor();


            Logger.i("文件监听接收的 fromWhere：" + fromWhere);
            Logger.i("文件监听接收的 type：" + fileTransferRequest.getMimeType());
            Logger.i("文件监听接收的 StreamID()：" + fileTransferRequest.getStreamID());




            //接收
            IncomingFileTransfer incomingFileTransfer = fileTransferRequest.accept();


            File externalFileDir = new File(mediaPath, "/files/");

            if (!externalFileDir.exists())
                externalFileDir.mkdirs();

            File landingDir = new File(externalFileDir, incomingFileTransfer.getFileName());

            try {

                //接收的位置
                incomingFileTransfer.recieveFile(landingDir);

            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            //发送文件广播
            Intent fileIntent = new Intent();
            fileIntent.putExtra("fileMessageName", landingDir.getAbsolutePath());
            fileIntent.putExtra("fileFromWhere", fileTransferRequest.getRequestor());
            fileIntent.putExtra("fileSize", fileTransferRequest.getFileSize());
            fileIntent.setAction("com.rance.service.FileListenerService");
            context.sendBroadcast(fileIntent);


        }

    }
}
