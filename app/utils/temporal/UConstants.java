package utils.temporal;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author Denis Danilin | denis@danilin.name
 * 02.10.2017 17:40
 * core-router ☭ sweat and blood
 * 
 */
public interface UConstants {
    ZoneId utcZoneId = ZoneId.of("UTC");

    TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

}
