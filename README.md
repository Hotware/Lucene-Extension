LuceneBeanExtension
===================

Utility classes for easier storage of beans in Lucene

No need for manual mapping of Beans to Lucene documents.
Let the extension do that for you :).

An example of an annotated class can be found in the tests.

Note: As of now, this extension does _not_ include support for nested hierarchies
as in my opinion these hierarchies tend to overcomplicate most models
and as Lucene is storing all data in a de-normalized way it can be misleading
to use Object Hierarchies.

If you still want to handle hierarchies there is always the option to use custom types.
