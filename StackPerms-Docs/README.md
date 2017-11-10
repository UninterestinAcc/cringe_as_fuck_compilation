# Documentation for StackPerms.
*The Reborncraft implementation of permissions.*

## About
StackPerms was created in frustration against hard-to-use or now-deprecated permissions plugins. It arranges players into groups, and groups are collected together to form stacks. Players can have (in theory) unlimited groups from different stacks but only 1 group from each stack.

## Commands
* **/stackperms create** - Starts an instance of the management web interface.
* **/stackperms destroy** - Kills an instance of the management web interface.
* **/stackperms addtime** - Resets the expiration time of the management web interface to an hour.
* **/stackperms reload** - Reloads configurations from disk.
* **/promote player group** - Promotes the player of that name to the new group if it's higher than something the player already possesses.
* **/chargeback player** - Runs a chargeback clean up on the player, resetting all prefixes, suffixes, groups and permissions the player has.

## Terms
Type | Can have | Description
:--: | :--: | ----
Stack | Groups, the weight of the stack and a default group | The stack collects groups together to form an easy-to-manage ladder. Stacks are arranged by weight in ascending order, meaning the stack with the lowest weight (ie. 1) takes precedence over another stack with a higher weight (ie. 2). Groups can also be arranged in order inside the stack into ranks, which are ordered like weights. The *fixes of the player are taken from the highest ranking group inside the stack.
Group | Prefixes, suffixes and permissions | A group contains permissions, prefixes and suffixes which the Stacks sort.
Player | Prefixes, suffixes, permissions and groups | Players can have groups that override default groups. The *fixes of a player goes in front of all the stacks. So someone in multiple groups can have prefixes like `[PlayerPrefix] [LowestWeighingStackPrefix] [HigherWeighingStackPrefix]` and so on...

## Management Interface
The management interface is a friendly web interface which is quite easily understood and simple.

*To be completed.*