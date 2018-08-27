package module.infosolutions.others.models;

/**
 * Created by R D Mishra on 15-08-2017.
 */

public class NotificationModel {

    private String notificationLabel;

    public NotificationModel(String notificationLabel) {
        this.notificationLabel = notificationLabel;
    }

    public NotificationModel() {
    }

    public String getNotificationLabel() {
        return notificationLabel;
    }

    public void setNotificationLabel(String notificationLabel) {
        this.notificationLabel = notificationLabel;
    }
}
