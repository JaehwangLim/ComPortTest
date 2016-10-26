
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*; // IOException
import java.text.SimpleDateFormat;
import java.util.*; // Scanner
import jssc.*;

/**
 *
 * @author Emiliarge
 */
public class ComPortTest {

	private static SerialPort serialPort;

	private static final String NEW_LINE = "\n";

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		String[] portNames = SerialPortList.getPortNames();

		if (portNames.length == 0) {
			System.out.println(
					"There are no serial-ports :( You can use an emulator, such ad VSPE, to create a virtual serial port.");
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
		for (int i = 0; i < portNames.length; i++) {
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

			// serialPort.setParams(SerialPort.BAUDRATE_9600,
			serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

			serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
			// writing string to port
			serialPort.writeString("Hurrah!!!");

			System.out.println("String wrote to port, waiting for response..");
		} catch (SerialPortException ex) {
			System.out.println("Error in writing data to port: " + ex);
		}
	}

	// receiving response from port
	private static class PortReader implements SerialPortEventListener {
		boolean newLine = true;

		@Override
		public void serialEvent(SerialPortEvent event) {
//			System.out.println("start--->");
			if (event.isRXCHAR() && event.getEventValue() > 0) {
				try {
					String receivedData = serialPort.readString(event.getEventValue());

					// if (receivedData.contains(nl)) {
					StringTokenizer token = new StringTokenizer(receivedData, NEW_LINE, true);
					while (token.hasMoreTokens()) {
						String text = token.nextToken();
						if (newLine) {
							System.out.print(getTimeString() + "\t");
							newLine = false;
						}
						System.out.print(text);
						if (text.equals(NEW_LINE)) {
							newLine = true;
						}
					}
					// int index = receivedData.indexOf(nl);
					// int lastIndex = receivedData.lastIndexOf(nl);
					// if (index != lastIndex) {
					// // 줄바꿈이 여러개 있음
					// } else {
					// if (index == receivedData.length() - 1) {
					// newLine = true; // 줄바꿈으로 끝난 경우, 다음 로그 앞에 시간찍기 위해
					// // flag 셋팅
					// } else {
					// // 데이터 중간에 줄바꿈이 있는 경우, 여기에 시간을 추가 한다.
					// String currTime = getTimeString();
					// receivedData = receivedData.replace(nl, nl + currTime +
					// "\t");
					// }
					// }
					// } else {
					// if (newLine) {
					// String currTime = getTimeString() + "\t";
					// System.out.print(currTime);
					// newLine = false;
					// }
					// }
					// System.out.print(receivedData);
				} catch (SerialPortException ex) {
					System.out.println("Error in receiving response from port: " + ex);
				}
			}
//			System.out.println("<---end");
		}
	}

	private static String getHex(int i) {
		String a = "0x";
		a += Integer.toHexString(i);
		return a;
	}

	private static String getTimeString() {
		String value = new String();
		SimpleDateFormat format = new SimpleDateFormat("MM.dd.HH:mm:ss.SSS");
		value = format.format(new Date());
		return value;
	}
}
