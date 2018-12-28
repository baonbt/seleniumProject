package object.dto;

import java.util.ArrayList;
import java.util.List;

public class PageRawDTO extends BaseDTO{

    List<PageRawInfo> pages = new ArrayList<>();

    public List<PageRawInfo> getPages() {
        return pages;
    }

    public void setPages(List<PageRawInfo> pages) {
        this.pages = pages;
    }

    public static class PageRawInfo {

        String pid;
        String name;
        String key;
        String type;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
