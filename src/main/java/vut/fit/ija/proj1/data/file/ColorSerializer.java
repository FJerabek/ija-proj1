package vut.fit.ija.proj1.data.file;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ColorSerializer extends StdSerializer<Color> {

    public ColorSerializer() {
        this(null);
    }

    public ColorSerializer(Class<Color> t) {
        super(t);
    }

    @Override
    public void serialize(Color color, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String hex = String.format( "#%02x%02x%02x", (int) color.getRed() * 255, (int) color.getGreen() * 255, (int) color.getBlue() * 255);
        gen.writeString(hex);
    }
}
