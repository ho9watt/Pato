package com.example.pato.model;

import java.util.HashMap;
import java.util.Map;


public class BoardModel {

    public static class Board {
        public String title;
        public String content;
        public long timestamp;
        public String uid;
        public String nickname;
        public String category;
        public int replyscount;
        public int readcount;
        public Map<String,Object> likeUsers = new HashMap<>();
        public String bI;
        public String bI2;
        public String bI3;
        public String bI4;
        public String bI5;
        public String bIS;
        public String bIS2;
        public String bIS3;
        public String bIS4;
        public String bIS5;
    }

    public static class Reply{
        public String content;
        public long timestamp;
        public String uid;
        public String nickname;
        public boolean removereplys;
        public boolean rrp;
        public long part;
        public boolean rpEmpty;
        public String opname;
        public String pId;
    }

    public static class ReReplys{
        public String nickname;
        public Object timestamp;
        public String content;
        public String uid;
        public String opname;
        public int userLv;
    }
}
