package com.test.sample.hirecooks.Models.Chat;

public abstract class ListObject {
        public static final int TYPE_DATE = 2;
        public static final int TYPE_GENERAL_RIGHT = 0;
        public static final int TYPE_GENERAL_LEFT = 1;
        public static final int TYPE_CARDVIEW_GRID = 0;
        public static final int TYPE_CARDVIEW_HORIZONTAL = 1;

        abstract public int getType(int userId);
    }