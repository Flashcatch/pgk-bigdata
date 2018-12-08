package domain.bigdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author SandQ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AttributeList implements Serializable {

    private long groupingSetId;
    private String sqlCalcName;

}
