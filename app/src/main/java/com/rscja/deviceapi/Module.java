package com.rscja.deviceapi;

import com.rscja.deviceapi.exception.ConfigurationException;

/* loaded from: classes.dex */
public class Module {
    private static Module a = null;
    protected a config;

    private Module() {
        try {
            this.config = a.d();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Module getInstance() throws ConfigurationException {
        Module module;
        synchronized (Module.class) {
            if (a == null) {
                a = new Module();
            }
            module = a;
        }
        return module;
    }

    protected DeviceAPI getDeviceAPI() {
        return DeviceAPI.a();
    }

    public boolean powerOn(int module) {
        if (getDeviceAPI().ModulePowerOn(this.config.i(), module) != -1) {
            return true;
        }
        return false;
    }

    public boolean powerOff(int module) {
        if (getDeviceAPI().ModulePowerOff(this.config.i(), module) != -1) {
            return true;
        }
        return false;
    }

    public boolean uartSwitch(int module) {
        if (getDeviceAPI().UartSwitch(this.config.i(), module) != -1) {
            return true;
        }
        return false;
    }
}
