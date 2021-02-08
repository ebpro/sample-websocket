package fr.univtln.bruno.samples.websocket;

import fr.univtln.bruno.samples.websocket.model.Person;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PersonTest {

    @Test
    public void AuteurCreationTest() {
        long id = 1;
        String lastname = "Doe", firstname = "John";
        Person auteur = Person.builder()
                .id(id)
                .lastname(lastname)
                .firstname(firstname)
                .build();
        assertThat(auteur, allOf(hasProperty("lastname", is(lastname)),
                hasProperty("firstname", is(firstname)
                )));
    }

}
