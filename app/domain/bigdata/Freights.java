package domain.bigdata;

import java.io.Serializable;

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
@SuppressWarnings("serial")
public class Freights implements Serializable {

    private Long key;
    private Long group;

}
