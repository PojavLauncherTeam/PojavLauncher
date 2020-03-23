package net.kdt.pojavlaunch.update;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface UpdateTarget {
    String from();
	String to();
}
