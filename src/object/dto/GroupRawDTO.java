package object.dto;

import java.util.ArrayList;
import java.util.List;

public class GroupRawDTO extends BaseDTO {

    List<GroupInfo> groups = new ArrayList<>();

    public List<GroupInfo> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupInfo> groups) {
        this.groups = groups;
    }

    public static class GroupInfo {

        String gid;
        String name;
        Integer member;
        String key;
        String type;

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getMember() {
            return member;
        }

        public void setMember(Integer member) {
            this.member = member;
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
