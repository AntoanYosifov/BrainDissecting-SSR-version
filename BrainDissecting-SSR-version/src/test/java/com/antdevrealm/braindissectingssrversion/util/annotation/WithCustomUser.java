package com.antdevrealm.braindissectingssrversion.util.annotation;


import com.antdevrealm.braindissectingssrversion.util.factory.CustomUserSecurityContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomUserSecurityContextFactory.class)
public @interface WithCustomUser {
    String username() default "testUser";
    String email() default "test_user@example.com";
    String[] roles() default {"USER"};
    boolean banned() default false;
}
