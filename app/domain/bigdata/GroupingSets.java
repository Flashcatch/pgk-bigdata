package domain.bigdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("serial")
public class GroupingSets implements Serializable {

    long statIndicatorId;
    long groupingSetId;
    int level;

}
