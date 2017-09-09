package io.github.mmbs.androidthingsperipherals;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;
import java.io.IOException;
import timber.log.Timber;

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 *
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class HomeActivity extends Activity {

  private static final String BUTTON_PIN_NAME = "GPIO_174";
  private static final String LED_PIN_NAME = "GPIO_34";
  private final int BLINK_INTERVAL = 500;

  private Handler blinkHandler = new Handler();
  private boolean ledState = false;

  //private Gpio buttonA;
  private Gpio ledA;

  // Driver for the GPIO button
  //https://github.com/androidthings/contrib-drivers/tree/master/button
  /*
  Alternatively, you can register a ButtonInputDriver with the system and receive
  KeyEvents through the standard Android APIs:
   */
  //private ButtonInputDriver buttonInputDriverA;

  /**
   * Modify the debounce
   * The button driver applies 100ms of debounce to the input by default to avoid multiple event
   * triggers that can be caused by the mechanical switch inside a button "bouncing" as it opens
   * and closes. You can tune this value with the setDebounceDelay() method. Try out some different
   * values to see how the behavior of the code changes. What happens when you disable
   * debounce altogether?
   */

  // Only Button from https://github.com/androidthings/contrib-drivers/tree/master/button
  // has setDebounceDelay()
  private Button buttonAWithDebounceDelay;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    PeripheralManagerService service = new PeripheralManagerService();
    Timber.d("Available GPIO: %s", service.getGpioList());

    try {
      //Create GPIO connection
      //buttonA = service.openGpio(BUTTON_PIN_NAME);
      //buttonInputDriverA =
      //    new ButtonInputDriver(BUTTON_PIN_NAME, Button.LogicState.PRESSED_WHEN_LOW,
      //        KeyEvent.KEYCODE_SPACE);

      //register with the framework
      //buttonInputDriverA.register();

      buttonAWithDebounceDelay = new Button(BUTTON_PIN_NAME, Button.LogicState.PRESSED_WHEN_LOW);
      buttonAWithDebounceDelay.setDebounceDelay(100);
      buttonAWithDebounceDelay.setOnButtonEventListener(onButtonEventListener);

      ledA = service.openGpio(LED_PIN_NAME);

      //configure as an input, trigger events on every change
      //buttonA.setDirection(Gpio.DIRECTION_IN);
      ledA.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

      //buttonA.setEdgeTriggerType(Gpio.EDGE_BOTH);

      //value is true when the pin is LOW
      //buttonA.setActiveType(Gpio.ACTIVE_LOW);

      //Register the button event callback
      //buttonA.registerGpioCallback(gpioCallback);
    } catch (IOException e) {
      Timber.e("Error opening GPIO", e);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    //close button
    //if (buttonA != null) {
    //  buttonA.unregisterGpioCallback(gpioCallback);
    //
    //  try {
    //    buttonA.close();
    //  } catch (IOException e) {
    //    Timber.e("Error when closing GPIO", e);
    //  }
    //}

    //close led
    if (ledA != null) {
      try {
        ledA.close();
      } catch (IOException e) {
        Timber.e(e);
      }
    }

    //if (buttonInputDriverA != null) {
    //  buttonInputDriverA.unregister();
    //
    //  try {
    //    buttonInputDriverA.close();
    //  } catch (IOException e) {
    //    Timber.e(e);
    //  }
    //}

    if (buttonAWithDebounceDelay != null) {
      try {
        buttonAWithDebounceDelay.close();
      } catch (IOException e) {
        Timber.e(e);
      }
    }

    blinkHandler.removeCallbacks(blinkRunnable);
  }

  // for ButtonInputDriver
  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_SPACE) {
      // turn on the LED A
      setLedValue(true);
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  // for ButtonInputDriver
  @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_SPACE) {
      //Turn off the LED
      setLedValue(false);
      return true;
    }

    return super.onKeyUp(keyCode, event);
  }

  private GpioCallback gpioCallback = new GpioCallback() {
    @Override public boolean onGpioEdge(Gpio gpio) {
      try {
        Timber.i("GPIO hanged, button %b", gpio.getValue());
        boolean buttonValue = gpio.getValue();
        ledA.setValue(buttonValue);
      } catch (IOException e) {
        Timber.e("Error reading GPIO", e);
      }
      //return true to keep callback active
      return true;
    }
  };

  private Button.OnButtonEventListener onButtonEventListener = new Button.OnButtonEventListener() {
    @Override public void onButtonEvent(Button button, boolean pressed) {
      if (pressed) {
        blinkHandler.post(blinkRunnable);
      } else {
        blinkHandler.removeCallbacks(blinkRunnable);
      }

      //try {
      //  ledA.setValue(pressed);
      //} catch (IOException e) {
      //  Timber.e(e);
      //}
    }
  };

  private void setLedValue(boolean value) {
    try {
      ledA.setValue(value);
    } catch (IOException e) {
      Timber.e("Error updating GPIO value", e);
    }
  }

  private Runnable blinkRunnable = new Runnable() {
    @Override public void run() {
      if (ledA == null) return;

      try {
        ledState = !ledState;
        ledA.setValue(ledState);
        Timber.d("Led state set to %b", ledState);

        // reschedule the same runnable for interval
        blinkHandler.postDelayed(blinkRunnable, BLINK_INTERVAL);
      } catch (IOException e) {
        Timber.e(e);
      }
    }
  };
}
