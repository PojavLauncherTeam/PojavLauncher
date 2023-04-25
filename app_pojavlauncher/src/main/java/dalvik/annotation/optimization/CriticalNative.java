package dalvik.annotation.optimization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//  Dummy CriticalNative annotation. On devices which dont have it this declaration will prevent errors.
//  On devices that do have it it will be overridden by the system one and work as usual
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface CriticalNative {
}
