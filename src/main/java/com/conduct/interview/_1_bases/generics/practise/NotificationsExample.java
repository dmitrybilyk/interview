package com.conduct.interview._1_bases.generics.practise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NotificationsExample {
    public static void main(String[] args) {
        List<EmailNotification> emails = List.of(
                new EmailNotification("Welcome!", 1002),
                new EmailNotification("Invoice ready", 1001)
        );

        List<SmsNotification> sms = List.of(
                new SmsNotification("Code: 123", 1005),
                new SmsNotification("Reminder", 1000)
        );

        NotificationRepository<Notification> repo = new NotificationRepository<>();
        repo.addAll(emails);
        repo.addAll(sms);

        // Sort using custom comparator
        repo.sort(Comparator.comparing(new Function<Notification, String>() {
            @Override
            public String apply(Notification notification) {
                return notification.getMessage();
            }
        }));

        // Map to summaries
        List<String> summaries = repo.mapToSummaries(n -> "Summary: " + n.getMessage());

        summaries.forEach(System.out::println);
    }
}

class EmailNotification extends Notification {
    public EmailNotification(String message, long timestamp) {
        super(message, timestamp);
    }
}

class SmsNotification extends Notification {
    public SmsNotification(String message, long timestamp) {
        super(message, timestamp);
    }
}

class NotificationRepository<T extends Comparable<T>> {

    private final List<T> store = new ArrayList<>();

    public void addAll(Collection<? extends T> comparables) {
        store.addAll(comparables);
    }

    public void sort(Comparator<T> comparator) {
        store.sort(comparator);
    }

    public List<String> mapToSummaries(Function<? super T, String> mapper) {
        return store.stream().map(mapper).collect(Collectors.toList());
    }

    public List<T> getAll() {
        return new ArrayList<>(store);
    }
}

class Notification implements Comparable<Notification> {
    private final String message;
    private final long timestamp;

    public Notification(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }

    @Override
    public int compareTo(Notification other) {
        return Long.compare(this.timestamp, other.timestamp);
    }
}
//
//class NotificationDispatcher {
//    public <T extends Notification> void dispatch(
//            List<? extends T> notifications,
//            NotificationSender<? super T> sender) {
//        for (T notifiation: notifications) {
//            sender.send(notifiation);
//        }
//    }
//}
//
//
//interface NotificationSender<T extends Notification> {
//    void send(T notification);
//}
//
//class EmailNotificationSender implements NotificationSender<EmailNotification> {
//
//    @Override
//    public void send(EmailNotification notification) {
//        System.out.println("sending email notification: " + notification);
//    }
//}
//
//class SmsNotificationSender implements NotificationSender<SmsNotification> {
//    @Override
//    public void send(SmsNotification notification) {
//        System.out.println("Sending SMS: " + notification.getMessage());
//    }
//}
//
//class Notification {
//    private final String message;
//
//    public Notification(String message) {
//        this.message = message;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//}
//
//class EmailNotification extends Notification {
//    public EmailNotification(String message) {
//        super(message);
//    }
//}
//
//class SmsNotification extends Notification {
//    public SmsNotification(String message) {
//        super(message);
//    }
//}


