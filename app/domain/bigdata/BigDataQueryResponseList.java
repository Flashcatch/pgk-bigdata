package domain.bigdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author SandQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BigDataQueryResponseList {

    private String actualDate;
    private List<BigDataQueryResponse> metrics;

}
