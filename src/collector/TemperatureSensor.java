package collector;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TemperatureSensor implements Sensor<Float> {

	private final static int valueSize = 10;

	private final static float minTemperature = 15f;
	private final static float maxTemperature = 30f;

	private Random rand;
	private Float[] values;
	private int pos;

	public TemperatureSensor() {

		rand = new Random();
		values = new Float[valueSize];
		pos = 0; 

		Timer timer = new Timer();
		TimerTask minuteTask = new TimerTask() {

			@Override
			public void run() {
				float value = minTemperature + (rand.nextFloat() * ((1 + maxTemperature) - minTemperature));
				DecimalFormat df = new DecimalFormat();
				DecimalFormatSymbols dfs = new DecimalFormatSymbols();
				dfs.setDecimalSeparator('.');
				df.setDecimalFormatSymbols(dfs);
				df.setMaximumFractionDigits(1);
				values[pos] = Float.valueOf(df.format(value));
				pos = (pos + 1) % valueSize;
			}
		};

		timer.scheduleAtFixedRate(minuteTask, 0, 2000);
	}

	@Override
	public int getType() {
		return Sensor.TEMPERATURE_SENSOR;
	}

	@Override
	public String getInformation() {
		return "Temperature Sensor (return int values)";
	}

	@Override
	public Float[] getValues() {
		return values;
	}

	@Override
	public Float getLastValue() {
		return values[(pos-1)%valueSize];
	}

	@Override
	public String getName() {
		return "TemperatureSensor";
	}

}
