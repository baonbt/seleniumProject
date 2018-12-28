package bot.base;

import ducky.java.hardware.HardwareAbstractionLayer;
import ducky.java.software.OSFileStore;
import ducky.java.software.OperatingSystem;
import ducky.java.utils.FormatUtil;
import object.dto.ResourceDTO;
import service.ServerService;
import util.resource.SystemInfo;
import util.resource.Variable;

import static util.common.Constant.COMMON.RESOURCE_REPORT_SLEEPING_TIME;
import static util.common.Constant.SERVER.SERVER_REPORT_RESOURCE;

public class ResourceReportThread extends Thread {

    private SystemInfo si;
    private OperatingSystem os;
    private HardwareAbstractionLayer hal;

    @Override
    public void run() {
        while (true) {
            try {
                si = new SystemInfo();
                os = si.getOperatingSystem();
                hal = si.getHardware();

                if (hal.getProcessor() != null) {
                    Variable.CPU = FormatUtil.round((float) hal.getProcessor().getSystemCpuLoad(), 3);
                    Variable.CPUSTATUS = FormatUtil.formatHertz(hal.getProcessor().getVendorFreq());
                }

                if (hal.getMemory() != null) {
                    long total = hal.getMemory().getTotal();
                    long available = hal.getMemory().getAvailable();
                    long used = total - available;
                    Variable.RAM = FormatUtil.round((float) used / total, 3);
                    Variable.RAMSTATUS = FormatUtil.formatBytes(used) + "/" + FormatUtil.formatBytes(total);
                }

                double totalDisk = 0.0;
                if (hal.getFileStores() != null) {
                    for (OSFileStore osFileStore : hal.getFileStores()) {
                        totalDisk += osFileStore.getTotalSpace();
                    }
                }

                ResourceDTO resourceDTO = new ResourceDTO();
                if (Variable.RAMSTATUS.split("/")[0].contains(" MB")) {
                    resourceDTO.setRam(1);
                } else {
                    resourceDTO.setRam(Double.valueOf(Variable.RAMSTATUS.split("/")[0].replaceAll(" GB", "")));
                }

                resourceDTO.setMaxRam(Double.valueOf(Variable.RAMSTATUS.split("/")[1].replaceAll(" GB", "")));
                resourceDTO.setCpuName(hal.getProcessor().getName());
                resourceDTO.setCpu(Variable.CPU);
                resourceDTO.setDiskSize((int) (totalDisk / 1048576L));
                resourceDTO.setOperatingSystem(os.getManufacturer() + " " + os.getFamily() + " " + os.getVersion());

                ServerService.sendDTOToServer(SERVER_REPORT_RESOURCE, resourceDTO);

                Thread.sleep(RESOURCE_REPORT_SLEEPING_TIME);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR getting resource info!");
            }
        }
    }
}
