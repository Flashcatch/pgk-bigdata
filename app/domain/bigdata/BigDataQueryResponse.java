package domain.bigdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SandQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BigDataQueryResponse {

    private Integer id;
    private Double duration;
    @JsonProperty("snd_st_id")
    private Integer sndStId;
    @JsonProperty("rsv_st_id")
    private Integer rsvStId;
    @JsonProperty("rod_id")
    private Integer rodId;
    @JsonProperty("route_send_sign")
    private Integer routeSendSign;
    private String exception;

}
