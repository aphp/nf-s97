package fr.aphp.nf.s97.parser;

public interface Element
{

   String name();

   String tag();

   Element value(String value);

   Recipient on(Recipient current);

}
