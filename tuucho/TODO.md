### Todo:

- test image Model

- TTL
- error when loading image
- registry, add custom fetcher ?

- Animation placeholder / final image ?
- Alternative image (fallback) query fallback local/remote ?

- InteractionLockProtocol.Registry -> how the user can add its own ? example EchoMessageCustomActionDefinition, lock not added to registry
- shadower with flow ?

- do a smart padding object - can do also for size)?
- time to live same as actions, colors, texts reusable
- NavigationLocalDestinationActionMiddleware do finish too, and check if value can have single source with Navigation.Back / Finish

-> close alpha 25

### Need to fix: 
- "credentials-title": { "id": "*credentials-request" } ok, but "credentials-title": "*credentials-request" failed. Look why and fix it. (Aka login page)
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