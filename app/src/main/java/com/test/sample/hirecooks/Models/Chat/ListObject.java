package com.test.sample.hirecooks.Models.Chat;

public abstract class ListObject {
        public static final int TYPE_DATE = 2;
        public static final int TYPE_GENERAL_RIGHT = 0;
        public static final int TYPE_GENERAL_LEFT = 1;

        abstract public int getType(int userId);
    }