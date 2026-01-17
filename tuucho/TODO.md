### Todo:

- TODO process image should return an object or a list
- do the parser Image

- then do the ui-component Image


- make sure I can catch a network error on image fail retrieval side application
- Need to update the config.properties on Jenkins because image endpoint

- time to live same as actions, colors, texts reusable
- NavigationLocalDestinationActionMiddleware do finish too, and check if value can have single source with Navigation.Back / Finish

### Need to fix: 
- redraw trigger is not convenient, maybe add a general purpose event state where view could register to it ?
- contextual register, when outer receive later, the inner did not succeed to register
  - check message of contextual field, they never request update view after click

**** ****
- Shadower
    // do not crash application but 
    // - build a way to inform view the failure (all skimmer)
    // - on back, when it was failure, how to attempt reload the block if back shadower is off ?
- Error when Compose enter in the game
- Add bottom/top screen not react to transition
- Find a solution for share mock test instead of duplicated code