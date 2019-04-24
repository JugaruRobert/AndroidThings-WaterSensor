package com.led.led;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String redLedPin = "BCM26";
    private static final String blueLedPin = "BCM6";
    private static final String greenLedPin = "BCM5";
    private Gpio mLedGpioRed;
    private Gpio mLedGpioBlue;
    private Gpio mLedGpioGreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PeripheralManager manager = PeripheralManager.getInstance();
        configureLeds(manager);
        setLedValue(mLedGpioGreen, true);
        setLedValue(mLedGpioBlue, true);
        setLedValue(mLedGpioRed, true);
        boolean allLedsOff = true;
        if (allLedsOff) {
            setLedValue(mLedGpioGreen, false);
            setLedValue(mLedGpioBlue, false);
            setLedValue(mLedGpioRed, false);
        } else {
            try {
                Adc0832 adc0832 = new Adc0832();
                while (true) {
                    Integer value = adc0832.getADCChannelValue(0);
                    if (value <= 100) {
                        setLedValue(mLedGpioGreen, true);
                        setLedValue(mLedGpioBlue, false);
                        setLedValue(mLedGpioRed, false);
                    } else if (value > 100 && value <= 125) {
                        setLedValue(mLedGpioGreen, false);
                        setLedValue(mLedGpioBlue, true);
                        setLedValue(mLedGpioRed, false);
                    } else if (value > 125) {
                        setLedValue(mLedGpioGreen, false);
                        setLedValue(mLedGpioBlue, false);
                        setLedValue(mLedGpioRed, true);
                    }
                    Log.e("VALUE", value.toString());
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setLedValue(Gpio gpio, boolean value) {
        try {
            gpio.setValue(value);
        } catch (IOException e) {
            Log.e(TAG, "Error updating GPIO value", e);
        }
    }

    public void configureLeds(PeripheralManager pioService) {
        try {
            Log.i(TAG, "Configuring GPIO pins");
            mLedGpioRed = pioService.openGpio(redLedPin);
            mLedGpioBlue = pioService.openGpio(blueLedPin);
            mLedGpioGreen = pioService.openGpio(greenLedPin);
            mLedGpioRed.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpioBlue.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpioGreen.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            Log.e(TAG, "Error configuring GPIO pins", e);
        }
    }
}
