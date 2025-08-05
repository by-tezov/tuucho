## In progress

Problem

- state and use case do recursive koin
- url is not enough to identifie a view -> Route
- component retrieve is done on view and navigate (cached, freed, ...)


- add language management
- add selector management + on language too

- Improve: update view have a knowledge of previous element 
and next element to avoid overhear job (aka generate validators)
- Improve: // add option wait all available before to render the view instead of render on the fly
- Check the TODO List

- add config to remove hard string inside di

- When do the ttl stuff, improve the database code. Not readable, not scalable, not maintainable...
- doc for setting

- Fix the monkey navigation crash the onDemand unique constraint -> because on-demand are not
  cancelled when leave the page
  and so requested multiple times -> add proxy client xD

