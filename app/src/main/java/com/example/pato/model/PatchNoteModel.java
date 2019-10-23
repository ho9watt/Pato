package com.example.pato.model;

import java.util.HashMap;
import java.util.Map;

public class PatchNoteModel {

    public Map<String,String> patchinfo = new HashMap<>();
    public Map<String,Integer> readcount = new HashMap<>();

    public static class readcount{
        public int readcount;
        public int replyscount;
    }

    public static class patchinfo{
        public String title;
        public String timestamp;
        public String version;
    }

    public static class Contents{
        public int uid;
        public String name;
        public String contentS;
        public String contentP;
        public String contentQ;
        public String contentQQ;
        public String contentW;
        public String contentWW;
        public String contentE;
        public String contentEE;
        public String contentR;
        public String contentRR;
        public String status;
    }

    public static class Replys{
        public String nickname;
        public Object timestamp;
        public String content;
        public String uid;
        public Boolean removereplys;
        public String pId;
        public Object part;
        public boolean rpEmpty;
        public boolean rrp;
        public String opname;
    }


}
