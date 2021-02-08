package fr.univtln.bruno.samples.websocket.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Person implements Serializable {
    @EqualsAndHashCode.Include
    long id;

    String firstname;

    String lastname;
}

