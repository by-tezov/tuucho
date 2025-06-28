# Texts

Defines texts, array of text that can be accessed by reference.

### Properties

- `id`: to extend another id by defining the source. Check the example of refer to 'id' definition.
- `default`: default text mandatory
- `****`: any other language.

. texts can be split in different group, by default all texts are put in common group if no group are defined. All reference that do no explicitly define a group will target the common group
. it can be a simple value if only one language
. id is the key of the text, only source can be defined inside id object iif added

... (TODO expand with example JSON)