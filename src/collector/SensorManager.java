package collector;

public class SensorManager {

	public final static Sensor<?>[] getSensors(){
		Sensor<?>[] sensors = new Sensor[2];
		sensors[0] = new TimeSensor();
		sensors[1] = new TemperatureSensor();
		return sensors;
	}
}
