/**
 * @author xjerab25
 * File containing deserializer for {@link javafx.scene.paint.Color} class
 */
package vut.fit.ija.proj1.data.file;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Deserializer for {@link Color} class
 */
public class ColorDeserializer extends StdDeserializer<Color> {

    /**
     * Default constructor
     */
    public ColorDeserializer() {
        this(null);
    }

    /**
     * Constructor from supertype
     * @param vc class parameter
     */
    public ColorDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String itemName = node.asText();
        return Color.valueOf(itemName);
    }

}
