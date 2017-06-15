package edu.wing.yytang.common;

import edu.wing.yytang.tvconsole.R;

/**
 * Created by yytang on 1/25/17.
 */

public interface Constants {
    int DEFAULT_PORT = 8001;

    public static final String ACTION_REFRESH = "edu.wing.yytang.ACTION_REFRESH"; // causes SvmpActivity to refresh its layout
    public static final String ACTION_STOP_SERVICE = "edu.wing.yytang.ACTION_STOP_SERVICE"; // causes
    public static final String ACTION_LAUNCH_APP = "edu.wing.yytang.LAUNCH_APP";
    public static final String PERMISSION_REFRESH = "edu.wing.yytang.PERMISSION_REFRESH"; // required to send ACTION_REFRESH intent

    // used to map sensor IDs to key names
    public static final int[] PREFERENCES_SENSORS_KEYS = {
            R.string.preferenceKey_sensor_accelerometer,
            R.string.preferenceKey_sensor_magneticField,
            R.string.preferenceKey_sensor_orientation,         // virtual
            R.string.preferenceKey_sensor_gyroscope,
            R.string.preferenceKey_sensor_light,
            R.string.preferenceKey_sensor_pressure,
            R.string.preferenceKey_sensor_temperature,
            R.string.preferenceKey_sensor_proximity,
            R.string.preferenceKey_sensor_gravity,             // virtual
            R.string.preferenceKey_sensor_linearAcceleration,  // virtual
            R.string.preferenceKey_sensor_rotationVector,      // virtual
            R.string.preferenceKey_sensor_relativeHumidity,
            R.string.preferenceKey_sensor_ambientTemperature
    };

    // used to map sensor IDs to default values
    public static final int[] PREFERENCES_SENSORS_DEFAULTVALUES = {
            R.string.preferenceValue_sensor_accelerometer,
            R.string.preferenceValue_sensor_magneticField,
            R.string.preferenceValue_sensor_orientation,         // virtual
            R.string.preferenceValue_sensor_gyroscope,
            R.string.preferenceValue_sensor_light,
            R.string.preferenceValue_sensor_pressure,
            R.string.preferenceValue_sensor_temperature,
            R.string.preferenceValue_sensor_proximity,
            R.string.preferenceValue_sensor_gravity,             // virtual
            R.string.preferenceValue_sensor_linearAcceleration,  // virtual
            R.string.preferenceValue_sensor_rotationVector,      // virtual
            R.string.preferenceValue_sensor_relativeHumidity,
            R.string.preferenceValue_sensor_ambientTemperature
    };

    // multiplier for minimum sensor updates
    public static final double[] SENSOR_MINIMUM_UPDATE_SCALES = {
            1.0, // accelerometer
            1.0, // magnetic field
            1.0, // orientation
            1.0, // gyroscope
            1.0, // light
            1.0, // pressure
            1.0, // temperature
            1.0, // proximity
            1.0, // gravity
            1.0, // linear acceleration
            1.0, // rotation vector
            1.0, // relative humidity
            1.0  // ambient temperature
    };
}
