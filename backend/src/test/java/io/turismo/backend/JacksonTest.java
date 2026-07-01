package io.turismo.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.JtsModule;

public class JacksonTest {
    @Test
    public void test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JtsModule());
        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(new Coordinate(1, 2));
        System.out.println(mapper.writeValueAsString(p));
    }
}
