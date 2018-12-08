package utils;

import java.time.ZoneId;
import java.util.TimeZone;

public interface UConstants {
    ZoneId utcZoneId = ZoneId.of("UTC");

    TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

}
