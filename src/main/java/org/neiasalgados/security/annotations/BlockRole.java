package org.neiasalgados.security.annotations;

import org.neiasalgados.domain.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BlockRole {
    UserRole[] allowedRoles() default {};
}
