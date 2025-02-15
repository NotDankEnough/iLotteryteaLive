package kz.ilotterytea.bot.entities.events.subscriptions;

import jakarta.persistence.*;
import kz.ilotterytea.bot.entities.events.Event;
import kz.ilotterytea.bot.entities.users.User;

/**
 * Entity for event subscription.
 *
 * @author ilotterytea
 * @version 1.6
 */
@Entity
@Table(name = "event_subscriptions")
public class EventSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false, updatable = false)
    private Event event;

    public EventSubscription() {
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
