package com;

import android.util.Log;

import com.serial.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * c串口通讯的父类，处理通讯同步问题
 * Created by Administrator on 2017/8/24.
 */

public abstract class SerialCommunication {
    private static  final String tag="SerialCommunication";

    private final Lock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
    protected boolean flag = false;
    private ConcurrentLinkedQueue<byte[]> sendBuff = new ConcurrentLinkedQueue<byte[]>();
    private ConcurrentLinkedQueue<Integer>comState = new ConcurrentLinkedQueue<Integer>();
    private int state;
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    private int recSize;
    private byte[] recBuff = new byte[1024];
    private ReadThread readThread;
    private SendThread sendThread;
    private int sendTimeOut;
    /**
     *
     * @param num c串口编号 0~3
     * @param baudrate 波特率
     * @param stopbit
     */
    public SerialCommunication(int num,int baudrate,int stopbit){
        try {
            mSerialPort = getSerialPort(num, baudrate, stopbit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mInputStream = mSerialPort.getInputStream();
        mOutputStream = mSerialPort.getOutputStream();
        readThread = new ReadThread();
        sendThread = new SendThread();
        readThread.start();
        sendThread.start();
    }

    /**
     * 检验收到字节流是否合规
     * @return
     */
    protected  abstract boolean checkRecBuff();

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


    private SerialPort getSerialPort(int num,int baudrate,int stopbit) throws IOException {
        if (mSerialPort == null){
            String path = "/dev/ttyS1";
            switch (num){
                case 0:
                    path = "/dev/ttyS0";
                    break;
                case 1:
                    path = "/dev/ttyS1";
                    break;
                case 2:
                    path = "/dev/ttyS2";
                    break;
                case 3:
                    path = "/dev/ttyS3";
                    break;
                default:
                    break;
            }
            Log.d(tag,"right");
            mSerialPort = new SerialPort(new File(path),baudrate,stopbit);

        }
        return  mSerialPort;
    }

    private class ReadThread extends Thread{

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()){
                try {
                    while (mInputStream.available()==0){
                        Thread.sleep(50);
                    }
                    Thread.sleep(100);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    recSize = mInputStream.read(recBuff);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (recSize>0){
                    lock.lock();
                    if (checkRecBuff()) {
                        try {
                            if (flag) {
                                communicationProtocol(recBuff, recSize,state);
                                flag = false;
                                cond.signal();
                                //cond.signalAll();
                            } else {
                                asyncCommunicationProtocol(recBuff, recSize);
                            }
                        } catch (Exception e) {

                        } finally {
                            lock.unlock();
                        }
                       // Log.d(tag, "sizeof"+String.valueOf(recSize)+":"+tools.bytesToHexString(recBuff,recSize));
                    }
                    else{
                        //Log.d(tag, "unchecked frame sizeof"+String.valueOf(recSize)+":"+tools.bytesToHexString(recBuff,recSize));
                    }


                }

            }
        }
    }

    protected void addSendBuff(byte[] buff, int state){
        comState.add(state);
        sendBuff.add(buff);
    }

    private  class SendThread extends  Thread{

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()){
                lock.lock();
                try{
                    if(!sendBuff.isEmpty()){
                      if (!flag){
                          flag = true;
                          byte [] buff = sendBuff.poll();
                          state = comState.poll();
                          mOutputStream.write(buff);
                          mOutputStream.flush();
                          sendTimeOut = 0;
                          //Log.d(tag,"send:"+tools.bytesToHexString(buff,buff.length));
                      }
                      else{
                          sendTimeOut++;
                          if(sendTimeOut > 4){
                              sendTimeOut = 0;
                              flag = false;
                          }
                          //wait(100);
                          try {
                              Thread.sleep(100);
                          } catch (InterruptedException e) {
                              e.printStackTrace();
                          }
                      }

                    }

                }
                catch (Exception e){

                }
                finally {
                    lock.unlock();
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
