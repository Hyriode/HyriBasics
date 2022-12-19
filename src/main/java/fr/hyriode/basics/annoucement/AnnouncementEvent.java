package fr.hyriode.basics.annoucement;

import fr.hyriode.api.event.HyriEvent;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 08/12/2022 at 17:11
 */
public class AnnouncementEvent extends HyriEvent {

    private final UUID sender;
    private final String message;

    public AnnouncementEvent(UUID sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public UUID getSender() {
        return this.sender;
    }

    public String getMessage() {
        return this.message;
    }

}
