package ristogo.server.storage.kvdb;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/*
 * These interface specifies the methods and fields of an Entity_ that can be handled from LevelDB
 * and treated as an Attribute in the Key Value Datastore.
 * Retention specifies that the annotation should not be removed at runtime
 * Target is the class that the interface should use
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Attribute
{
	public String name() default "";
	public String getter() default "";
	public String setter() default "";
	public boolean isEntity() default false;
}
