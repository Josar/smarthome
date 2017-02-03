package org.eclipse.smarthome.core.auth;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * @author Mohamadreza Amir Khostevan
 *
 */

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface NoAuthenticationRequired {
}
