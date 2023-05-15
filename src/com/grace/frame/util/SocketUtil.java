package com.grace.frame.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.grace.frame.exception.AppException;

/**
 * Socket相关的工具类
 * 
 * @author yjc
 */
public class SocketUtil{
	public static final String BROADCAST_ADDRESS = "255.255.255.255";// 广播的IP地址

	/**
	 * UDP协议的数据发送
	 * <p>
	 * address:目标IP; <br>
	 * 1.单播：网络节点之间的通信就好像是人们之间的对话一样。如果一个人对另外一个人说话，那么用网络技术的术语来描述就是“单播”，
	 * 此时信息的接收和传递只在两个节点之间进行
	 * 。单播在网络中得到了广泛的应用，网络上绝大部分的数据都是以单播的形式传输的，只是一般网络用户不知道而已。例如
	 * ，你在收发电子邮件、浏览网页时，必须与邮件服务器、Web服务器建立连接，此时使用的就是单播数据传输方式。但是通常使用“点对点通信”（Point
	 * to Point）代替“单播”，因为“单播”一般与“多播”和“广播”相对应使用。
	 * 2.多播：“多播”也可以称为“组播”，在网络技术的应用并不是很多，
	 * 网上视频会议、网上视频点播特别适合采用多播方式。因为如果采用单播方式，逐个节点传输
	 * ，有多少个目标节点，就会有多少次传送过程，这种方式显然效率极低，是不可取的
	 * ；如果采用不区分目标、全部发送的广播方式，虽然一次可以传送完数据，但是显然达不到区分特定数据接收对象的目的
	 * 。采用多播方式，既可以实现一次传送所有目标节点的数据，也可以达到只对特定对象传送数据的目的。
	 * 　　IP网络的多播一般通过多播IP地址来实现。多播IP地址就是D类IP地址
	 * ，即224.0.0.0至239.255.255.255之间的IP地址。Windows 2000中的DHCP管理器支持多播IP地址的自动分配。
	 * 3.广播：“广播”在网络中的应用较多，如客户机通过DHCP自动获得IP地址的过程就是通过广播来实现的。但是同单播和多播相比，
	 * 广播几乎占用了子网内网络的所有带宽。拿开会打一个比方吧，在会场上只能有一个人发言，想象一下如果所有的人同时都用麦克风发言，那会场上就会乱成一锅粥。
	 * 集线器由于其工作原理决定了不可能过滤广播风暴
	 * ，一般的交换机也没有这一功能，不过现在有的网络交换机（如全向的QS系列交换机）也有过滤广播风暴功能了，路由器本身就有隔离广播风暴的作用。
	 * 　　广播风暴不能完全杜绝
	 * ，但是只能在同一子网内传播，就好像喇叭的声音只能在同一会场内传播一样，因此在由几百台甚至上千台电脑构成的大中型局域网中，一般进行子网划分
	 * ，就像将一个大厅用墙壁隔离成许多小厅一样，以达到隔离广播风暴的目的。
	 * 　　在IP网络中，广播地址用IP地址“255.255.255.255”来表示，这个IP地址代表同一子网内所有的IP地址。 <br>
	 * port:端口；<br>
	 * data:数据
	 * </p>
	 * 
	 * @author yjc
	 * @throws IOException
	 * @throws AppException
	 * @date 创建时间 2017-7-3
	 * @since V1.0
	 */
	public static void sendDataByUDP(String address, int port, byte[] data) throws IOException, AppException {
		if (StringUtil.chkStrNull(address)) {
			throw new AppException("目标地址为空");
		}
		if (0 == port) {
			throw new AppException("目标端口为空");
		}
		if (null == data || data.length <= 0) {
			throw new AppException("发送的数据为空");
		}

		DatagramSocket dataSocket = new DatagramSocket();// 创建用来发送数据报包的套接字
		try {
			DatagramPacket dataPacket = new DatagramPacket(data, data.length, InetAddress.getByName(address), port);
			dataSocket.send(dataPacket);// 发送数据报
		} catch (Exception e) {
			throw new AppException(e.getCause());// 抛出异常
		} finally {
			dataSocket.close();
		}
	}

	/**
	 * UDP协议的数据发送
	 * 
	 * @author yjc
	 * @throws AppException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @date 创建时间 2017-7-3
	 * @since V1.0
	 */
	public static void sendDataByUDP(String address, int port, String dataStr,
			String charset) throws UnsupportedEncodingException, IOException, AppException {
		if (StringUtil.chkStrNull(dataStr)) {
			throw new AppException("数据字符串[dataStr]为空");
		}
		if (StringUtil.chkStrNull(charset)) {
			throw new AppException("数据编码格式[charset]为空");
		}
		SocketUtil.sendDataByUDP(address, port, dataStr.getBytes(charset));
	}

	/**
	 * UDP协议的数据发送
	 * 
	 * @author yjc
	 * @date 创建时间 2017-7-3
	 * @since V1.0
	 */
	public static void sendDataByUDP(String address, int port, String dataStr) throws Exception {
		SocketUtil.sendDataByUDP(address, port, dataStr, "UTF-8");
	}
}
