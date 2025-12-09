package fr.aphp.lang.factory;

@FunctionalInterface
public interface Factory<T>
{
   T create();
}
