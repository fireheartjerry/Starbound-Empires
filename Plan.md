# Detailed plan for the game
You will have a bunch of planets that you need to conquer and the objective is to conquer all the planets

Also you have resources:
  
  Material resources: stellar reserves and energy
   - Energy is used to convert population between the three occupations
   - Stellar reserves is used to capture planets
  
  Human resources: workers, soldiers, doctors, which come from a pool of population
   - Workers are used to increase the rate of energy and stellar reserves production
   - Soldiers are used for conquering planets and you will randomly lose some soldiers each time you conquer a planet
     - if you don't have enough soldiers then game over and your civilization collapses
   - Doctors increase the rate of population growth
   - There is a population cap and you can expand this cap by capturing more planets
   - Population passively grows
## Progression:
Planets: Earth, Mars, Asteroid belt, Jupiter, Saturn, Uranus, Oort cloud, Planet 31337, Proxima Centari b

So you start on Earth with one worker and that is all of your population

Then your population grows and etc

Once you have enough stellar reserves, you can conquer Mars, which increases your population cap, allowing you to get more people to get more resources

The solar system planets (excluding the Oort cloud) only require stellar reserves but no soldiers

Then, for the other ones, much more stellar reserves and many soldiers are required to conquer them

Requirement of stellar reserves grows exponentially

# Stats
## Planets
|                         | Earth | Mars | Asteroid belt | Jupiter | Saturn | Uranus | Oort cloud | Planet 31337 | Proxima centauri b |
|-------------------------|-------|------|---------------|---------|--------|--------|------------|--------------|--------------------|
| Population cap          | 10    | 20   | 40            | 80      | 200    | 300    | 1000       | 3000         | 10000              |
| Stellar reserves needed | 0     | 50   | 150           | 500     | 1000   | 1500   | 10000      | 20000        | 100000             |
| Soldiers needed         | 0     | 0    | 0             | 0       | 0      | 0      | 100        | 1000         | 2000               |
## Professions
Worker: 5GJ/s (i.e 5GW) + 0.1 stellar reserve/s

Soldier: literally just for conquering planets

Doctors: +0.1 population/s from the base rate of 0.1 population/s
 - population starts out as "unemployed"

It requires 1TJ to switch anyone's occupation