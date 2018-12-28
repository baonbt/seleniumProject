package object.dto;

import java.util.List;

public class CustomerInfoDTO {
    List<String> customerIds;

    public List<String> getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(List<String> customerIds) {
        this.customerIds = customerIds;
    }
}
