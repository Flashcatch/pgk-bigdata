package dto.bigdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BigDataQueryParamsDto.
 * @author SandQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BigDataQueryParamsDto {

    @ApiModelProperty(value = "Идентификатор метрики")
    private Integer id;
    @ApiModelProperty(value = "Идентификатор РПС из справочника РПС")
    @JsonProperty(value = "rod_id")
    private Integer rodId;
    @ApiModelProperty(value = "Идентификатор обобщенной характеристики из справочника")
    @JsonProperty(value = "property_id")
    private Integer propertyId;
    @ApiModelProperty(value = "Станция отправления")
    @JsonProperty(value = "snd_st_id")
    private Integer sndStId;
    @ApiModelProperty(value = "Станция назначения")
    @JsonProperty(value = "rsv_st_id")
    private Integer rsvStId;
    @ApiModelProperty(value = "Отделение отправления")
    @JsonProperty(value = "snd_dp_id")
    private Integer sndDpId;
    @ApiModelProperty(value = "Отделение назначения")
    @JsonProperty(value = "rsv_dp_id")
    private Integer rsvDpId;
    @ApiModelProperty(value = "Станция операции")
    @JsonProperty(value = "st_id_disl")
    private Integer stIdDisl;
    @ApiModelProperty(value = "Идентификатор груза")
    @JsonProperty(value = "fr_id")
    private Integer frId;
    @ApiModelProperty(value = "Идентификатор группы груза")
    @JsonProperty(value = "gr_id")
    private Integer grId;
    @ApiModelProperty(value = "Идентификатор клиента")
    @JsonProperty(value = "client_id")
    private Integer clientId;
    @ApiModelProperty(value = "Идентификатор грузоотправителя")
    @JsonProperty(value = "snd_org_id")
    private Integer sndOrgId;
    @ApiModelProperty(value = "Идентификатор грузополучателя")
    @JsonProperty(value = "rsv_org_id")
    private Integer rsvOrgId;
    @ApiModelProperty(value = "Признак маршрутной отправки")
    @JsonProperty(value = "route_send_sign")
    private Integer routeSendSign;
    @ApiModelProperty(value = "Вид подготовки")
    @JsonProperty(value = "vid_podgotovki")
    private Integer vidPodgotovki;
    @ApiModelProperty(value = "Вид забраковки")
    @JsonProperty(value = "vid_zabrakovki")
    private Integer vidZabrakovki;
    @ApiModelProperty(value = "Признак технической станции")
    @JsonProperty(value = "is_tech_st")
    private Integer isTechSt;
    @ApiModelProperty(value = "model_property_id")
    @JsonProperty(value = "model_property_id")
    private Integer modelPropertyId;
    @ApiModelProperty(value = "is_load")
    @JsonProperty(value = "is_load")
    private Integer isLoad;

}
