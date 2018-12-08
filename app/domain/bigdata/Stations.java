package domain.bigdata;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
