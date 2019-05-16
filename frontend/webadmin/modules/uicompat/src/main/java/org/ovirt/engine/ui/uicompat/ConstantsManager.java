package org.ovirt.engine.ui.uicompat;

import com.google.gwt.core.client.GWT;

public abstract class ConstantsManager {

    private static ConstantsManager instance = new GwtConstantsManager();

    public static ConstantsManager getInstance() {
        return instance;
    }

    public static void setInstance(ConstantsManager manager) {
        instance = manager;
    }

    public abstract UIConstants getConstants();
    public abstract UIMessages getMessages();
    public abstract LocalizedEnums getEnums();
    public abstract NextRunFieldMessages getNextRunFieldMessages();

    static class GwtConstantsManager extends ConstantsManager {

        private static UIConstants constants;
        private static UIMessages messages;
        private static LocalizedEnums enums;
        private static NextRunFieldMessages nextRunFieldMessages;

        @Override
        public UIConstants getConstants() {
            if (constants == null) {
                constants = GWT.create(UIConstants.class);
            }
            return constants;
        }

        @Override
        public UIMessages getMessages() {
            if (messages == null) {
                messages = GWT.create(UIMessages.class);
            }
            return messages;
        }

        @Override
        public LocalizedEnums getEnums() {
            if (enums == null) {
                enums = GWT.create(LocalizedEnums.class);
            }
            return enums;
        }

        @Override
        public NextRunFieldMessages getNextRunFieldMessages() {
            if (nextRunFieldMessages == null) {
                nextRunFieldMessages = GWT.create(NextRunFieldMessages.class);
            }
            return nextRunFieldMessages;
        }
    }
}
