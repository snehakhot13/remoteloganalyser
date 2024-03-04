package com.logFileAnalyser.srvice;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ServerConnection {
	
	
	private String search;
	private static String host;
	private static Integer port;
	private static String user;
	private static String password;
	
	private static JSch jsch;
	private static Session session;
	private static Channel channel;
	private static ChannelSftp sftpChannel;
	
	
	
	public ServerConnection(String host, Integer port, String user, String password) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
	}
	
	public static void connect() {
		
		System.out.println("connecting..."+host);
		try {
			jsch = new JSch();
			session = jsch.getSession(user, host,port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();

			channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;

		} catch (JSchException e) {
			e.printStackTrace();
		}

	}
	
	public static void disconnect1() {
		System.out.println("disconnecting...");
		sftpChannel.disconnect();
		channel.disconnect();
		session.disconnect();
	}
	

}
