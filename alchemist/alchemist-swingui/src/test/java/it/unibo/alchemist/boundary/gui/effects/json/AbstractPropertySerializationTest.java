package it.unibo.alchemist.boundary.gui.effects.json;

import com.google.gson.Gson;
import javafx.beans.property.Property;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

/**
 * Abstract class that provides a common base of methods for properties
 * serialization test.
 */
public abstract class AbstractPropertySerializationTest {

    /** The {@link Gson} object used for serialization. */
    protected static final Gson GSON = EffectSerializer.getGSON();

    /**
     * Tests (de)serialization with default Java serialization engine.
     * 
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    public abstract void testJavaSerialization() throws Exception;

    /**
     * Tests (de)serialization with Google Gson serialization engine.
     * 
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    public abstract void testGsonSerialization() throws Exception;

    /**
     * Method that generate {@link org.junit.jupiter.api.Assertions#assertTrue(boolean)} messages.
     * 
     * @param <T>
     *            the class wrapped by this property
     * 
     * @param origin
     *            the original {@link Property}
     * @param deserialized
     *            the deserialized {@link Property}
     * @return the message of test fail
     */
    protected <T> String getMessage(final Property<T> origin, final Property<T> deserialized) {
        if (origin == null) {
            return "Original property is null";
        }

        if (deserialized == null) {
            return "Deserialized property is null";
        }

        return "Property \"" + origin.getName() + ": " + origin.getValue() + "\" is different from property \"" + deserialized.getName()
                + ": " + deserialized.getValue() + "\"";
    }

    /**
     * Returns the {@link Gson} {@link Type}.
     * 
     * @return the Gson type for the tested class
     */
    protected abstract Type getGsonType();

}
