package com.rance.chatui.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.rance.chatui.enity.User;
import com.rance.chatui.ui.activity.LoginActivity;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.bookmarks.Bookmarks;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.rance.chatui.ui.activity.LoginActivity.xmppconnection;


/**
 * 工具类
 */
public class XmppTool {

	public static MultiUserChat muc;
	private static XmppTool xmppTool =null;

	private static final String TAG = "XmppTool";
	private static String HOST = "112.74.181.112";
	//private static String host = "";
	private static final int PORT = 5222;
	private static final String SERVICENAME = "iZwz9cppdz0tjjbqalu817Z";
	//private static XMPPTCPConnection connection;
	//private static final String SERVICENAME = "master";

	//private  Abstract XMPPConnection abstractXMPPConnection;
	public static OutgoingFileTransfer outgoingFileTransfer;

	public static void setHost(String host) {
		XmppTool.HOST = host;
	}

	public static AbstractXMPPConnection XMPPConnection(){

		XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();

		configBuilder.setHost(HOST);
		configBuilder.setPort(PORT);
		configBuilder.setServiceName(SERVICENAME);
		configBuilder.setCompressionEnabled(true);
		configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		//登录前要将状态设置为离线,为了接收离线消息
		configBuilder.setSendPresence(false);

		configBuilder.setConnectTimeout(10000);
		//configBuilder.setResource("test");
		//configBuilder.setDebuggerEnabled(true);
		XMPPTCPConnection connection=new XMPPTCPConnection(configBuilder.build());
		//abstractXMPPConnection = new XMPPTCPConnection(configBuilder.build());

		return connection;
	}

	public static XMPPConnection getConnection(){

		if (xmppconnection == null) {

			try {
				xmppconnection = XMPPConnection().connect();
				Log.i(TAG,"连接服务器成功");
			} catch (SmackException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XMPPException e) {
				e.printStackTrace();
			}
		}
		return xmppconnection;
	}

	/**
	 * Description: 断开连接
	 */
	public static void closeConnection(AbstractXMPPConnection abstractXMPPConnection){
		if (abstractXMPPConnection != null && abstractXMPPConnection.isConnected()) {

			abstractXMPPConnection.disconnect();
			//abstractXMPPConnection = null;

			System.out.println("服务器登录断开连接");

		}
	}

	public static boolean userLogin(AbstractXMPPConnection connection, final String userName, final String password){

		try {
			 //if(!XMPPConnection().isConnected()){
			 if(!connection.isConnected()){
				Log.i(TAG,"ConnectionIsConnected()"+connection.isConnected());
				 //连接
				 connection.connect();
				 //connection = XMPPConnection();
				 Log.i(TAG,"ConnectionIsConnected()"+connection.isConnected());
			 }
			 //登录
			 connection.login(userName, password);
			 Log.i(TAG,"登陆成功！");

			 //在线功能设计
			/* Presence presence = new Presence(Presence.Type.available);
			 presence.setStatus("luo am online");
			 connection.sendStanza(presence);*/
			 return true;
		} catch (Exception e) {
			Log.i(TAG,e.getMessage());
			 return false;
		}
	}

	public static boolean userRegister(AbstractXMPPConnection connection, String username,String password){
		AccountManager accountManager;
		boolean isSupport = false;

		Log.i(TAG,"ConnectionIsConnected()"+connection.isConnected());
		try {
			/*if(!connection.isConnected()){
				//连接
				//connection.connect();

				Log.i(TAG,"ConnectionIsConnected()"+connection.isConnected());
			}*/
			//connection = XMPPConnection();
			accountManager =AccountManager.getInstance(xmppconnection);
			accountManager.createAccount(username,password);
			Log.i(TAG,"注册成功！");
			//设置为true允许通过不安全连接的敏感操作。不然会报错的
			accountManager.sensitiveOperationOverInsecureConnection(true);
			//如果服务器支持创建新帐户，则返回true.
			//isSupport = accountManager.supportsAccountCreation();
			isSupport=true;
			return isSupport;
		} catch (Exception e) {
			Log.i(TAG,e.getMessage());
			return false;
		}
	}
	public static VCard getUserVCard(String user) throws XMPPException
	{
		VCard vCard;
		try {

			VCardManager vCardManager = VCardManager.getInstanceFor(xmppconnection);

			Log.e("connection  is",""+xmppconnection.isConnected());
			vCard = vCardManager.loadVCard(user);

			//boolean isSupport = vCardManager.isSupported(user);
			String jabberID = vCard.getJabberId();

			Log.i(TAG,"jabberId"+jabberID+"&&"+vCard.getFirstName());
			//Log.i(TAG,"isSupport"+isSupport);


		} catch (SmackException.NoResponseException e) {
			e.printStackTrace();
			return null;
		} catch (SmackException.NotConnectedException e) {
			e.printStackTrace();
			return null;
		}

		return vCard;
	}


	/**
	 * 获取用户头像信息
	 */
	public static Bitmap getUserImage(String user) {
		//ImageIcon  ic = null;
		Bitmap bmp = null;
		try {
			//vcard.load(connection, user);

			VCardManager vCardManager = VCardManager.getInstanceFor(xmppconnection);
			VCard vCard = vCardManager.loadVCard(user);


			if(vCard == null || vCard.getAvatar() == null)
			{
				return null;
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(vCard.getAvatar());

			//添加compile files ('C:/Program Files/Java/jre7/lib/rt.jar')，否则找不到
			/*BufferedImage image =ImageIO.read(bais);
			ic = new ImageIcon(image);
			System.out.println("图片大小:"+ic.getIconHeight()+" "+ic.getIconWidth());*/

			BitmapDrawable bmpDraw = new BitmapDrawable(bais);
			bmp = bmpDraw.getBitmap();



		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}


	public static boolean addUser(Roster roster, String userName, String name, String groupName) {
		try {
			roster.createEntry(userName, name, new String[] { groupName });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	synchronized public static XmppTool getInstance() {
		if(xmppTool == null){
			xmppTool =new XmppTool();
		}
		return xmppTool;
	}
	public static List<User> searchUsers(String username){

		XMPPConnection conn = xmppconnection;


		List<User> results = new ArrayList<>();


		/*UserSearch userSearch = new UserSearch();
		try {
			Form form = userSearch.getSearchForm(conn,conn.getServiceName());
			List<FormField> formField = form.getFields();
			for(FormField f :formField){

				List list = f.getValues();
				for(int i = 0;i<list.size();i++){
					Logger.i("用户的搜索结果："+list.get(i));
				}
			}
		} catch (SmackException.NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPException.XMPPErrorException e) {
			e.printStackTrace();
		} catch (SmackException.NotConnectedException e) {
			e.printStackTrace();
		}*/


		UserSearchManager userSearchManager = new UserSearchManager(conn);
		//UserSearch userSearch = new UserSearch();

		try {
			//List listServices = userSearchManager.getSearchServices();

//			if(listServices.isEmpty())
//				throw new Exception("No search services available");
			//new ServiceDiscoveryManager(conn);
			//Form searchForm = userSearchManager.getSearchForm(listServices.iterator().next().toString());
			Form searchForm = userSearchManager.getSearchForm(conn.getServiceName());
			//Form searchForm =userSearch.getSearchForm(conn,conn.getServiceName());


			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username",true);
			answerForm.setAnswer("search",username);

			ReportedData data = userSearchManager.getSearchResults(answerForm,"users."+conn.getServiceName());
			//ReportedData data = userSearch.sendSearchForm(conn,answerForm,conn.getServiceName());

			Iterator<ReportedData.Row> it = data.getRows().iterator();
			User user;
			ReportedData.Row row = null;
			while (it.hasNext()) {

				Log.i(TAG,"OUT 查询到了！！！");
				user = new User();
				user.setName(row.getValues("Name").toString());
				user.setUser(row.getValues("Username").toString());
				user.setEmail(row.getValues("Email").toString());


				Log.i(TAG,"userSerach"+row.getValues("Name").toString());
				Log.i(TAG,row.getValues("Username").toString());
				Log.i(TAG,row.getValues("Email").toString());

				results.add(user);
			}

		}  catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}
	public static int  createChatRoom(final String chatRoomBName , String userName, String password){

		final XMPPConnection  conne = xmppconnection;

		muc = MultiUserChatManager.getInstanceFor(conne).getMultiUserChat(chatRoomBName+"@muc."+conne.getServiceName());

		Log.i(TAG,"创建room的全称："+chatRoomBName+"@muc."+conne.getServiceName());


		try {

			boolean isJoin = muc.isJoined();
			//Logger.i("创建房间是否已加入："+isJoin);

			if(!isJoin){
				//创建聊天
				muc.create(chatRoomBName);
				muc.join(userName);
				//muc.createOrJoin("smack");

			/*//授予权限
			muc.grantOwnership(userName);
			muc.grantAdmin(userName);
			muc.grantMembership(userName);*/

				Form form = muc.getConfigurationForm();
				Form submitForm = form.createAnswerForm();


			/*//打印Filds的属性
			List<FormField> fieldss=form.getFields();
			for(FormField field:fieldss)
			{
				if(!FormField.Type.hidden.equals(field.getType())&&field.getVariable()!=null)
				{
					submitForm.setDefaultAnswer(field.getVariable());
					System.out.println("罗："+field.getVariable()+"     "+field.getType().toString()+"   "+field.getLabel());
				}
			}*/

				/**
				 * muc#roomconfig_roomname     text-single   Natural-Language Room Name
				 muc#roomconfig_roomdesc     text-single   Short Description of Room
				 muc#roomconfig_persistentroom     boolean   Make Room Persistent?
				 muc#roomconfig_publicroom     boolean   Make Room Publicly Searchable?
				 muc#roomconfig_moderatedroom     boolean   Make Room Moderated?
				 muc#roomconfig_membersonly     boolean   Make Room Members Only?
				 muc#roomconfig_passwordprotectedroom     boolean   Password Required to Enter?
				 muc#roomconfig_roomsecret     text-single   Password
				 muc#roomconfig_anonymity     list-single   Room anonymity level:
				 muc#roomconfig_changesubject     boolean   Allow Occupants to Change Subject?
				 muc#roomconfig_enablelogging     boolean   Enable Public Logging?
				 logging_format     list-single   Logging format:
				 muc#maxhistoryfetch     text-single   Maximum Number of History Messages Returned by Room
				 muc#roomconfig_maxusers     list-single   Maximum Number of Occupants
				 tigase#presence_delivery_logic     list-single   Presence delivery logic
				 tigase#presence_filtering     boolean   Enable filtering of presence (broadcasting presence only between selected groups
				 tigase#presence_filtered_affiliations     list-multi   Affiliations for which presence should be delivered
				 */

				// 向要提交的表单添加默认答复
				for (Iterator fields = form.getFields().iterator(); fields.hasNext();) {
					FormField field = (FormField) fields.next();
					if (!FormField.FORM_TYPE.equals(field.getType())
							&& field.getVariable() != null) {
						// 设置默认值作为答复
						submitForm.setDefaultAnswer(field.getVariable());
					}
				}
				//让房间公开搜索
				submitForm.setAnswer("muc#roomconfig_publicroom",true);
				// 设置聊天室是持久聊天室，即将要被保存下来
				submitForm.setAnswer("muc#roomconfig_persistentroom",true);
				//设置房间的名字
				submitForm.setAnswer("muc#roomconfig_roomname",chatRoomBName);

				//设置群聊的拥有者
				List owners = new ArrayList();
				owners.add(conne.getUser());

				// 房间仅对成员开放
				submitForm.setAnswer("muc#roomconfig_membersonly", false);

				if(password != null && password.length() != 0) {
					// 进入是否需要密码
					submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
					// 设置进入密码
					submitForm.setAnswer("muc#roomconfig_roomsecret", password);

				}
				// 登录房间对话
				submitForm.setAnswer("muc#roomconfig_enablelogging", true);

				// 发送已完成的表单（有默认值）到服务器来配置聊天室
				muc.sendConfigurationForm(submitForm);

				return 0;
			}else {

				return 1;
			}





		} catch (XMPPException.XMPPErrorException e) {
			e.printStackTrace();
			return 2;
		} catch (SmackException e) {
			e.printStackTrace();
			return 3;
		}

	}
	public static boolean joinChatRoom(String chatRoomBName , String userNickName, String password){

		XMPPConnection  conne = xmppconnection;
		muc = MultiUserChatManager.getInstanceFor(conne).getMultiUserChat(chatRoomBName+"@muc."+conne.getServiceName());

		// 聊天室服务将会决定要接受的历史记录数量
		DiscussionHistory discussionHistory = new DiscussionHistory();
		discussionHistory.setMaxChars(0);
		try {

			muc.join(userNickName,password);

			//授予权限
			//muc.grantAdmin(userNickName);
			//muc.grantMembership(userNickName);

			return true;
		} catch (XMPPException.XMPPErrorException e) {
			e.printStackTrace();
			return  false;
		} catch (SmackException e) {
			e.printStackTrace();
			return  false;
		}
	}
	public static Collection<BookmarkedConference> getAllMUCRooms() {

		XMPPConnection  conne = xmppconnection;

		PrivateDataManager manager = PrivateDataManager.getInstanceFor(conne);
		Bookmarks bm = new Bookmarks();

		manager.addPrivateDataProvider(bm.getElementName(),bm.getNamespace(),new Bookmarks.Provider());

		try {
			bm = (Bookmarks) manager.getPrivateData(bm.getElementName(),bm.getNamespace());
		} catch (SmackException.NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPException.XMPPErrorException e) {
			e.printStackTrace();
		} catch (SmackException.NotConnectedException e) {
			e.printStackTrace();
		}

		Collection<BookmarkedConference> c = bm.getBookmarkedConferences();
		for(BookmarkedConference bookmarkedConference:c){
			Log.i(TAG,"bookmarke存储的群："+bookmarkedConference.getName());
			Log.i(TAG,"bookmarke存储的群昵称："+bookmarkedConference.getNickname());
			Log.i(TAG,"bookmarke存储的群jid："+bookmarkedConference.getJid());
			Log.i(TAG,"bookmarke存储的群密码："+bookmarkedConference.getPassword());

		}

		return  bm.getBookmarkedConferences();
	}
	public static List<MultiUserChat>  getAllMucRoom() {

		XMPPConnection  conne = xmppconnection;
		List<MultiUserChat> listMultiUser = new ArrayList<>();

		Logger.i("XMPPConnection对象是："+XMPPConnection());

		try {

			MultiUserChatManager multiUserChatManager =  MultiUserChatManager.getInstanceFor(conne);
			List<HostedRoom> roomList = multiUserChatManager.getHostedRooms("muc."+conne.getServiceName());


			//会议获取所有的服务器是房间
			for(HostedRoom hostedRoom : roomList){

				MultiUserChat multiUser =multiUserChatManager.getMultiUserChat(hostedRoom.getJid());

				listMultiUser.add(multiUser);

				//Logger.i(TAG+"发现的Strng multiUser："+multiUser);
				//Logger.i(TAG+"发现的Strng getNickname："+multiUser.getNickname());
				//Logger.i(TAG+"发现的Strng getRoom："+multiUser.getRoom());

			}
		} catch (SmackException.NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPException.XMPPErrorException e) {
			e.printStackTrace();
		} catch (SmackException.NotConnectedException e) {
			e.printStackTrace();
		}

		return listMultiUser;
	}
	public static List<Affiliate> getRoomDetails(String roomJID){

		XMPPConnection  conne = xmppconnection;
		MultiUserChat muc = MultiUserChatManager.getInstanceFor(conne).getMultiUserChat(roomJID);
		List roomList = null;

		try {
			roomList = muc.getMembers();

			return roomList;
		} catch (SmackException.NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPException.XMPPErrorException e) {
			e.printStackTrace();
		} catch (SmackException.NotConnectedException e) {
			e.printStackTrace();
		}

		return roomList;
	}

	/**
	 * 发送文件
	 * @param userJID
	 * @param filePath
     * @return
     */
	public static boolean fileTransfer(String userJID,String filePath){

		//FileTransferManager fileTransferManager;


		Presence presence = Roster.getInstanceFor(LoginActivity.xmppconnection).getPresence(userJID);//获得用户状态
		//自己只能在线发送，不能处于离线状态
		if(presence.getType() == Presence.Type.available)
		{
			String userFullJID = presence.getFrom();//这里获得的是完整JID
			//然后在进行文件传输。
			System.out.println("fa文件完整JID："+ userFullJID);

			FileTransferManager fileTransferManager = FileTransferManager.getInstanceFor(xmppconnection);
			outgoingFileTransfer = fileTransferManager.createOutgoingFileTransfer(userFullJID);

			try {

				outgoingFileTransfer.sendFile(new java.io.File(filePath),"sendFile");


				System.out.println("fa文件getServiceName："+ xmppconnection.getServiceName());
				System.out.println("fa文件getStreamId："+ xmppconnection.getStreamId()+"<>"+ xmppconnection.getFromMode());
				System.out.println("fa文件传输的进度:："+outgoingFileTransfer.getProgress());
				System.out.println("fa文件传输出错了："+outgoingFileTransfer.isDone());
				//System.out.println("used "+((System.currentTimeMillis()-startTime)/1000)+" seconds  ");


				return true;

			} catch (SmackException e) {
				e.printStackTrace();
				System.out.print("fa文件传输出错了");
			}

			return false;

		}else {

			return false;
		}
	}
	public static List<User> updateRoster(Roster roster) {

		List<User> userList = new ArrayList<>();

		//重新加载名单和块，直到它被重新加载。避免好友列表为空
		try {
			roster.reloadAndWait();
		} catch (SmackException.NotLoggedInException e) {
			e.printStackTrace();
		} catch (SmackException.NotConnectedException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.i(TAG,"updateRoster执行了没啊啊：");
		Log.i(TAG,"isLoaded："+  roster.isLoaded());
		Collection<RosterEntry> entries = roster.getEntries();
		Collection<RosterGroup> groups = roster.getGroups();


		for(RosterGroup group :groups){
			System.out.println("组名为："+group.getName());
			List<RosterEntry> entryList = group.getEntries();
			for(RosterEntry entry : entryList){
				System.out.print("成员为："+entry.getName());
				System.out.println("---user:"+entry.getUser());
				entry.getType();
			}
		}

		for (RosterEntry entry : entries) {
			System.out.print("花名册（好友）："+entry.getName() + " - " + entry.getUser() + " - "
					+ entry.getType() + " - " + entry.getGroups().size());

			Presence presence = roster.getPresence(entry.getUser());
			boolean available = presence.isAvailable();
			//boolean islogin=presence.
			System.out.println(" ->状态：" + presence.getStatus() + " - "+ presence.getFrom()+"-"+available);


			User user = new User();
			user.setName(entry.getName());
			user.setUser(entry.getUser());
			user.setAvailable(available);
			//user.setLogin();
			user.setSize(entry.getGroups().size());
			user.setStatus(presence.getStatus());
			user.setFrom(presence.getFrom());

			userList.add(user);
		}

		return  userList;
	}

	/**
	 * 不属于任何组的条目
	 * @return
	 */
	public static List<User> unfiledEntity(Roster roster){

		//Roster roster = videoactivity.roster;
		//Roster roster= ContactsFragment.roster;
		List<User> userList = new ArrayList<>();
		Collection<RosterEntry> unfiledEntity = roster.getUnfiledEntries();
		Iterator<RosterEntry> iter = unfiledEntity.iterator();
		while (iter.hasNext()) {
			RosterEntry entry = iter.next();
			//Logger.i("未同意的好友信息;" + entry.getName());

			Presence presence = roster.getPresence(entry.getUser());
			boolean available = presence.isAvailable();
			User user = new User();
			user.setName(entry.getName());
			user.setUser(entry.getUser());
			user.setAvailable(available);
			user.setSize(entry.getGroups().size());
			user.setStatus(presence.getStatus());
			user.setFrom(presence.getFrom());

			userList.add(user);

		}
		return userList;
	}




}
