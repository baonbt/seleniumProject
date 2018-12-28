package object.dto;

import java.util.ArrayList;
import java.util.List;

public class LiveStreamDTO extends BaseDTO {

    private List<LiveStreamLinkInfo> liveStreamLinkInfos = new ArrayList<>();
    private List<String> subcriberIds = new ArrayList<>();

    public void setLiveStreamLinkInfos(List<LiveStreamLinkInfo> liveStreamLinkInfos) {
        this.liveStreamLinkInfos = liveStreamLinkInfos;
    }

    public List<LiveStreamLinkInfo> getLiveStreamLinkInfos() {
        return liveStreamLinkInfos;
    }

    public static class LiveStreamLinkInfo {
        String liveStreamLink;
        int liveStreamViewerCount;
        /*
         * LIVE STREAM STATUS CODE 1 (SV GỬI VỀ) = CLIENT MỞ TAB MỚI VÀO XEM LIVE
         * LIVE STREAM STATUS CODE 2 (SV GỬI VỀ) = CLIENT ĐÓNG TAB ĐANG XEM LIVE NẾU CÓ
         * LIVE STREAM STATUS CODE 3 (SV GỬI VỀ) = CLIENT MỞ CỬA SỔ MỚI VÀO XEM LIVE
         * đối với code 3: khi client request lên sv nếu 1 live đang cần tăng mắt
         * sv sẽ gửi về ACTION_CODE WATCH_LIVE_STREAM 501, client sẽ mở profile tối ưu cho watch live và vào xem live
         * LIVE STREAM STATUS CODE 4 (LISTENER GỬI LÊN) = LISTENER CANH NOTIFICATION VÀ BÁO LÊN SV KHI CÓ LIVE MỚI
         * LIVE STREAM STATUS CODE 5 (SV GỬI VỀ) = ĐÓNG CỬA SỔ VÀ KẾT THÚC LUỒNG
         * LIVE STREAM STATUS CODE 6 (CLONE GỬI LÊN) = LUỒNG TRỰC TIẾP ĐÃ KẾT THÚC
         * LIVE STREAM STATUS CODE 7 (CLONE GỬI LÊN) = LUỒNG TRỰC TIẾP ĐANG XEM
         * LIVE STREAM STATUS CODE 8 (CLONE GỬI LÊN) = BÁO CÁO LÊN SV MỖI 5P
         * */
        int liveStreamStatusCode;
        /*
         * LIST NHỮNG KÊNH LINK ĐANG LIVE ĐÃ MUA DỊCH VỤ CỦA MÌNH
         * VÌ SAU KHI LVE KẾT THÍCH FB CÓ THỂ SẼ TỰ ĐỘNG ĐẨY SANG LIVE TIẾP THEO
         * CLIENT SỬ DỤNG LIST NÀY ĐỂ CHECK VÀ ĐÓNG NHỮNG TAB KHÔNG PHÙ HỢP
         * */

        public int getLiveStreamStatusCode() {
            return liveStreamStatusCode;
        }

        public void setLiveStreamStatusCode(int liveStreamStatusCode) {
            this.liveStreamStatusCode = liveStreamStatusCode;
        }

        public int getLiveStreamViewerCount() {
            return liveStreamViewerCount;
        }

        public void setLiveStreamViewerCount(int liveStreamViewerCount) {
            this.liveStreamViewerCount = liveStreamViewerCount;
        }

        public String getLiveStreamLink() {
            return liveStreamLink;
        }

        public void setLiveStreamLink(String liveStreamLink) {
            this.liveStreamLink = liveStreamLink;
        }
    }

    public List<String> getSubcriberIds() {
        return subcriberIds;
    }

    public void setSubcriberIds(List<String> subcriberIds) {
        this.subcriberIds = subcriberIds;
    }
}
