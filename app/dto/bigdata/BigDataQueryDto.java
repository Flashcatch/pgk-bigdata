package dto.bigdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BigDataQueryDto {
    @JsonProperty(value = "actual_date")
    private String actualDate;
    @JsonProperty(value = "metrics_blanks")
    private List<BigDataQueryParamsDto> metricsBlanks;
}
