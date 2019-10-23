package com.example.pato.model;


public class NotificationPatchModel {

    public String to;
    public Data data = new Data();

    public static class Data{
        public String title;
        public String text;
        public String gcmUid;
        public String writer;
        public String noteVersion;
        public String year;
    }
}
