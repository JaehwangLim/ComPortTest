
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
			if (event.isRXCHAR() && event.getEventValue() > 0) {
				try {
					String receivedData = serialPort.readString(event.getEventValue());

					// NEW_LINE을 포함하여 토큰 분리
					StringTokenizer token = new StringTokenizer(receivedData, NEW_LINE, true);
					while (token.hasMoreTokens()) {
						String text = token.nextToken();
						// 직전에 NEW_LINE을 찍은 경우, 현재시간을 찍고, 다음 내용을 출력하도록 한다.
						// 즉, 새로운 라인을 찍게 되는 경우, 앞에 현재시간 출력
						if (newLine) {
							System.out.print(getTimeString() + "\t");
							newLine = false;
						}
						// text 출력
						System.out.print(text);
						// 방금 찍은게 NEW_LINE 이면, 다음에 시간출력을 위한 flag 셋팅
						if (text.equals(NEW_LINE)) {
							newLine = true;
						}
					}
				} catch (SerialPortException ex) {
					System.out.println("Error in receiving response from port: " + ex);
				}
			}
		}
	}

	private static String getTimeString() {
		// 바꿨는데 왜 안나오지?
		String value = new String();
		SimpleDateFormat format = new SimpleDateFormat("MM.dd.HH:mm:ss.SSS");
		value = format.format(new Date());
		return value;
	}
}
