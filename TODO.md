## In progress

- update doc and close the release

- update Jenkins to check Api
./gradlew updateLegacyAbi
./gradlew checkLegacyAbi

- still need to fix mock server
- Need a way to let core barrel on dev without fail because test lib are not here.
- form field initial value inside state, need to make the matcher for the text and uncomment id update register
- rectifier for response, cf action TypeResponseSchema. can't work with material rectifier

**** ****
- add lock on command (mke it smart and easy for nested lock... and always released, cf throw on network)
- add language management
- add selector management + on text language too (also for dimension/color)
- add bottom/top screen not react to transition

- Improve: update view have a knowledge of previous element and next element to avoid overhear job (aka generate validators)
- Improve: solution for JsonObject low storage update easy ?
