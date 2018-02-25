package com.grean.dusttools;

import com.SerialCommunication;
import com.tools;

/**
 * 处理COM0~3 四个串口
 * 单例化
 * 操作4个COM口,以及LAN口扩展的串口
 * Created by weifeng on 2018/2/25.
 */

public class ComManager {
    public static final int DUST_BIN_STEP_MOTOR = 0,DUST_BIN_GREEN = 1;
    private static ComManager instance = new ComManager();
    private COM[] coms  = new COM[4];;

    private ComManager(){
        coms[0] = new COM(0);
        coms[1] = new COM(1);
        coms[2] = new COM(2);
        coms[3] = new COM(3);
    }

    private boolean isNumOk(int num){
        if(num >3){
            return false;
        }else if(num <0){
            return false;
        }else {
            return true;
        }
    }

    public static ComManager getInstance() {
        return instance;
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
        public COM(int num) {
            super(num, 9600, 0);
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
                    listener.onComplete(rec,size);
                }
            }
        }

        @Override
        protected void asyncCommunicationProtocol(byte[] rec, int size) {

        }

        private boolean checkFrameWithAddress(byte []buff,int size,byte addr){
            if (buff[0]!=addr){
                return false;
            }

            if(!((buff[1]==0x03)||(buff[1]==0x06))){
                return false;
            }


            if (tools.calcCrc16(buff,0,size)!=0x0000){
                return false;
            }

            return true;
        }

        /**
         * 写一个寄存器
         * @param address 地址
         * @param reg 寄存器地址
         * @param value 寄存器值
         */
        public void WriteRegister(byte address,int reg,int value){
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
         *
         * @param reg 起始寄存器地址
         * @param readNum 需要读取的寄存器数目
         * @param listener 回调的接口
         */
        public void ReadRegisters(byte address,int reg,int readNum,ReadModBusRegistersListener listener){
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
            addSendBuff(cmd,READ_REGISTER);
        }

    }
}
