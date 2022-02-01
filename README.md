PowerNBT
========

Command-based in-game NBT editor for Bukkit

***********************************************

build:
```shell
env PLUGIN_VERSION="DEV-SNAPSHOT" mvn package
```

***********************************************

**Features:**
* Browsing and editing NBT tags
* supports items, players, offline-players, entities, blocks, ~~chunks~~, schematic files and all other nbt files
* save tags to files
* spawn entity with custom nbt tags
* parse mojangson string
* custom colors and unicode symbols
* supports tab-completion
* supports operations copy/paste/cut/swap
* supports variables
* JSON editor for NBT strings

**Permissions**
* powernbt.use

***********************************************

## Commands ##

Use nbt command:  
`/powernbt ...`  
`/pnbt ...`  
`/nbt ...`  

Use nbt command in silent mode:  
`/powernbt. ...`  
`/pnbt. ...`  
`/nbt. ...`  

`(command) (required param) (optional param)`

### Browse tags ###


You can browse nbt tree structure using this command:  
`/nbt (object) (query) (view-mode)`

**object** - Object that contains NBT tag.  
Examples:  
`/nbt inventory` - show items in your inventory as nbt list  
`/nbt block` - show nbt of target block  
`/nbt item` - show nbt tag of item in hand  

**query (optional)** - Query to view nested tags.  
Examples:  
`/nbt block .` - empty query for block. Command is similar to /nbt block  
`/nbt me HealF` - show your health  
`/nbt *admin EnderItems[0].id` - show id of first item in admin's enderchest  

**view-mode (optional)** - View mode of tag

### Edit tags ###

#### Set new value for nbt tag: ####
`/nbt object query = value type`  
`/nbt value type > object query`  

**value** - New value of nbt tag  
Value can be: number, string, hexadecimal value, color, array of numbers  
Example:  
`/nbt me foodLevel = 20 - set your food level to full (20)`

**type** (optional in some cases) - Type of value  
Type can be: any of nbt tag type  
Type is required for numeric values. In other cases type is optional.  
Example:  
`/nbt buffer foo = 12 float` - set new value (12 float) to tag "foo" in buffer compound

#### Set new value for nbt tag from other object: ####
`/nbt object1 query1 = object2 query2`  
`/nbt object1 query1 < object2 query2`  
`/nbt object2 query2 > object1 query1`  

**object1** - Object to be changed  
**query1** (optional) - Value in object be changed (query)  
**object2** - Object whose value will be read  
**query2** (optional) - Query to get the specific value of object2  
Examples:  
`/nbt $playerFile = *playerName` - save all tags of playerName to file playerFile.nbt  
`/nbt me HealF = $playerFile HealF` - set value of your health to stored in playerFile.nbt in tag "HealF"  

#### Move value (cut and paste): ####
`/nbt object1 query1 >> object2 query2`  
`/nbt object2 query2 << object1 query1`  
Value will be removed from object1 and set to object2

#### Swap values: ####
`/nbt object1 query1 <> object2 query2`  
`/nbt object1 query1 swap object2 query2`  
Values of the objects will be swapped  
Example:  
`/nbt block Items <> me Inventory` - swap your inventory with targeted chest  

#### Add value: ####
`/nbt object1 query1 add value`  
`/nbt object1 query1 add object2 query2`  
`/nbt object1 query1 += value`  
`/nbt object1 query1 += object2 query2`  
Add value to object1

Behavior is different for the following cases:  
Object1 is number, value is number - increace number  
Object1 is string - concatenate  
Object1 is list or array, value is list or array - add all items from value to object  
Object is compound and value is compound - add all entries from value to object  
Examples:  
`/nbt me HealF += 2` - Increace your hp by 2   
`/nbt me HealF += -2` - Decreace your hp by 2   
`/nbt item display.Name += "(tool)"` - add text to item name  

#### Insert value to special position ####
`/nbt object1 query1 insert position value type`  
`/nbt object1 query1 ins position value type`  
`/nbt object1 query1 insert position object2 query2`  
`/nbt object1 query1 ins position object2 query2`  
Insert value to object to special position Object1 must be an list or array.  
Example:  

before: item display.Lore = `["Lore1","Lore2","Lore3","Lore4"]`  
`/nbt item display.Lore insert 2 "New Lore"` - insert "New lore" to position 2 in list  
after: item display.Lore = `["Lore1","Lore2","New Lore","Lore3","Lore4"]`  

#### Bitwise operations ####
`/nbt object1 query1 &= object2 query2`  
`/nbt object1 query1 |= object2 query2`  
`/nbt object1 query1 ^= object2 query2`  
`/nbt object1 query1 &= value type`  
`/nbt object1 query1 |= value type`  
`/nbt object1 query1 ^= value type`  
`/nbt object1 query1 inverse`  
`/nbt object1 query1 inv`  
`/nbt object1 query1 ~`

`&=` - bitwise AND  
`|=` - bitwise OR  
`|=` - bitwise XOR  
`~` - bitwise NOT  
Examples:  
`/nbt item HideFlags |= b0000100` - hide "Unbreakable" display property in item  
`/nbt item HideFlags &= b1111110` - show "ench" display property in item  
`/nbt item HideFlags ^= b0000010` - toggle "AttributeModifiers" display property in item  

#### Multiplication ####
`/nbt object1 query1 *= object2 query2`  
`/nbt object1 query1 *= value type`  
Multiply left value to right value.  
All values must be a numbers

#### Rename tags ####

`/nbt object query1 rename newName`  
`/nbt object query1 ren newName`  
Rename tag in compound to newName

#### Remove tags ####

`/nbt object query remove`  
`/nbt object query rem`  
If query is not present, it will attempt to remove object  
Examples:  
`/nbt item display remove` - remove all display attributes of item  
`/nbt $filename remove` - remove file filename.nbt  
`/nbt id12345 remove` - remove entity with id=12345  

#### Buffer ####

###### Copy value to buffer ######
`/nbt object query copy`  
`/nbt value type copy`  

###### Cut value to buffer ######
`/nbt object query cut`  

###### Paste value from buffer ######
`/nbt object query paste`  

Example:  
`/nbt me Inventory copy` - copy your inventory to buffer (clipboard)  
`/nbt buffer [0].tag.display.Name = "new Name of item"` - change value in buffer  
`/nbt me Inventory paste` - paste buffer back to your inventory  

#### Variables ####

###### Set variable ###### 

`/nbt %variable set object query`  
`/nbt %variable set value type`  
`/nbt object query as %variable`  
`/nbt value type as %variable`  
After this command you can use a variable as if using the object  
**%variable** - Name of variable  

###### Get variable ######
`/nbt %variable`

###### Remove variable ######
`/nbt %variable remove`

Example:  
`/nbt %b set block` - set block as variable %b  
`/nbt block as %b` - set block as variable %b  
`/nbt %b Items` - Now you can use a variable %b as if you are using a block  

#### Spawn entities ####

`/nbt object [query] spawn world`  
`/nbt mojangsonValue spawn world`  
**world** (optional) - World where spawn entity. Required for executing from console.  
Required tags in left side to spawn entity:  
**id** - entity Savegame ID   
**Pos** - position of entity. List of doubles  

Examples:  
`/nbt {id:pig,Pos:[1d,100d,2d],Riding:{id:MinecartRideable}} spawn world1` - spawn pig in minecart at position `1:100:2` in world world1  
`/nbt id123(Creeper) copy` - copy entity value to buffer  
`/nbt buffer Pos = me Pos` - change position stored in buffer to your position  
`/nbt buffer spawn` - spawn entity at your position  

#### Debug mode ####

Enable or disable debug mode:
`/nbt debug on`
`/nbt debug off`

#### Object ####

Object is special container that can contains nbt tag.
Plugin can read tags from it, save, and remove container.
You can use these objects:

* `me` - you =) as player  
* `item`, `i` - item in hand (only tagged items)  
* `block`, `b` - block at the line of sight  
* `chunk` - chunk in player's position (disabled in 1.17+)  
* `id<?>` - entity with specified id. Example: `id102`  
* `x:y:z:world` - block at position x:y:z in world  
* `chunk:x:z:world` - chunk at position x:z in world (disabled in 1.17+)  
* `buffer`, `c` - your buffer  
* `*name` - online player.  
* `@nаme` - offline player's .dat file.   
* `%variable` - defined variable. Example: `%skeleton`  
* `inventory`, `inv` - your inventory. (alias for "me Inventory")  
* `hand`, `h` - Selected slot in your inventory. (alias for "me Inventory[n]")  
* `hand:рlayer`, `h:рlayer` - Selected slot in inventory of player.  
* `$name` - file name.nbt in folder plugins/PowerNBT/nbt/  
* `$$name` - compressed file name.nbtz in folder plugins/PowerNBT/nbt/  
* `file:filename` - file in minecraft folder.  
filename can be enclosed in quotes: file:"plugins/myplugin/file.dat"  
* `gzip:filename` - compressed file in minecraft folder.  
* `sch:schematicName`, `schematic:schematicName` - Get schematic file in plugins/WorldEdit/schematics/  
* `compound` - create a new empty compound  
* `list` - create a new empty list  
* `self`, `this` - get object from left fide (or right side in some cases)  
Examples:  
`/nbt item display.Lore[2] = this display.Name`  
`/nbt me Inventory = this EnderItems`  
* `*` - future object.  
after this command you must select any block or entity or input new value to chat  

#### Query ####

Query allows you to browse and select special tag in object.  
Query is string containing tags, sepatated by `.`    
Examples:  
`Inventory[1].id` - id of second item in player inventory  
`display.Name` - item's name.  
`pages[0]` - first page of book  
`.` - root  

Tag name can contains spaces and unicode characters.  
In this case, enter the tag name in quotes:  
`forgeData."foo bar"[0]`  
`forgeData."unicode\u2600".tag`  
`forgeData.example."multi\nline"`  

###### JSON query: `#` ######
`display.Name#` - select string display.Name as JSON  
`display.Name#text` - select "text" property of JSON  
Example:  
`/nbt item display.Name#text = "name"`  
`/nbt item display.Name`  {"text":"name"}  

###### Range query: `[A..B]` ######
Select subarray or substring.  
`[1..3]` - select items from 1 to 3  
`[1..]` - select items from 1 to end  
`[3..1]` - select items from 1 to 3 in reversed order  
`[..3]` - select items from 3 to end in reversed order  
Examples:  
`/nbt item display.Lore[2..4] remove`  
`/nbt block Items = me Inventory[0..7]`  

#### View mode ####

###### limit ######
Limit of display lines or chars  
Examples:  
`/nbt me Inventory 10` - show first 10 items in inventory  
`/nbt item display.Name 100` - show first 100 characters of item name  

###### range ######
Show results from start range to end  
Example:  
`/nbt me Inventory 10-20` - show items in inventory from 10 to 19  
(default view limit is 0-10)

###### all ######
Show all lines or full result
Example:  
`/nbt me . all`  


###### hex ######

Show number or string in hex mode  
Example:  
`/nbt item display.color hex` - show color of item in hex mode  

You can combine hex mode with others by `,`:  
`/nbt item display.Name 5-10,hex` - show 5-10 characters of item name in hex mode

#### Value ####

In some commands you can specify new value for tag. But in some cases you also must specify a type.  
Value can be:  

###### string ######
string must be enclosed in quotes.  
supported java-like escaping by char `\ `  
escaping features:  
`\c` - Translate to COLOR_CHAR §  
`&` - Translate to COLOR_CHAR §  
`\&` - Translate to &  
`\_` - Translate to space  
type is not required (type is string by default)  
extra types can be used:   
`json` - convert json string to object  
`mojangson` - convert mojangson string to object  
Example:  
`/nbt item display.Name = "\cbItem name"`  
`/nbt "{test:true, text:\"foo\"}" json`  

###### numeric value ######
if the previous object already contains a value - type is optional  
available types: byte, short, int, long, float, double  
Examples:  
`/nbt me Health = 100500`   
`/nbt buffer NewValue = 18 int`  

###### hexadecimal ######
default type is int, but you can specify other type  
available types: byte, short, int, long, float, double  
Example:  
`/nbt item display.color = #FF0000 - red color`  

###### binary ######
Binary value, starting by `b`  
default type is int, but you can specify other type  
available types: byte, short, int, long, float, double  
Example:  
`/nbt item HideFlags = b101` - hide display properties for "ench" and "Unbreakable"  

###### color ######
color contains an integer value  
colors:
* `black`
* `red`
* `green`
* `brown`
* `blue`
* `purple`
* `cyan`
* `lightgray`
* `gray`
* `pink`
* `lime`
* `lightgreen`
* `yellow`
* `lightblue`
* `magenta`
* `orange`
* `white`

Example:  
`/nbt item display.color = red`   

###### constants ######

`on` - aliases for "1 byte"  
`off` - aliases for "0 byte"  
`true` - "true" (json mode) or "1 byte" (nbt mode)   
`false` - "false" (json mode) or "0 byte" (nbt mode)  
`int[]`, `byte[]`, `long[]` - new empty array  

###### typed arrays ######

`[1,2,...]b`, `[1,2,...]i`, `[1,2,...]l` - native arrays, values separated by `,`  
last char:  
`b` -> byte[] array  
`i` -> int[] array  
`l` -> long[] array
Example:  
`/nbt buffer myByteArray = [1,2,3,10]b`  

###### json / mojangson ######
Create nbt object from string in mojangson format  
Spaces in tag names are not allowed.  
Types can be used: `json`, `mojangson` (default)  
Example:  
`/nbt item = {display:{Name:"New item name"}}`  
`/nbt item.display.Name# = {text: "New item name"} json`  
See http://minecraft.gamepedia.com/Tutorials/Command_NBT_Tags  

#### Type ####

Available nbt types:  
`byte`, `short`, `int`, `long`, `float`, `double`, `byte[]`, `int[]`, `long[]`, `string`  
Extra types available for json and strings:  
`json`, `mojangson`  

#### Future object ####

Future object - is object that you can specify later  

Example, set fire ticks:  
`/nbt * Fire = 1000` - waiting for future object  
=> select entity by right-click  
and selected entity begins to burn  

Example, set variable:  
`/nbt %var select` - waiting for future object  
=> select entity or block by right-click  
and selected object is stored in variable %var  

***********************************************

API
---
http://flinbein.github.io/PowerNBT/apidocs/
