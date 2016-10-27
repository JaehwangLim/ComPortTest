package com.gmail.assamoa.comm.comport;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class ComPortHandler implements SerialPortEventListener {
	private String[] ports;
	private String portName;
	private SerialPort serialPort;
	private ComPortListener comPortListener;

	public ComPortHandler() {
		refreshPort();
	}

	public void refreshPort() {
		ports = SerialPortList.getPortNames();
	}

	public String[] getAvailablePorts() {
		return ports;
	}

	public boolean connect(String portName, ComPortListener listener) {
		comPortListener = listener;
		this.portName = portName;
		serialPort = new SerialPort(portName);

		// opening port
		try {
			serialPort.openPort();
		} catch (SerialPortException e) {
			System.out.println("unable to open " + portName);
			return false;
		} catch (Throwable t) {
			System.out.println("Error while open " + portName + ":" + t);
			return false;
		}
		try {
			// serialPort.setParams(SerialPort.BAUDRATE_9600,
			serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

			serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);

			return true;
		} catch (SerialPortException ex) {
			System.out.println("unable to set " + portName);
		} catch (Throwable t) {
			System.out.println("Error while connect: " + t);
		}
		return false;
	}

	public boolean close() {
		try {
			return serialPort.closePort();
		} catch (SerialPortException e) {
			System.out.println("unable to close " + portName);
		} catch (Throwable t) {
			System.out.println("Error while close: " + t);
		}
		return false;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.isRXCHAR() && event.getEventValue() > 0) {
			try {
				comPortListener.notifyMessage(serialPort.readString(event.getEventValue()));
			} catch (SerialPortException e) {
				e.printStackTrace();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
