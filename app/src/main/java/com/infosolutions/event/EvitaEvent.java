package com.infosolutions.event;

/**
 * Created by Noman Khan on 13/09/17.
 */

public class EvitaEvent {

    public static class EventDataSyncToServer {
        private boolean isDataSynced;
        public boolean isDataSynced() {
            return isDataSynced;
        }
        public void setDataSynced(boolean dataSynced) {
            isDataSynced = dataSynced;
        }
    }

}
