## In progress

- refactor -> encoder with jsonElement
    - Linear Layout
  

    - Button, Label
    - Texts / Colors / Dimenions
    - Check all conform, check what is not done is no subset then do decoder

- issue, on contents, there is no subset... can't rectifie label... -> ok, will be done on decode. 
Only issue is this element won't be targetable on the "fly"


- quick button / label

- do one ui page
- add the bottom bar + some element


- do the refactor of parser to work only with json element
- replace retrofit with KTOR and pure kotlin lib
- add behavior 
- ttl + purge versioning (config et page si pas en cache)

## Improve
- decoder remove specialisation and recurse look on type to resolve text, color, dim, component, etc... should be possible
- encoder refactor to work on JsonElement data side (Name key etc...)

- then have only what need on domain side -> surely need material when behavior target component, etc not on the current page 
to avoid fetch from db (or do not preload in material but fetch the database only when needed?
- Make it extensible by module feature
- add comprehensible warning and error why the json parsing is not correct