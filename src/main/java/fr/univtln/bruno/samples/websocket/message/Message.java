package fr.univtln.bruno.samples.websocket.message;

import fr.univtln.bruno.samples.websocket.model.Person;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * This class describe a message in our simple chat protocol.
 * It is automatically encoded/decoded to JSON with the inner class Message.EncoderDecoder.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Message implements Serializable {
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private Date date;
    private Person sender;
    private String messageContent;

    public static class EncoderDecoder extends AbstractJacksonDecoder.Text<Message> {
    }

}