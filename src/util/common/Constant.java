/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.common;

/**
 * @author Thai Tuan Anh
 */
public class Constant {

    public static class DRIVER {

        //        public static final String USER = "reddog";
        public static final String USER = "thereddog";
        //        public static final String USER = "baonb";
//        public static final String USER = "Thai Tuan Anh";
//        public static final String USER = "Administrator";
        public static final String CHROME_FOLDER = "C:\\BotNetLib\\chromedriver.exe";
        public static final String CHROME_PROFILE_FOLDER = "C:\\Users\\" + USER + "\\AppData\\Local\\Google\\Chrome\\User Data\\";
        public static final String FIREFOX_FOLDER = "./geckodriver.exe";
        public static final String MAIN_FOLDER = "C:\\BotNetLib";
        public static final String EXTENSIONS_FOLDER = "extensions";
        public static final String PROTOTYPE_EXT_FOLDER = "gatoasang94";
        public static final String PLAY_LIVE_STREAM_EXT = "E:\\Work\\EXT\\playLS";
        public static final String FACEBOOK_HOME = "https://www.facebook.com/";
        public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
        public static final String CHROME_ORIGIN_ZIP = "C:\\BotNetLib\\chrome_profile_30_5_2018.zip";
        public static final String JAVA_TEMP_FOLDER = "C:\\Users\\" + USER + "\\AppData\\Local\\Temp\\";
    }

    public static class ACTION {

        public static final String NOTHING = "na";

        //SPECIAL ACTION
        public static final int UPDATE_COOKIES = 9999;
        public static final int INITIAL_SETUP = 9001;

        //CUSTOMER ACTION CODE
        public static final int CUSTOMER_FOLLOW = 1001;
        public static final int CUSTOMER_SEE_FIRST = 1002;
        public static final int CUSTOMER_UN_FOLLOW = 1003;
        public static final int CUSTOMER_UN_SEE_FIRST = 1004;


        //RANDOM ACTION
        public static final int INTERTACTION = 201;
        public static final int AUTOTYM = 299;
        public static final int AUTOTYMMFB = 2991;

        //PURPOSE ACTION
        public static final int JOINGROUP = 101;
        public static final int ACCEPT_FRIENDS_REQUEST_BY_QUALITY = 102;
        public static final int SEND_FRIENDS_REQUEST_BY_SUGGEST_AND_QUALITY = 103;
        public static final int SEND_FRIENDS_REQUEST_BY_TARGET = 104;
        public static final int SEND_FRIENDS_REQUEST_BY_GROUP_AND_QUALITY = 105;
        public static final int UPDATE_PROFILE_PICTURE = 106;
        public static final int UPDATE_COVER_PHOTO = 107;
        public static final int UPDATE_STATUS = 108;
        public static final int POST_IMAGE_TO_A_GROUP = 110;
        public static final int POST_LINK_TO_GROUP = 111;
        public static final int INVITE_SUGGEST_FRIENDS_TO_A_GROUP = 112;
        public static final int INVITE_TARGET_FRIEND_TO_A_GROUP = 113;
        public static final int COMMENT_POST_BY_LINK = 114;
        public static final int REACTION_POST_BY_LINK = 115;
        public static final int FANPAGE_REVIEW = 121;
        public static final int CRAWL_POST = 122;
        public static final int POST_LINK_TO_WALL = 116;
        public static final int GET_PROFILE_LINK = 118;
        public static final int SHARE_TO_WALL = 119;
        public static final int SHARE_TO_GROUP = 120;

        //TRUST FRIEND ACTION
        public static final int TF_SEND_REQUEST = 301;
        public static final int TF_ADD_TRUST_FRIEND = 302;
        public static final int TF_SCAN_TRUSTED_FRIEND = 303;
        public static final int GET_ACCOUNT_INFO = 304;
        public static final int TF_ACCEPT_REQUEST = 305;

        //market building sequence action
        public static final int CRAWL_FROM_FRIEND = 401;
        public static final int FRIENDS_FROM_GROUP = 402;
        public static final int FRIENDS_FROM_NEWFEED = 403;
        public static final int FRIENDS_FROM_FANPAGE = 404;
        public static final int GROUPS_FROM_KEY = 405;
        public static final int GROUPS_FROM_DISCOVER = 406;
        public static final int FRIENDS_FROM_WEBSITE = 407;
        public static final int PAGES_FROM_KEY = 408;
        public static final int PAGES_FROM_DISCOVER = 409;
        public static final int EVALUATE_FRIEND = 410;
        public static final int EVALUATE_GROUP = 411;
        public static final int EVALUATE_PAGE = 412;
        public static final int EVALUATE_POST = 413;
        public static final int TERMINATE_RELATION = 414;

        //BOOST ACTION
        public static final int WATCH_LIVE_STREAM = 501;
        public static final int POST_AND_LIVE_STREAM_LISTENER = 502;
        public static final int BOOST_POST = 503;

        //delay time
        public static final int DELAY_FROM = 1000;
        public static final int DELAY_TO = 2000;
        public static final String DEFAULT_REACTION_RATE = "1=10;2=30;3=20;4=20;5=10;6=10";
        public static final int MAXIMUM_SELL_GROUP_POST = 1;
        public static final int MAXIMUM_FRIEND_REQUEST_ACT = 1;
        public static final int MAXIMUM_CRAWL_POST = 5;
        public static final int LOAD_TIME = 120;
        public static final int MAXIMUM_LOOP_CRAWL_FRIENDS_WEBSITE = 100;

        public static final String FB_HOME = "https://www.facebook.com/";
    }

    public static class HTTP {

        public static final String USER_AGENT = "Mozilla/5.0";
        public static final String ACCEPTED_LANGUAGE = "en-US,en;q=0.5";
        public static final String ERROR_CODE_SUCCESS = "0";
        public static final String ERROR_CODE_FAIL = "-1";
        public static final String CONTENT_TYPE = "application/json; charset=utf8";
    }

    public static class SERVER {
        public static final String SERVER_ADDRESS = "http://localhost:8080";
//        public static final String SERVER_ADDRESS = "http://45.77.32.223:8080";

        public static final String SERVER_COMMAND_URL = SERVER_ADDRESS + "/command/normal";
        public static final String SERVER_COMMAND_BUFF_URL = SERVER_ADDRESS + "/command/buff";
        public static final String SERVER_COMMAND_TYM_URL = SERVER_ADDRESS + "/command/tym";
        public static final String SERVER_COMMAND_TYM_TEST_URL = SERVER_ADDRESS + "/command/tymtest";
        public static final String SERVER_COMMAND_LISTENER_URL = SERVER_ADDRESS + "/command/listener";
        public static final String SERVER_COMMENT = SERVER_ADDRESS + "/comment";
        public static final String SERVER_MESSAGE = SERVER_ADDRESS + "/message";
        public static final String SERVER_LOG_BASE = SERVER_ADDRESS + "/log/base";
        public static final String SERVER_CHECKPOINT = SERVER_ADDRESS + "/checkpoint";
        public static final String SERVER_PROFILE_LINK = SERVER_ADDRESS + "/profileLink";
        public static final String SERVER_ACCOUNT_REPORT = SERVER_ADDRESS + "/account/report";
        public static final String SERVER_TF_REQUEST = SERVER_ADDRESS + "/friend/request";
        public static final String SERVER_TF_ADD_TRUST = SERVER_ADDRESS + "/friend/add";
        public static final String SERVER_TF_SCAN = SERVER_ADDRESS + "/friend/scan";
        public static final String SERVER_GET_ACCOUNT_INFO = SERVER_ADDRESS + "/getinfo";
        public static final String SERVER_POST_STATUS = SERVER_ADDRESS + "/status";
        public static final String SERVER_GET_STATUS = SERVER_ADDRESS + "/status";
        public static final String SERVER_CRAWL_FRIEND_RAW = SERVER_ADDRESS + "/crawl/friend/raw";
        public static final String SERVER_CRAWL_GROUP_RAW = SERVER_ADDRESS + "/crawl/group/raw";
        public static final String SERVER_CRAWL_PAGE_RAW = SERVER_ADDRESS + "/crawl/page/raw";
        public static final String SERVER_CRAWL_FRIEND_EVAL = SERVER_ADDRESS + "/crawl/friend/eval";
        public static final String SERVER_REPORT_RESOURCE = SERVER_ADDRESS + "/resource/report";
        public static final String SIMSIMI = SERVER_ADDRESS + "/chatbot/simsimi";
        public static final String BUFF_LISTENER = SERVER_ADDRESS + "/buff/listener";
        public static final String BUFF_LIVE = SERVER_ADDRESS + "/buff/live";
        public static final String BUFF_LISTENER_SETUP = SERVER_ADDRESS + "/buff/setup";
        public static final String RESOURCE_REPORT = SERVER_ADDRESS + "/resource/check";
        public static final String OFFLINE_WATCHER = SERVER_ADDRESS + "/buff/offline";
        public static final String LISTENER_SETTING_LIVE_COMPLETE = SERVER_ADDRESS + "/buff/setting";
        public static final String SERVER_ACCOUNT_REPORT_V2 = SERVER_ADDRESS + "/account/report/v2";
    }

    public static class COMMON {
        public static final String[] SALE_KEY_WORDS = {"sản phẩm", "bán hàng", "khuyến mãi", "mới vui vẻ"};
        public static final long RESOURCE_REPORT_SLEEPING_TIME = 5000;
        public static final long CHECK_RESOURCE_SLEEP_TIME = 10000;
        public static final float LIVESTREAM_PERCENT = 0.7f;

        public static final int ACTION_TYPE_NORMAL = 0;
        public static final int ACTION_TYPE_BUFF = 1;
        public static final int ACTION_TYPE_TYM = 2;
        public static final int ACTION_TYPE_TYM_TEST = 4;
        public static final int ACTION_TYPE_LISTENER = 3;

        public static final int INIT_QUEUE_SIZE = 10;

        public static final String MODE_NORMAL = "normal";
        public static final String MODE_BUFF = "buff";
        public static final String MODE_TYM = "tym";
        public static final String MODE_TYM_TEST = "tymtest";
        public static final String MODE_LISTENER = "listener";
    }

    public static class RESOURCE {
        public static final String RESOURCE_AVAILABLE = "OK";
    }
}
