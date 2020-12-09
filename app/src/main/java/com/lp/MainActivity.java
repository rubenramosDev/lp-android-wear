package com.lp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor pressureSensor;
    private Sensor humiditySensor;


    private TextView txtRPressure, txtRLight, txtRHumidity;

    private DatabaseReference firebaseDatabase;

    private final String LIGHT_CONST     = "LIGHT";
    private final String HUMIDITY_CONST  = "HUMIDITY";
    private final String PRESSURE_CONST  = "PRESSURE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initElements();
        initSensors();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

//        readValues();
    }

    private void initElements() {
        txtRPressure = findViewById(R.id.txtRPressure);
        txtRLight = findViewById(R.id.txtRLight);
        txtRHumidity = findViewById(R.id.txtRHumidity);
    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        lightSensor    = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    private void readValues(){
        DatabaseReference messagesRef = firebaseDatabase.getRoot();
        FirebaseDatabase data = messagesRef.getDatabase();

        DatabaseReference reference = data.getReference(LIGHT_CONST);
        
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Dummy data = snapshot.getValue(Dummy.class);
                System.out.println(data.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Log.e("values", messagesRef.toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Integer sensorType = sensorEvent.sensor.getType();
        Float currentValue = sensorEvent.values[0];

        DatabaseReference messagesRef = firebaseDatabase.getRoot();


        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                txtRLight.setText(currentValue.toString());
                messagesRef.child(LIGHT_CONST).push().setValue("value",currentValue.toString());
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                txtRHumidity.setText(currentValue.toString());
                messagesRef.child(HUMIDITY_CONST).push().setValue("value",currentValue.toString());
                break;

            case Sensor.TYPE_PRESSURE:
                txtRPressure.setText(currentValue.toString());
                messagesRef.child(PRESSURE_CONST).push().setValue("value",currentValue.toString());
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}