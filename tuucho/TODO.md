### Todo:

-fix the throw catcher on flow...

- Should be able to pass context coroutine to middleware executor

- cache key must exist on Domain and usable of for middleware

- Animation placeholder / final image ?
- Alternative image (fallback) query fallback local/remote ?

-> close alpha 25

- Then documentation
- do a smart padding object - can do also for size)?
- shadower with flow ?

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