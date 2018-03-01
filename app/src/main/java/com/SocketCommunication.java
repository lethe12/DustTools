package com;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 操作socket线程
 * Created by weifeng on 2018/2/26.
 */

public abstract class SocketCommunication {
    private final static String tag = "SocketCommunication";
    private String ipAddress;
    private int port;
    private Context context;
    private Socket socketClient;
    private boolean run, connected = false,flag;
    private ConcurrentLinkedQueue<byte[]> sendBuffer = new ConcurrentLinkedQueue<byte[]>();
    private ConcurrentLinkedQueue<Integer>comState = new ConcurrentLinkedQueue<Integer>();
    private int state;
    private final Lock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();


    protected abstract boolean isComplete(byte [] buff,int length);
    /**
     * 通讯数据流，同步信息，发送立即返回
     * @param rec 返回数组
     * @param size 数组大小
     */
    protected  abstract void communicationProtocol(byte[] rec,int size,int state);

    /**
     * 异步通讯流，无发送即接收
     * @param rec 数组地址
     * @param size 数组大小
     */
    protected abstract void  asyncCommunicationProtocol(byte[] rec,int size);


    public SocketCommunication(Context context,String ip,int port){
        this.ipAddress = ip;
        this.port = port;
        this.context = context;
        new ReceiveThread().start();
    }

    /**
     * 增加发送buff
     * @param buff 发送的数据
     * @param state ModBus状态
     * @return
     */
    protected boolean addSendBuff(byte[] buff, int state){
        if(connected) {
            comState.add(state);
            sendBuffer.add(buff);
        }
        return connected;
    }

    public boolean isConnected(){
        return connected;
    }

    public void disconnect(){
        run = false;
        if(socketClient!=null){
            try {
                socketClient.shutdownOutput();
                socketClient.shutdownInput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class WriteThread extends Thread{
        private OutputStream send;
        public WriteThread(OutputStream send){
            this.send = send;
        }
        @Override
        public void run() {
            int sendTimeOut = 0;
            while (connected&&(!interrupted())){
                try {
                    lock.lock();

                    if(!sendBuffer.isEmpty()){
                        if (!flag){
                            //cond.awaitNanos(6000000);
                            flag = true;
                            byte[] buf = sendBuffer.poll();
                            send.write(buf);
                            state = comState.poll();
                            send.flush();
                            sendTimeOut = 0;
                           // wait(100);
                        }
                        else{
                            sendTimeOut++;
                            if(sendTimeOut > 4){
                                sendTimeOut = 0;
                                flag = false;
                                //Log.d(tag,"发送超时");
                            }
                            //Log.d(tag,"发送超时计数"+String.valueOf(sendTimeOut));
                            delay(100);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } /*catch (InterruptedException e) {
                    e.printStackTrace();
                }*/ finally {
                    lock.unlock();
                }
                delay(100);
            }


        }
    }

    /**
     * 启动接收线程
     * 流程：判断是否联网->新建socket连接->接收线程
     */
    private class ReceiveThread extends Thread{
        private boolean isOnline(){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            //Log.d(tag,String.valueOf(info!=null));
            if(info!=null && info.isAvailable()){
                return true;
            }else{
                return false;
            }
        }

        private void initSocket() throws IOException {
            Log.d(tag,"server="+ipAddress+":"+String.valueOf(port));
            socketClient = new Socket();
            socketClient.connect(new InetSocketAddress(ipAddress,port),5000);
            socketClient.setTcpNoDelay(true);
            socketClient.setSoLinger(true,30);
            socketClient.setSendBufferSize(10240);
            socketClient.setReceiveBufferSize(10240);
            socketClient.setKeepAlive(true);
            //receive = socketClient.getInputStream();
            //send = socketClient.getOutputStream();
            socketClient.setOOBInline(true);
        }

        private void receiveSocket(InputStream receive)throws IOException{
            int count,index=0;
            byte[] readBuff = new byte[10240];
            byte[] readContent = new byte[20481];
            if (socketClient.isConnected() && (!socketClient.isClosed())) {

                while ((count = receive.read(readBuff)) != -1 && connected) {
                    if((index+count)<=20480) {//未超buff区间
                        System.arraycopy(readBuff, 0, readContent, index, count);
                        index += count;
                        if(isComplete(readContent,index)){//帧完整
                            lock.lock();
                            try {
                                if (flag) {
                                    communicationProtocol(readBuff, index,state);
                                    flag = false;
                                    cond.signal();
                                } else {
                                    asyncCommunicationProtocol(readBuff, index);
                                }
                            } catch (Exception e) {
                                Log.d(tag,"协议异常");
                            } finally {
                                lock.unlock();
                            }
                            index=0;
                        }
                    }else{
                        index=0;
                    }
                }
            }
        }

        @Override
        public void run() {
            InputStream receive = null;
            OutputStream send = null;
            run  = true;
            while (run&&(!interrupted())){
                Log.d(tag," 重置连接");
                while ((!isOnline())&&run){
                    delay(5000);
                }
                connected = false;
                if(run) {
                    try {
                        initSocket();
                        try {
                            receive = socketClient.getInputStream();
                            send = socketClient.getOutputStream();
                            connected = true;
                            Log.d(tag, "已连接服务器");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (SocketException e) {
                        e.printStackTrace();
                        Log.d(tag, "连接异常1");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(tag, "连接异常2");
                    }
                }
                try {
                    if(connected){//如成功连接则启动发送线程
                        new WriteThread(send).start();
                    }
                    while (connected && run && (!interrupted())) {
                        receiveSocket(receive);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
                connected = false;
            }
            Log.d(tag,"结束Socket");
        }
    }

    private void delay(int ms){
        if(ms>0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
