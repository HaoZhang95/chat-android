package com.example.ahao9.chatclient.components;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * set I/O stream once for whole chat application
 * do not need to connect server in each activity
 */
public class ClientSocket extends Socket{
	private static ClientSocket clientSocket = null;
	private static DataOutputStream dos;
	private static DataInputStream dis;
	private static String ipAddress;
	private static int port;
	private static String hostIPAddress;
	private static int hostPortNum;

	private ClientSocket(String ipAddress,int port) throws IOException {
		super(ipAddress,port);
	}

	//connect
	public static ClientSocket getClientSocket(){
		try {
			if (clientSocket == null) {
				clientSocket = new ClientSocket(ipAddress, port);
				hostIPAddress = clientSocket.getLocalAddress().toString();
				hostPortNum = clientSocket.getLocalPort();
			}
		} catch (IOException e) {
			Log.e("cuowu",e.getMessage() + " from clientSocket activity");
		}
		return clientSocket;
	}

	//get inputstream
	public static DataInputStream getDis(){
		try {
			if (dis == null) {
				dis = new DataInputStream(clientSocket.getInputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dis;
	}

	//get outputstream
	public static DataOutputStream getDos(){
		try {
			if (dos == null) {
				Log.d("cuowu", "getDos: " + clientSocket);
				dos = new DataOutputStream(clientSocket.getOutputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dos;
	}

	public static String getIpAddress() {
		return ipAddress;
	}

	public static void setIpAddress(String ipAddress) {
		ClientSocket.ipAddress = ipAddress;
	}

	public static int getPortNum(){
		return port;
	}

	public static void setPortNum(int port) {
		ClientSocket.port = port;
	}

	public static String getHostIPAddress() {
		return hostIPAddress;
	}

	public static int getHostPortNum() {
		return hostPortNum;
	}

}
