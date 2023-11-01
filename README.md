# Bunk Beds

A Minecraft mod that makes sleeping in beds push to a stack, enabling you to spawn at old beds.

Minecraft's beds only keep track of your *last* bed location, so if you go out adventuring, sleep for the night, and break your bed, your respawn has now reset to the world spawn. This feels unnecessarily cumbersome, as the player has already identified newer, more convenient valid spawning positions. 

This mod makes player's bed spawns *stack up*, so if you break a newer bed, you'll be spawned at the previous *valid* bed in your spawn stack (the last bed you slept in which still exists in the world). The default stack size limit is 5 beds per player, but this is configurable with Cloth Config installed. Note that the stack continuously prunes invalid beds during operations, so your bed stack should have a minimal number of invalid beds.

Mod name lore:
> After all, what is a bunk bed if not a `Stack<Bed>`
- u/Tec, 2023
