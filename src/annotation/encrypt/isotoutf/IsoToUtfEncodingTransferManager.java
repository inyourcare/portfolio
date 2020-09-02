package annotation.encrypt.isotoutf;

import com.nomad.zaksim.msg.ExceptionCode;
import com.nomad.zaksim.util.CommonUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

@Component
public class IsoToUtfEncodingTransferManager {

    public void transfer(Object[] state, String[] propertyNames, Object entity) {
        ReflectionUtils.doWithFields(entity.getClass(), field -> transferField(field, state, propertyNames), IsoToUtfEncryptionUtils::isFieldEncrypted);
    }

    private void transferField(Field field, Object[] state, String[] propertyNames) {
        int propertyIndex = IsoToUtfEncryptionUtils.getPropertyIndex(field.getName(), propertyNames);
        Object currentValue = state[propertyIndex];
        if (currentValue == null)
            return;
        if (!(currentValue instanceof String)) {
            throw new IllegalStateException(ExceptionCode.ISO_NON_STRING_FIELD.getMessage());
        }
        state[propertyIndex] = CommonUtil.encryptingIsoToUtf(currentValue.toString());
    }


    //reverseTransfer
    public void reverseTransfer(Object entity) {
        ReflectionUtils.doWithFields(entity.getClass(), field -> reverseTransferField(field, entity), IsoToUtfEncryptionUtils::isFieldEncrypted);
    }

    private void reverseTransferField(Field field, Object entity) {
        field.setAccessible(true);
        Object value = ReflectionUtils.getField(field, entity);
        if (value == null)
            return;
        if (!(value instanceof String)) {
            throw new IllegalStateException(ExceptionCode.ENCRYPTED_NON_STRING_FIELD.getMessage());
        }
        ReflectionUtils.setField(field, entity, CommonUtil.encryptingIsoToUtf(value.toString()));
    }
}
