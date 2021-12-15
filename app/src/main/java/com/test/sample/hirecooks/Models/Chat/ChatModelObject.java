package com.test.sample.hirecooks.Models.Chat;

public class ChatModelObject extends ListObject {

        private Message chatModel;

        public Message getChatModel() {
            return chatModel;
        }

        public void setChatModel(Message chatModel) {
            this.chatModel = chatModel;
        }

        @Override
        public int getType(int userId) {
            if (this.chatModel.getFrom_users_id() == userId) {
                return TYPE_GENERAL_RIGHT;
            } else
                return TYPE_GENERAL_LEFT;
        }
    }