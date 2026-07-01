package io.turismo.backend.config;

import tools.jackson.core.JsonGenerator;
import org.locationtech.jts.geom.Point;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class PointToJsonSerializer extends ValueSerializer<Point> {
    @Override
    public void serialize(Point point, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (point == null) {
            gen.writeNull();
            return;
        }

        gen.writeStartObject();
        gen.writeStringProperty("type", "Point");

        gen.writeArrayPropertyStart("coordinates");
        gen.writeNumber(point.getX());
        gen.writeNumber(point.getY());
        gen.writeEndArray();

        gen.writeEndObject();
    }
}