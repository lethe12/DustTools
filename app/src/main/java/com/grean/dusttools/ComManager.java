package com.grean.dusttools;

import android.content.Context;
import android.util.Log;

import com.SerialCommunication;
import com.SocketCommunication;
import com.tools;

import java.net.Socket;

/**
 * 处理COM0~3 四个串口
 * 单例化
 * 操作4个COM口,以及LAN口扩展的串口
 * Created by weifeng on 2018/2/25.
 */

public class ComManager {
    private static final String tag = "ComManager";
    public static final int DUST_BIN_STEP_MOTOR = 0,DUST_BIN_GREEN = 1;
    private static ComManager instance = new ComManager();
    private COM[] coms  = new COM[4];
    private  SocketCom[] socketCom = new SocketCom[5];

    private ComManager(){
        coms[0] = new COM(0,9600);
        coms[1] = new COM(1,9600);
        coms[2] = new COM(2,9600);
        coms[3] = new COM(3,57600);
    }

    public boolean writeRegister(int comNumber,byte id,int reg,int value){
        if(isNumOk(comNumber)) {
            if (comNumber <= 3) {
                coms[comNumber].writeRegister(id,reg,value);
            }else{
                socketCom[comNumber-4].writeRegister(id,reg,value);
            }
            return true;
        }else{
            return false;
        }
    }

    public boolean writeRegisters(int comNumber,byte id,int reg,int value[]){
        if(isNumOk(comNumber)) {
            if (comNumber <= 3) {
                coms[comNumber].writeRegisters(id,reg,value);
            }else{
                socketCom[comNumber-4].writeRegisters(id,reg,value);
            }
            return true;
        }else{
            return false;
        }
    }

    public boolean readRegister(int comNumber,byte id,int reg,int num,ReadModBusRegistersListener listener){
        if(isNumOk(comNumber)){
            if(comNumber<=3){
                Log.d(tag,"read reg");
                coms[comNumber].readRegisters(id,reg,num,listener);
                return true;
            }else{
                return socketCom[comNumber-4].readRegisters(id,reg,num,listener);
            }
        }else{
            return false;
        }
    }

    public boolean readIO(int comNumber,byte id,int reg,ReadModBusRegistersListener listener){
        if(comNumber <=3 ){
           coms[comNumber].readIO(id,reg,listener);
            return true;
        }
        return false;
    }

    public boolean writeIO(int comNumber,byte id,int reg,int value){
        if(comNumber <=3 ){
            coms[comNumber].writeIO(id,reg,value);
            return true;
        }
        return false;
    }

    /**
     * 打开网络串口
     */
    public void openSocketCom(SocketComBuilder builder){
        for(int i=0;i<5;i++) {
            socketCom[i] = new SocketCom(builder.context, builder.ip, builder.ports[i]);
        }
    }

    static class SocketComBuilder{
        private String ip;
        private int [] ports = new int[5];
        private Context context;
        public SocketComBuilder ip (String ip){
            this.ip = ip;
            return this;
        }

        public SocketComBuilder ports (int [] ports){
            if(ports.length == 5) {
                System.arraycopy(ports,0,this.ports,0,5);
            }
            return this;
        }

        public SocketComBuilder context(Context context){
            this.context = context;
            return this;
        }

    }

   /* public void sendTcpCom(){
        if(socketCom!=null) {
            socketCom.addSendBuff();
        }
    }*/

    /**
     * 0~3为原生串口；4~8为网络串口
     * @param num
     * @return
     */
    private boolean isNumOk(int num){
        if(num >8){
            return false;
        }else if(num > 3){
            if(socketCom[num-4]==null){
                return false;
            }else{
                return true;
            }
        }else if(num <0){
            return false;
        }else {
            return true;
        }
    }

    public static ComManager getInstance() {
        return instance;
    }

    private class SocketCom extends SocketCommunication {
        private final static int READ_REGISTER = 0,WRITE_REGISTER=1;
        private byte id =0x01;
        private int readNumber;
        private ReadModBusRegistersListener listener;
        @Override
        protected boolean isComplete(byte[] buff, int length) {
            return true;
        }

        @Override
        protected void communicationProtocol(byte[] rec, int size, int state) {
            if(checkFrameWithAddress(rec,size,id)) {
                switch (state){
                    case READ_REGISTER:
                        listener.onComplete(rec,size);
                        break;
                    case WRITE_REGISTER:

                        break;
                    default:
                        break;
                }
            }
            //Log.d(tag,"同步接收:size="+String.valueOf(size)+"content="+tools.bytesToHexString(rec,size));
        }

        @Override
        protected void asyncCommunicationProtocol(byte[] rec, int size) {
            Log.d(tag,"异步接收:size="+String.valueOf(size)+"content="+tools.bytesToHexString(rec,size));
        }

        public SocketCom(Context context,String ip,int port) {
            //super(context, "192.168.1.18", 50001);
            super(context,ip,port);
        }

        public void addSendBuff(){
            byte[] cmd = {0x01,0x03,0x00,0x01,0x00,0x01, (byte) 0xd5, (byte) 0xca};
            boolean success = addSendBuff(cmd,0);
            Log.d(tag,"发送"+String.valueOf(success));

        }

        /**
         * 写一个寄存器
         * @param address 地址
         * @param reg 寄存器地址
         * @param value 寄存器值
         */
        public void writeRegister(byte address,int reg,int value){
            byte [] cmd = {0x01,0x06,0x00,0x00,0x01,0x01,0x0d,0x0a},buff;
            cmd[0] = address;
            buff = tools.int2byte(reg);
            cmd[2] = buff[0];
            cmd[3] = buff[1];
            buff = tools.int2byte(value);
            cmd[4] = buff[0];
            cmd[5] = buff[1];
            tools.addCrc16(cmd,0,6);
            this.id = address;
            addSendBuff(cmd,WRITE_REGISTER);
        }

        /**
         * 使用16指令 连续写寄存器
         * @param address
         * @param reg
         * @param value
         */
        public void writeRegisters(byte address,int reg,int value[]){
            byte [] cmd = new byte[value.length*2+9],buff;
            cmd[0] = address;
            cmd[1] = 0x10;
            buff = tools.int2byte(reg);
            cmd[2] = buff[0];
            cmd[3] = buff[1];
            buff = tools.int2byte(value.length);
            cmd[4] = buff[0];
            cmd[5] = buff[1];
            cmd[6] = (byte) (value.length*2);
            for(int i=0;i<value.length;i++){
                buff = tools.int2byte(value[i]);
                cmd[7+i*2] = buff[0];
                cmd[8+i*2]=buff[1];
            }
            tools.addCrc16(cmd,0,value.length*2+7);
            this.id = address;
            addSendBuff(cmd,WRITE_REGISTER);
        }

        /**
         *
         * @param reg 起始寄存器地址
         * @param readNum 需要读取的寄存器数目
         */
        public boolean readRegisters(byte address,int reg,int readNum,ReadModBusRegistersListener listener){
            byte [] cmd = {0x01,0x03,0x00,0x00,0x00,0x01,0x0d,0x0a},buff;
            cmd[0] = address;
            buff = tools.int2byte(reg);
            cmd[2] = buff[0];
            cmd[3] = buff[1];
            buff = tools.int2byte(readNum);
            cmd[4] = buff[0];
            cmd[5] = buff[1];
            tools.addCrc16(cmd,0,6);
            this.id = address;
            this.readNumber = readNum;
            this.listener = listener;

            return addSendBuff(cmd,READ_REGISTER);
        }
    }

    private class COM extends SerialCommunication {
        private final static int READ_REGISTER = 0,WRITE_REGISTER=1;
        private byte id = 0x01;
        private ReadModBusRegistersListener listener;
        /**
         * @param num      c串口编号 0~3
         * @param
         * @param
         */
        public COM(int num,int baudRate) {
            super(num, baudRate, 0);
        }

        @Override
        protected boolean checkRecBuff() {
            return true;
        }

        @Override
        protected void communicationProtocol(byte[] rec, int size, int state) {
            if(checkFrameWithAddress(rec,size,id)){
                if(state == WRITE_REGISTER){

                }else if(state == READ_REGISTER){
                   // Log.d(tag,"RS232"+String.valueOf(state)+":"+tools.bytesToHexString(rec,size));
                    listener.onComplete(rec,size);
                }
            }
            //Log.d(tag,"RS232"+String.valueOf(state)+":"+tools.bytesToHexString(rec,size));
        }

        @Override
        protected void asyncCommunicationProtocol(byte[] rec, int size) {

        }

        /**
         * 使用05指令强制对应IO口为On或Off状态
         * @param address 地址
         * @param reg 寄存器 0500~0505 Y0~Y5
         * @param value FF00为On 0000为Off
         */
        public void writeIO(byte address,int reg,int value){
            byte [] cmd = {0x01,0x05,0x00,0x00,0x00,0x01,0x0d,0x0a},buff;
            cmd[0] = address;
            buff = tools.int2byte(reg);
            cmd[2] = buff[0];
            cmd[3] = buff[1];
            buff = tools.int2byte(value);
            cmd[4] = buff[0];
            cmd[5] = buff[1];
            tools.addCrc16(cmd,0,6);
            this.id = address;
            addSendBuff(cmd,WRITE_REGISTER);
        }

        public void readIO(byte address,int reg,ReadModBusRegistersListener listener){
            this.listener = listener;
            byte [] cmd = {0x01,0x02,0x00,0x00,0x00,0x01,0x0d,0x0a},buff;
            cmd[0] = address;
            buff = tools.int2byte(reg);
            cmd[2] = buff[0];
            cmd[3] = buff[1];
            tools.addCrc16(cmd,0,6);
            this.id = address;
            addSendBuff(cmd,READ_REGISTER);
        }

        /**
         * 写一个寄存器
         * @param address 地址
         * @param reg 寄存器地址
         * @param value 寄存器值
         */
        public void writeRegister(byte address,int reg,int value){
            byte [] cmd = {0x01,0x06,0x00,0x00,0x01,0x01,0x0d,0x0a},buff;
            cmd[0] = address;
            buff = tools.int2byte(reg);
            cmd[2] = buff[0];
            cmd[3] = buff[1];
            buff = tools.int2byte(value);
            cmd[4] = buff[0];
            cmd[5] = buff[1];
            tools.addCrc16(cmd,0,6);
            this.id = address;
            addSendBuff(cmd,WRITE_REGISTER);
        }
        /**
         * 使用16指令 连续写寄存器
         * @param address
         * @param reg
         * @param value
         */
        public void writeRegisters(byte address,int reg,int value[]){
            byte [] cmd = new byte[value.length*2+9],buff;
            cmd[0] = address;
            cmd[1] = 0x10;
            buff = tools.int2byte(reg);
            cmd[2] = buff[0];
            cmd[3] = buff[1];
            buff = tools.int2byte(value.length);
            cmd[4] = buff[0];
            cmd[5] = buff[1];
            cmd[6] = (byte) (value.length*2);
            for(int i=0;i<value.length;i++){
                buff = tools.int2byte(value[i]);
                cmd[7+i*2] = buff[0];
                cmd[8+i*2]=buff[1];
            }
            tools.addCrc16(cmd,0,value.length*2+7);
            this.id = address;
            //Log.d(tag,tools.bytesToHexString(cmd,cmd.length));
            addSendBuff(cmd,WRITE_REGISTER);
        }
        /**
         *
         * @param reg 起始寄存器地址
         * @param readNum 需要读取的寄存器数目
         * @param listener 回调的接口
         */
        public void readRegisters(byte address,int reg,int readNum,ReadModBusRegistersListener listener){
            this.listener = listener;
            byte [] cmd = {0x01,0x03,0x00,0x00,0x00,0x01,0x0d,0x0a},buff;
            cmd[0] = address;
            buff = tools.int2byte(reg);
            cmd[2] = buff[0];
            cmd[3] = buff[1];
            buff = tools.int2byte(readNum);
            cmd[4] = buff[0];
            cmd[5] = buff[1];
            tools.addCrc16(cmd,0,6);
            this.id = address;
            //Log.d(tag,"RS232->"+tools.bytesToHexString(cmd,cmd.length));
            addSendBuff(cmd,READ_REGISTER);
        }

    }

    /**
     * 校验modbus
     * @param buff
     * @param size
     * @param addr
     * @return
     */
    private boolean checkFrameWithAddress(byte []buff,int size,byte addr){
        if (buff[0]!=addr){
            return false;
        }

        if(!((buff[1]==0x03)||(buff[1]==0x06)||(buff[1]==0x02)||(buff[1]==0x05))){//只接受03、06、02、05指令
            return false;
        }


        if (tools.calcCrc16(buff,0,size)!=0x0000){//CRC校验
            return false;
        }

        return true;
    }
}
