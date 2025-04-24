package com.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class UsbSerialProber {
    private final ProbeTable mProbeTable;

    public UsbSerialProber(ProbeTable probeTable) {
        this.mProbeTable = probeTable;
    }

    public static UsbSerialProber getDefaultProber() {
        return new UsbSerialProber(getDefaultProbeTable());
    }

    public static ProbeTable getDefaultProbeTable() {
        ProbeTable probeTable = new ProbeTable();
        probeTable.addDriver(CdcAcmSerialDriver.class);
        probeTable.addDriver(Cp21xxSerialDriver.class);
        probeTable.addDriver(FtdiSerialDriver.class);
        probeTable.addDriver(ProlificSerialDriver.class);
        probeTable.addDriver(Ch34xSerialDriver.class);
        return probeTable;
    }

    public List<UsbSerialDriver> findAllDrivers(UsbManager usbManager) {
        List<UsbSerialDriver> result = new ArrayList<>();
        for (UsbDevice usbDevice : usbManager.getDeviceList().values()) {
            UsbSerialDriver driver = probeDevice(usbDevice);
            if (driver != null) {
                result.add(driver);
            }
        }
        return result;
    }

    public UsbSerialDriver probeDevice(UsbDevice usbDevice) {
        int vendorId = usbDevice.getVendorId();
        int productId = usbDevice.getProductId();
        Class<? extends UsbSerialDriver> driverClass = this.mProbeTable.findDriver(vendorId, productId);
        if (driverClass != null) {
            try {
                Constructor<? extends UsbSerialDriver> ctor = driverClass.getConstructor(UsbDevice.class);
                UsbSerialDriver driver = ctor.newInstance(usbDevice);
                return driver;
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
