/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*; // IOException
import java.util.*; // Scanner
import jssc.*;

/**
 *
 * @author Emiliarge
 */
public class ComPortTest {

    private static SerialPort serialPort;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[] portNames = SerialPortList.getPortNames();
        
        if (portNames.length == 0) {
            System.out.println("There are no serial-ports :( You can use an emulator, such ad VSPE, to create a virtual serial port.");
            System.out.println("Press Enter to exit...");
            try {
                System.in.read();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
        
        System.out.println("Available com-ports:");
        for (int i = 0; i < portNames.length; i++){
            System.out.println(portNames[i]);
        }
        System.out.println("Type port name, which you want to use, and press Enter...");
        Scanner in = new Scanner(System.in);
        String portName = in.next();
        
        // writing to port
        serialPort = new SerialPort(portName);
        try {
            // opening port
            serialPort.openPort();
            
//            serialPort.setParams(SerialPort.BAUDRATE_9600,
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                                 SerialPort.DATABITS_8,
                                 SerialPort.STOPBITS_1,
                                 SerialPort.PARITY_NONE);
            
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
                                          SerialPort.FLOWCONTROL_RTSCTS_OUT);
            
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
            // writing string to port
            serialPort.writeString("Hurrah!!!");
            
            System.out.println("String wrote to port, waiting for response..");
        }
        catch (SerialPortException ex) {
            System.out.println("Error in writing data to port: " + ex);
        }
    }
    
    // receiving response from port
    private static class PortReader implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    String receivedData = serialPort.readString(event.getEventValue());
                    String nl = "\n";//String.valueOf(0x0d)+ String.valueOf(0x0a);
                    if(receivedData.contains(nl)){
                    	receivedData = receivedData.replace(nl, nl+"newLine\t");
//                    	System.out.println("\nNEW LINE!!!");
                    } else {
//                    	char[] chars = receivedData.toCharArray();
//                    	for(int i=0; i<chars.length; i++){
//                    		System.out.print(" "+ getHex(chars[i]));
//                    	}
//                    	System.out.println("\n"+receivedData);
                    }
                    System.out.print(receivedData);
                }
                catch (SerialPortException ex) {
                    System.out.println("Error in receiving response from port: " + ex);
                }
            }
        }
    }
    
    private static String getHex(int i)
    {
    	String a = "0x";
    	a+=Integer.toHexString(i);
    	return a;
    }
}
