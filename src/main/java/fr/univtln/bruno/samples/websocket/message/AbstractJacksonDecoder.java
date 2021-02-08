package fr.univtln.bruno.samples.websocket.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This class is a generic abstract default encoder/decoder that used jackson to encode/decoded websocket message in JSON.
 */
@SuppressWarnings("EmptyMethod")
@Log
abstract class AbstractJacksonDecoder<T> {

    private static final ObjectMapper mapper = new ObjectMapper();
    protected Class<T> objectClass;

    private AbstractJacksonDecoder() {
        ParameterizedType thisClass = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type t = thisClass.getActualTypeArguments()[0];
        if (t instanceof Class) {
            objectClass = (Class<T>) t;
        } else if (t instanceof ParameterizedType) {
            objectClass = (Class<T>) ((ParameterizedType) t).getRawType();
        }
    }

    //This method is defined as a default handler for subclasses
    @SuppressWarnings("EmptyMethod")
    public void init(EndpointConfig endpointConfig) {
        // This method is called when de encoder/decoder is created.
        // Nothing special to do here.
    }

    //This method is defined as a default handler for subclasses
    @SuppressWarnings("EmptyMethod")
    public void destroy() {
        // This method is called when the encoder/decoder is about to be destroyed.
        // Nothing special to do here.
    }

    public static class Text<T> extends AbstractJacksonDecoder<T> implements Decoder.Text<T>, Encoder.Text<T> {

        @Override
        public T decode(String s) throws DecodeException {
            try {
                return mapper.readValue(s, objectClass);
            } catch (IOException e) {
                throw new DecodeException(s, "decode json error", e);
            }
        }

        @Override
        public String encode(T t) throws EncodeException {
            try {
                return mapper.writeValueAsString(t);
            } catch (JsonProcessingException e) {
                throw new EncodeException(t, "JSON Encoding Exception " + e.getLocalizedMessage());
            }
        }

        @Override
        public boolean willDecode(String s) {
            try {
                mapper.readTree(s);
            } catch (IOException e) {
                log.warning("invalid json" + e.getLocalizedMessage());
                return false;
            }
            return true;
        }
    }

    public static class TextStream<T> extends AbstractJacksonDecoder<T> implements Decoder.TextStream<T>, Encoder.TextStream<T> {

        @Override
        public T decode(Reader reader) throws IOException {
            return mapper.readValue(reader, objectClass);
        }

        @Override
        public void encode(T t, Writer writer) throws IOException {
            mapper.writeValue(writer, t);
        }
    }

}