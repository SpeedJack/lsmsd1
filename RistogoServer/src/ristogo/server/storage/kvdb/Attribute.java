package ristogo.server.storage.kvdb;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Attribute
{
	public String name() default "";
	public String getter() default "";
	public String setter() default "";
	public boolean isEntity() default false;
}
