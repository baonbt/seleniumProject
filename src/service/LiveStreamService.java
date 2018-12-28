package service;

import object.dto.LiveStreamDTO;
import util.common.DataUtil;
import java.util.ArrayList;
import java.util.List;

public class LiveStreamService extends BaseService {
    public List<String> getLiveStreamLink() {
        List<String> liveStreamLink = new ArrayList<>();
        liveStreamLink.add("");
        return liveStreamLink;
    }

    public LiveStreamDTO sendAndReceiveLiveStreamDTO(LiveStreamDTO liveStreamDTO, String serverUrl) throws Exception {
        LiveStreamDTO result = (LiveStreamDTO) DataUtil.jsonStringToObject(
                sendPostRequest(serverUrl, DataUtil.objectToJsonString(liveStreamDTO)).getMessage(),
                LiveStreamDTO.class);

        return result;
    }
}
