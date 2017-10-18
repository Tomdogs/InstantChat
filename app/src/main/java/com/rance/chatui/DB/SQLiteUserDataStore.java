/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Copyright @ 2015 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rance.chatui.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rance.chatui.enity.MessageInfo;
import com.rance.chatui.enity.MuiltRoom;
import com.rance.chatui.enity.Person;
import com.rance.chatui.util.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.jivesoftware.smackx.privacy.packet.PrivacyItem.Type.jid;

public class SQLiteUserDataStore extends  SQLiteOpenHelper
{


    public SQLiteUserDataStore(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("create table if not exists chat(_id integer " +
                "primary key autoincrement," +
                "jid varchar(50) ," +//jid
                "nickname varchar(50)," +//昵称
                "filepath varchar(50)," +//文件路径
                "content varchar(200)," +//文字消息
                "type varchar(50)," +//左右
                "time long," +//时间
                "header varchar(50)," +//头像的路径
                "image_url varchar(50)," + //图片的路径
                "voice_time varchar(50)," +//声音的时间
                "send_accept_state varchar(50))");//发送或接收的状态


        db.execSQL("create table if not exists im_group(_id integer " +
                "primary key autoincrement," +
                "gid varchar(50) ," +//jid
                "get_from varchar(50) ," +//哪个用户
                "nickname varchar(50)," +//群昵称
                "content varchar(200)," +//文字消息
                "type varchar(50)," +//左右
                "time varchar(50)," +//时间
                "header varchar(50)," +//头像的路径
                "send_accept_state varchar(50))");//发送或接收的状态

        Logger.i("建表了");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /**
     * 根据jid查询数据
     * @param jid
     * @param dataStore
     * @return
     */
    public static LinkedList<MessageInfo> getCahtDataFormJID(String jid,SQLiteUserDataStore dataStore)
    {

        LinkedList<MessageInfo> linkedList = new LinkedList<>();

        if (jid != null)
        {
            Logger.i(" getCahtDataFormJID 查询数据的JID:"+jid);
           synchronized (dataStore)
            {

                SQLiteDatabase db = dataStore.getReadableDatabase();

                Cursor cursor  = db.query("chat",null,"jid=?",new String[]{jid},null,null,null);

            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){

                    String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
                    String filepath = cursor.getString(cursor.getColumnIndex("filepath"));
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    String image_url = cursor.getString(cursor.getColumnIndex("image_url"));
                    String voice_time = cursor.getString(cursor.getColumnIndex("voice_time"));
                    String header = cursor.getString(cursor.getColumnIndex("header"));
                    String send_accept_state = cursor.getString(cursor.getColumnIndex("send_accept_state"));

                    MessageInfo messageInfo = new MessageInfo();

                    System.out.println("1111111111111111:"+content);
                    System.out.println("22222222222222222:"+time);
                    messageInfo.setMsgId(jid);
                    messageInfo.setContent(content);
                    messageInfo.setTime(time);
                    messageInfo.setFilepath(filepath);

                if(voice_time == null ){
                    messageInfo.setVoiceTime(0);
                }else {
                    messageInfo.setVoiceTime(Long.parseLong(voice_time));
                }

                    messageInfo.setHeader(header);
                    messageInfo.setImageUrl(image_url);
                    messageInfo.setSendState(Integer.parseInt(send_accept_state));
                    messageInfo.setType(Integer.parseInt(type));
                    messageInfo.setNickname(nickname);

                    linkedList.add(messageInfo);

                }

                cursor.close();

            }

        }
        return linkedList;
    }


    /**
     * 群组的历史查询
     * @param gid
     * @param dataStore
     * @return
     */
    public static LinkedList<MuiltRoom> getGroupDataFormJID(String gid,SQLiteUserDataStore dataStore)
    {

        LinkedList<MuiltRoom> linkedList = new LinkedList<>();

        if (gid != null)
        {
            Logger.i("查询数据的giD:"+gid);

            synchronized (dataStore)
            {

                SQLiteDatabase db = dataStore.getReadableDatabase();

                Cursor cursor  = db.query("im_group",null,"gid=?",new String[]{gid},null,null,null);

                for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){

                    String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
                    String jid = cursor.getString(cursor.getColumnIndex("get_from"));
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    //String image_url = cursor.getString(cursor.getColumnIndex("image_url"));
                    //String voice_time = cursor.getString(cursor.getColumnIndex("voice_time"));
                    String header = cursor.getString(cursor.getColumnIndex("header"));
                    String send_accept_state = cursor.getString(cursor.getColumnIndex("send_accept_state"));

                    //MessageInfo messagInfo = new MessageInfo();
                    MuiltRoom muiltRoom = new MuiltRoom();

                    System.out.println("qun 1111111111111111:"+content);
                    System.out.println("qun 22222222222222222:"+time);
                    muiltRoom.setGid(gid);
                    muiltRoom.setContent(content);
                    muiltRoom.setTime(time);
                    muiltRoom.setJid(jid);
                    muiltRoom.setHeader(header);
                    muiltRoom.setSendState(Integer.parseInt(send_accept_state));
                    muiltRoom.setType(Integer.parseInt(type));
                    muiltRoom.setNickname(nickname);

                    linkedList.add(muiltRoom);


                }

                cursor.close();

            }

        }
        return linkedList;
    }



    /**
     * 最近时间数据查询
     * @param dataStore
     * @return
     */
    public static ArrayList<Person> queryRecentChatData(SQLiteUserDataStore dataStore,int currentPage)
    {

        ArrayList<Person> linkedList = new ArrayList<>();

        if (jid != null)
        {

            synchronized (dataStore)
            {

                //int index = (currentPage-1)*2;
                SQLiteDatabase db = dataStore.getReadableDatabase();

                                                            //group by  having    order by
                Cursor cursor  = db.query("chat",null,null,null,"jid",null,"time desc limit "+20+" offset "+currentPage);
                //Cursor cursor  = db.query("chat",null,null,null,"jid",null,"time desc limit 50");

                for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){

                    String jid = cursor.getString(cursor.getColumnIndex("jid"));
                    String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
                    String filepath = cursor.getString(cursor.getColumnIndex("filepath"));
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    String image_url = cursor.getString(cursor.getColumnIndex("image_url"));


                    if(jid.contains("@")){
                        jid = jid.split("@")[0];
                    }
                    Person person = new Person(image_url,jid,content,time);
                    person.setFace(image_url);
                    person.setName(jid);

                    if(content == null){
                        String filestr [] = filepath.split("/");
                        Logger.i("content 为空 file路径："+filestr[filestr.length-1]);
                        person.setSign(filestr[filestr.length-1]);
                        //person.setSign(filepath.substring(filepath.length()-2,filepath.length()-1));
                    }else {
                        person.setSign(content);
                    }


                    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String timestr = dateformat.format(Long.parseLong(time));
                    person.setTime(timestr);

                    Logger.i("-------------------------------");
                    System.out.print("query jid:"+jid+" ");
                    System.out.print("query conent:"+content+" ");
                    System.out.println("query time:"+timestr);
                    Logger.i("-------------------------------");

                    linkedList.add(person);


                }

                cursor.close();

            }

        }
        return linkedList;
    }

}
