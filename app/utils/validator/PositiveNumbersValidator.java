/*
 * Created by IPStarikovskiy
 * Created 01.12.2017
 */

package utils.validator;

import play.data.validation.Constraints;
import play.libs.F;

public class PositiveNumbersValidator extends Constraints.Validator<Object> {

    @Override
    public boolean isValid(Object object) {
        if (object instanceof Number) {
            Number obj = (Number) object;
            return obj.intValue() >= 0;
        } else {
            return false;
        }

    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return F.Tuple("error.number_not_positive", new Object[] {});
    }
}
