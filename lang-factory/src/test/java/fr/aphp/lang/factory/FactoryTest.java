package fr.aphp.lang.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class FactoryTest
{

   @Test
   public void SimpleTypeTest(){

      final var st = new SimpleType();
      assertEquals("SimpleType", st.echo());
   }

   @Test
   public void OtherTypeTest(){

      final var st = new OtherType();
      assertEquals("OtherType", st.echo());
   }

   @Test
   public void SimpleFactoryTest(){

      final Factory<SimpleType> f = () -> new SimpleType();
      assertEquals("SimpleType", f.create().echo());
   }

   @Test
   public void OtherFactoryTest(){

      final Factory<OtherType> f = () -> new OtherType();
      assertEquals("OtherType", f.create().echo());
   }

   @Test
   public void ComplexFactoryTest(){

      final Factory<ComplexType> f = () -> new ComplexType("c");
      assertEquals("ComplexType(c)", f.create().echo());
   }

   @Test
   public void DynamicFactoryTest(){
      final DynamicFactory<String, BaseType> factories = new DynamicFactory<>();
      factories.putAll(Map.of(
         //default constructor
         "s", () -> new SimpleType(), "o", () -> new OtherType(),
         //constructor with param
         "c", () -> new ComplexType("c"), "d", () -> new ComplexType("d")));

      assertEquals("SimpleType", factories.create("s").echo());
      assertEquals("OtherType", factories.create("o").echo());

      assertEquals("ComplexType(c)", factories.create("c").echo());
      assertEquals("ComplexType(d)", factories.create("d").echo());
   }

   @Test
   public void RegisterClassTest(){
      final DynamicFactory<String, BaseType> factories = new DynamicFactory<>();
      factories.register("s", SimpleType.class);
      factories.register("o", OtherType.class);

      assertEquals("SimpleType", factories.create("s").echo());
      assertEquals("OtherType", factories.create("o").echo());
   }

   public static interface BaseType
   {
      default String echo(){
         return getClass().getSimpleName();
      }
   }

   public static class SimpleType implements BaseType
   {}

   public static class OtherType implements BaseType
   {}

   public static class ComplexType implements BaseType
   {
      String foo;

      public ComplexType(final String foo){
         super();
         this.foo = foo;
      }

      @Override
      public String echo(){
         return String.format("%s(%s)", getClass().getSimpleName(), foo);
      }

   }
}
