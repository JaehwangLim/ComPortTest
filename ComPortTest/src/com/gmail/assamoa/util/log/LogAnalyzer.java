package com.gmail.assamoa.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.gmail.assamoa.comm.comport.ComPortHandler;
import com.gmail.assamoa.comm.comport.ComPortListener;

public class LogAnalyzer implements ComPortListener {
	private static final String NEW_LINE = "\n";
	private ComPortHandler comPort;

	private boolean newLine = true;

	public LogAnalyzer() {
		comPort = new ComPortHandler();
		String[] ports = comPort.getAvailablePorts();

		System.out.println("Available com-ports:");
		for (int i = 0; i < ports.length; i++) {
			System.out.println(ports[i]);
		}
		System.out.println("Type port name, which you want to use, and press Enter...");
		Scanner in = new Scanner(System.in);
		String portName = in.next();
		in.close();

		comPort.connect(portName, this);
	}

	@Override
	public void notifyMessage(String message) {
		// NEW_LINE을 포함하여 토큰 분리
		StringTokenizer token = new StringTokenizer(message, NEW_LINE, true);
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
	}

	private static String getTimeString() {
		String value = new String();
		SimpleDateFormat format = new SimpleDateFormat("MM.dd.HH:mm:ss.SSS");
		value = format.format(new Date());
		return value;
	}
}
