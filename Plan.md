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

You start at 0 for all the resources except for a singular worker
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
|                         | Earth | Mars | Asteroid belt | Jupiter | Saturn | Uranus | Oort cloud | Planet X | Proxima centauri b |
|-------------------------|-------|------|---------------|---------|--------|--------|------------|--------------|--------------------|
| Population cap          | 10    | 20   | 40            | 80      | 200    | 300    | 1 000      | 3 000        | 10 000             |
| Stellar reserves needed | 0     | 50   | 300           | 1 000   | 5 000  | 10 000  | 50 000     | 100 000      | 500 000            |
| Soldiers needed         | 0     | 0    | 0             | 0       | 0      | 0      | 500        | 1 000        | 5 000              |

|                         | Ross 128b | Hoth      | Teth      | Gliese x7x | Groza-S   | Agamar     | Wayland    | SR-25      | Awajiba    |
|-------------------------|-----------|-----------|-----------|------------|-----------|------------|------------|------------|------------|
| Population cap          | 15 000    | 20 000    | 30 000    | 40 000     | 50 000    | 60 000     | 80 000     | 80 000     | 80 000     |
| Stellar reserves needed | 1 000 000 | 2 000 000 | 3 000 000 | 4 000 000  | 6 000 000 | 10 000 000 | 20 000 000 | 40 000 000 | 69 420 000 |
| Soldiers needed         | 10 000    | 12 000    | 20 000    | 25 000     | 33 000    | 40 000     | 55 000     | 60 000     | 65 000     |
## Professions
Worker: 5GJ/s (i.e 5GW) + 0.1 stellar reserve/s

Soldier: literally just for conquering planets

Doctors: +0.1 population/s from the base rate of 0.1 population/s
 - population starts out as "unemployed"

It requires 1TJ to switch anyone's occupation

# Random events and such
Every time you invade a planet, there is a random chance for a special event to happen

10% chance for each (i.e 20% in total for some special event)

"The alien forces were caught off guard by your military strategy" and then all the amounts needed are reduced by a random amount 10% to 50%

"The alien forces were underestimated and a bloody battle ensued" and then all the amounts needed are increased by a random amount 10% to 50%


Also, around every minute (i.e since 10fps, that would be 1/600 chance per frame) there will be an alien gift or attack

The magnitude of each gift or attack will be from 2% to 10% of the stellar reserves needed to conquer the next planet

If you don't have enough stellar reserve then you die

However, there is a 10% chance that a gift, instead of being a regular gift, will be a "pity gift":
> The aliens, living in their 5D universe, take pity on you by giving you a pity gift of 1 stellar reserve. Sucks to suck.

The faster you conquer planets, the less the chance of gifts and the more the chance of attack

# GUI deisgn and such
Start game --> name select and such --> main menu

Main menu has two options to go to the population menu or the planet map

Main menu:
- Energy and stellar reserves counter
- Energy per second and population growth rate
- Population counter and the professions

Population menu:
- Counter of how many workers, soldiers, doctors and unemployed
- Prompts you to choose two professions and a number, and converts that many of the first profession into the second profession
   - this requires energy of course

Planet map:
- Map of all the planets
- Prompts the user to enter if they would like to conquer the next planet

# Docs
## Ticks
Short tick (0.1s)
- update rates (i.e population growth rate, which is tied to number of doctors)
- random chance to have a gift/attack

Long tick (10s)
- update 

# jerry li's suggestion
jerry li make very suggestive noise
he suggests that we make something that the player can do while they wait for their resources
so basically we have a mine menu and you can every 5 seconds press m to mine and get 1% of the stellar reserves required for the next planet
and the 5 seconds only counts down when you are actually in the menu so that
- easier to code
- the ostensible reason is that "the workers only work when you are watching them"