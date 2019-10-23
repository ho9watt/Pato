package com.example.pato.model;


public class NotificationModel {
    public String to;
    public Notification notification = new Notification();
    public Data data = new Data();

    public static class Notification {
        public String title;
        public String text;
    }

    public static class Data{
        public String title;
        public String text;
        public String gcmUid;
        public String writer;
        public String noteVersion;
        public String year;
    }

}
