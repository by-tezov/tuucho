## In progress

- Then on action pass the lock (sendForm or navigation stuff, canBeRelease to false...)
- 
- Error when Compose enter in the game

./gradlew :app:jvm:run

*** ***
- still need to fix mock server
- form field initial value inside state, need to make the matcher for the text and uncomment id update register
    - fix state error with "" or just string ?
- rectifier for response, cf action TypeResponseSchema. can't work with material rectifier
- back button managed

**** ****
- add lock on command (mke it smart and easy for nested lock... and always released, cf throw on network)

- add bottom/top screen not react to transition
- Improve: update view have a knowledge of previous element and next element to avoid overhear job (aka generate validators)
- Improve: solution for JsonObject low storage update easy ?
- find a solution for share mock test instead of duplicated code