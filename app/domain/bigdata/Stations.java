package domain.bigdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Stations entity.
 * @author SandQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("serial")
public class Stations implements Serializable {

    private Long stationId;
    private Long rwId;
    private Long dpId;

}
