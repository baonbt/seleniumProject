package util.resource;

import com.sun.jna.Platform;
import ducky.java.hardware.HardwareAbstractionLayer;
import ducky.java.hardware.linux.LinuxHardwareAbstractionLayer;
import ducky.java.hardware.mac.MacHardwareAbstractionLayer;
import ducky.java.hardware.windows.WindowsHardwareAbstractionLayer;
import ducky.java.software.OperatingSystem;
import ducky.java.software.linux.LinuxOperatingSystem;
import ducky.java.software.mac.MacOperatingSystem;
import ducky.java.software.windows.WindowsOperatingSystem;

public class SystemInfo {

    private OperatingSystem os = null;
    private HardwareAbstractionLayer hLayer = null;
    private final PlatformEnum platformEnum;

    {
        if (Platform.isWindows()) {
            this.platformEnum = PlatformEnum.WINDOWS;
        } else if (Platform.isLinux()) {
            this.platformEnum = PlatformEnum.LINUX;
        } else if (Platform.isMac()) {
            this.platformEnum = PlatformEnum.MACOSX;
        } else {
            this.platformEnum = PlatformEnum.UNKNOWN;
        }
    }

    public OperatingSystem getOperatingSystem() {
        if (this.os == null) {
            switch (this.platformEnum) {
                case WINDOWS:
                    this.os = new WindowsOperatingSystem();
                    break;
                case LINUX:
                    this.os = new LinuxOperatingSystem();
                    break;
                case MACOSX:
                    this.os = new MacOperatingSystem();
                    break;
                default:
                    throw new RuntimeException("Operating system not supported: " + Platform.getOSType());
            }
        }
        return this.os;
    }

    public HardwareAbstractionLayer getHardware() {
        if (this.hLayer == null) {
            switch (this.platformEnum) {
                case WINDOWS:
                    this.hLayer = new WindowsHardwareAbstractionLayer();
                    break;
                case LINUX:
                    this.hLayer = new LinuxHardwareAbstractionLayer();
                    break;
                case MACOSX:
                    this.hLayer = new MacHardwareAbstractionLayer();
                    break;
                default:
                    throw new RuntimeException("Operating system not supported: " + Platform.getOSType());
            }
        }
        return this.hLayer;
    }
}
