package fr.aphp.nf.s97.processor.struct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Element
{
   public static String SEGMENT = "segment";

   public static String REPETITION = "repetition";

   public static String VALUE = "value";

   String tag();

   String name();

   String type();
}
