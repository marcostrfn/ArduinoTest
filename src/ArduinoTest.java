
public class ArduinoTest implements ArduinoControl {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArduinoTest m = new ArduinoTest();
		Arduino a = new Arduino();
		a.initializeArduinoConnection(m);
	}

	@Override
	public void getData(String s) {
		// TODO Auto-generated method stub
		System.out.println("Longitud " + s.length());
		System.out.println("getData " + s);
	}
	
}