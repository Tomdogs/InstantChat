# ChatUI
# 基于XMPP协议的移动即时通讯
## 一、简介
### 1.1 XMPP协议
XMPP协议（可扩展消息传递和存在协议），是一种以XML为基础的开放式即时通讯协议，其网络架构是一个典型的C/S架构。多数情况下，这些消息都是经过服务器的，这样简化了客服端的设计[13]。XMPP最初是在Jabber开源社区开发的，旨在为当时封闭的即时通讯服务提供一个开放，分散的解决方案。XMPP提供了以下几个关键优势：</br>
(1)开放性——XMPP协议是开源的、简易的; 此外，客户机，服务器，服务器组件和代码库的形式存在多种实现[14]。</br>
(2)标准性 ——互联网工程任务组（IETF）  将核心XML流协议形式化为已批准的即时消息和存在技术[15]。XMPP标准基金会已经发布许多标准XMPP扩展协议。</br>
(3)分散化 ——任何XMPP用户都可以向其他XMPP用户传递消息，许多XMPP服务器也可以通过一个专用的“Server—Server”协议相互通信，使个人或组织能够提升他们的通信体验。</br>
(4)安全 性——任何XMPP服务器都可与公共网络（内部网）进行隔离，并且在核心XMPP规范中内置了使用SASL和TLS，保证了强大安全性  [14]。</br>
(5)灵活 性——超越IM的XMPP应用程序包括网络管理，内容联合，协作工具，文件共享，游戏，远程系统监控，Web服务，轻量级中间件，云计算等等[16]。</br>
(6)多样化 ——大量的公司和开源项目组织都在使用XMPP构建和部署实时应用程序和服务。</br>
官网 [https://xmpp.org/](https://xmpp.org/)
### 1.2 Tigase简介
Tigase XMPP服务器是特立独行的，它完全支持最新的规范和其它扩展[6]。本平台就是基于tigase服务器部署与实现的。</br>
(1)高度优化。对设备要求较低，可运行在10M内存的设备上。每个部件或组件都可以通过文件配置加载或替代</br>
	(2)灵活性。集成到系统中非常方便，支持开箱即用的集群，无需寻找附加软件或扩展库[8]。</br>
	(3)为低、中、高档服务器而设计。还在Amazon的EC2云上广泛测试过。</br>
	(4)良好的测试。有专业的测试工具来进行行自动化测试并且有手动运行兼容性测试，以及大量的负载测试。</br>
(6)支持大量脚本。例如Python、Ruby、Scala等脚本语言编写扩展。</br>
	(7)易于监控。可以通过HTTP、SNMP、JMX、XMPP等方式监控服务器。</br>
	(8)支持SSL。Tigase从设计上就保证了安全性，保护用户的隐私。API不允许从一用户访问另一用户的数据，在服务器端程序实现了强隔离[12]。</br>
 这个是这个项目后台，本项目只包含Android端，后端暂时还没有提供，你可以自己搭建一个服务器。官网 [https://tigase.net/content/tigase-messenger-android](https://tigase.net/content/tigase-messenger-android)
### 1.3 Smack简介
Smack是一个开源，易于使用的XMPP（Jabber）客户端类库。本平台的即时通讯IM就是基于smack类库进行开发和实现的。Smack API, 是 Java 的XMPP Client Library[13]。 Smack是一个用 Java编程语言编写的XMPP客户端代码库。</br>
(1)简单——Smack是一个简单的，功能强大的类库，不需要你熟悉XMPP XML格式，甚至是XML格式。</br>
(2)开源——Apace License下的开源软件，可简易的运用在商业或非商业应用程序中。</br>
这个本项目所使用的SDK，官网 [https://igniterealtime.org/projects/smack/](https://igniterealtime.org/projects/smack/)
### 1.4 Jitsi 简介
Jitsi是一个基于XMPP协议开源的PC客户端。具有安全、高质量的XMPP视频通话、会议、聊天、桌面共享、文件传传输等功能。可以安装到你喜欢的操作系统中并且支持多种IM网络[12]。Jitsi让你可以在同一个软件中连到国外主流的社交网络和IM系统进行通信，像脸书、谷歌交流、Windows Live、雅虎、AIM和ICQ等软件。在此系统中，Jitsi主要作为PC端和Android端的测试软件。
官网 [https://jitsi.org/](https://jitsi.org/)
## 二、运行截图
### 2.1主界面

<div align=center><img width="250" src="https://github.com/Tomdogs/InstantChat/raw/master/screenshots/image011.png"/>
<img width="250" src="https://github.com/Tomdogs/InstantChat/raw/master/screenshots/image012.png"/>
<img width="250" src="https://github.com/Tomdogs/InstantChat/raw/master/screenshots/image013.png"/></div>

### 2.1聊天界面（文字、语音、文件）

<div align=center><img width="250" src="https://github.com/Tomdogs/InstantChat/raw/master/screenshots/image015.png"/>
<img width="250" src="https://github.com/Tomdogs/InstantChat/raw/master/screenshots/image016.png"/>
<img width="250" src="https://github.com/Tomdogs/InstantChat/raw/master/screenshots/image018.png"/>
<img width="250" src="https://github.com/Tomdogs/InstantChat/raw/master/screenshots/image019.png"/>
<img width="250" src="https://github.com/Tomdogs/InstantChat/raw/master/screenshots/image020.png"/>
<img width="250" src="https://github.com/Tomdogs/InstantChat/raw/master/screenshots/image022.png"/></div>

### 2.1群组界面
<div align=center><img width="250" src="https://github.com/Tomdogs/InstantChat/raw/master/screenshots/image023.png"/>
<img width="250" src="https://github.com/Tomdogs/InstantChat/raw/master/screenshots/image024.png"/>
</div>
