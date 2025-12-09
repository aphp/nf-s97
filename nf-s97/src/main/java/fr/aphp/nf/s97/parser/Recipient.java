package fr.aphp.nf.s97.parser;

import java.util.List;

public interface Recipient extends Element
{

   List<Element> getChildren();

   Recipient parent();
}
