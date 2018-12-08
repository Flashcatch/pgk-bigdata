package domain.bigdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * SiCalculation Entity.
 *
 * @author Sand
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("serial")
public class SiCalculation implements Serializable {

    private Long groupingSetId;
    private String yearMonth;
    private Long sndCnId;
    private Long rsvCnId;
    private Long sndDpId;
    private Long rsvDpId;
    private Long sndRwId;
    private Long rsvRwId;
    private Long sndStId;
    private Long rsvStId;
    private Long sndOrgId;
    private Long rsvOrgId;
    private Long frId;
    private Long frGroupId;
    private Long isLoad;
    private Long rodId;
    private Long sendKindId;
    private Long parkSign;
    private Long routeSendSign;
    private Long modelPropertyId;
    private Long stId;
    private Long clientId;
    private String vidPodgotovki;
    private Long vidZabrakovki;
    private Long isTechSt;
    private Float aggMedian;

}
