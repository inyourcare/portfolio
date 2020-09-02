package annotation.encrypt.isotoutf;

import com.nomad.zaksim.msg.ExceptionCode;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;

//3
public abstract class IsoToUtfEncryptionUtils {
    public static boolean isFieldEncrypted(Field field) {
        return AnnotationUtils.findAnnotation(field, IsoToUtfEncrypt.class) != null;
    }

    public static int getPropertyIndex(String name, String[] properties) {
        for (int i = 0; i < properties.length; i++) {
            if (name.equals(properties[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException(ExceptionCode.NO_PROPERTY_FOUND_FOR_NAME.getMessage() + " : " + name);
    }
}