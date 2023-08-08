package com.enabot.socketserver;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

class ListeningThread extends Thread {
    private static final String TAG = "ListeningThread";
    private int port;
    private boolean flag = true;
    private ServerSocket lServerSocket;
    private List<PrintWriter> connections = new Vector<PrintWriter>();    //保存所有连结
    public ListeningThread(int aPort){
        port = aPort;
    }
    public void run(){
        try {
            lServerSocket = new ServerSocket(port);
            lServerSocket.setSoTimeout(1000);
            Log.d(TAG, "TCP Socket Start listening......");
            while(flag) {
                try {
                    Socket incoming = lServerSocket.accept();
                    Log.d(TAG,"Accept "+incoming.getInetAddress()+"("+incoming.getPort()+")");
                    //PrintWriter out = new PrintWriter(incoming.getOutputStream(),true);
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream(), "UTF-8"),true);
                    connections.add(out);    //有新连结则将其输出流添加到连结列表中
                    out.println("Welcome to "+lServerSocket.getInetAddress()+"("+lServerSocket.getLocalPort()+")");    
                    out.flush();
                    Thread t = new ServiceThread(incoming);    //启动接收数据线程
                    t.start();
                }
                catch(SocketTimeoutException e) {
                    if(!flag)break;
                }
            }
            lServerSocket.close();
            System.exit(0);
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }
    public synchronized void chatMessage(String msg) {
        Iterator<PrintWriter> iter = connections.iterator();
        while(iter.hasNext()) {
            try {
                PrintWriter out = iter.next();
                out.println(msg);
                out.flush();
            }
            catch(Exception e) {
                iter.remove();    //如果发送中出现异常，则将连结移除
            }
        }
    }
    class ServiceThread extends Thread {
        private Socket lSocket;
        public ServiceThread(Socket aSocket){
            lSocket = aSocket;
        }
        public void run(){
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(lSocket.getInputStream(), "UTF-8"));
                String line;
                while(flag){
                    line = in.readLine();
                    if(line != null){
                        Log.d(TAG,line);    // 本地屏幕显示
                        chatMessage("Chat:"+line);    // 发送到所有客户端
                    }
                }
                lSocket.close();
                Log.d(TAG,"Thread stoped.");
            }
            catch(IOException e){
                System.out.println(e);
            }
        }
    }
}