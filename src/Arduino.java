
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class Arduino implements SerialPortEventListener {

	/** Puerto de conexion */
	private final String PORT_NAME = "COM5";
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 115200;
	
	// variables para mandar a arduino
	private static final String ON = "On";
	private static final String OFF = "Off";
	private static final String TURN_ON = "1";
	private static final String TURN_OFF = "0";

	/** The output stream to the port */
	private OutputStream output = null;
	private InputStream input = null;
	SerialPort serialPort;
	
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;

	// manejar la cadena de informacion recibida de arduino
	private char[] aChar = new char[1024];
	private int numero = 0;
	
	// para interaccion con la clase que le llama
	ArduinoTest miArduino;

	
	
	public void initializeArduinoConnection(ArduinoTest at) {

		
		miArduino = at;
		
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// iterate through, looking for the port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();

			System.out.println("Port Name : " + currPortId.getName());
			if (PORT_NAME.equals(currPortId.getName())) {
				portId = currPortId;
				break;
			}
		}

		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			output = serialPort.getOutputStream();
			input = serialPort.getInputStream(); // Se prepara input para recibir datos

			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public void sendData(String data) {
		try {
			output.write(data.getBytes());
		} catch (IOException e) {
			System.out.println("Error sending data");
		}
	}

	private int RecibirDatos() throws IOException {
		int output = 0;
		output = input.read();
		return output;
	}

	public void serialEvent(SerialPortEvent oEvent) {
		// TODO Auto-generated method stub
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int datos;
				datos = RecibirDatos(); // Se invoca la función RecibirDatos()
				
				switch (datos) {
				case 10:
					break;
				case 13:
					System.out.println("Total " + numero);
					char [] fChar = new char[numero];
					for (int i=0;i<numero;i++) {
						fChar[i] = aChar[i];
					}
					for (int i=0;i<1024;i++) {
						aChar[i] = 0;
					}
					numero = 0;
					parseString(fChar);
					break;
				default:
					System.out.print(datos + ":"); // Se imprime en el mensaje
					aChar[numero++] = (char) datos;	
					break;
				}
				
				
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

	private void parseString(char[] c) {
		// TODO Auto-generated method stub
		System.out.println("Recibido " + String.valueOf(c));
		miArduino.getData(String.valueOf(c));
	}

}
