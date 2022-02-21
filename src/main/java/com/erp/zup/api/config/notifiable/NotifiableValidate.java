package com.erp.zup.api.config.notifiable;

import jflunt.notifications.Notifiable;
import jflunt.notifications.Notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class NotifiableValidate {

    private List<Notification> notifications = new ArrayList<>();

    public NotifiableValidate() {

    }

    public List<Notification> getNotifications() {
        List<Notification> notifications = this.notifications;
        this.notifications = new ArrayList();
        return Collections.unmodifiableList(notifications);
    }

    public void addNotification(String property, String message) {
        this.notifications.add(new Notification(property, message));
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    public void addNotifications(List<Notification> notification) {
        this.notifications.addAll(notification);
    }

    public void addNotifications(Collection<Notification> notification) {
        this.notifications.addAll(notification);
    }

    public void addNotifications(Notifiable notifiable) {
        this.notifications.addAll(notifiable.getNotifications());
    }

    public void addNotifications(Notifiable... items) {
        Notifiable[] var2 = items;
        int var3 = items.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Notifiable item = var2[var4];
            this.addNotifications(item);
        }

    }

    public boolean isInvalid() {
        return this.notifications.size() > 0;
    }


    public boolean isValid() {
        return !this.isInvalid();
    }
}
