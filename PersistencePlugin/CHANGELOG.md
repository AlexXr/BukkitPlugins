# CHANGELOG

# 0.23

 - Vastly improve Plugin command interface:
 - Support sub-commands
 - Auto-dispatch to individual command handlers
 - Auto-typing with flexible data-driven "sender" system
 - Automatically generated, detailed, explorable help

# 0.22

 - Change contained classes so sub-field names get prefixed.
 - Support containers of containers of containers...
 - Allow for pure contained classes, which don't need an id.

# 0.21

 - Finally support contained lists!
 - Also, fix the server console interface.
 - Extend the Command and Plugin DAOs

# 0.20

 - Massive, massive refactor. No real functionality change, but:
 - All functionality related to creating and populatd classed moved to core.
 - SqlStore only deals with raw database-type actions now.
 - Very, very close to supporting contained references and lists.

# 0.19

 - Fully support Bukkit command interface, you can now use persistence commands from the server console!

# 0.18

 - Integrate Classes plugin directly into Persistence
 - Re organize namespaces for clarity.

# 0.17

 - Update to use the command standard. The persist command is now defined in the YML file.
 - Start work on basic CommandData framework. I'd like to support sub-commands, such as all the sub-commands of /persist,
   and also support auto-generating the help based on this data.
 - Get about halfway there on contained object support.

# 0.16

 - Implement Messaging utility, for consolidated storage of messages and commands.
 - Remove complex jar loading mechanism, use manifest classpath instead. Thanks A LOT, tkelly!
 - Implement contained objects.

# 0.15

 - Escape all table and column names, to allow for entity names with spaces, and also so I can use "group".
 - Support lists of objects.

# 0.14

 - Implement persistence of Lists of primitive objects. Lists of DAO references is not far behind.

# 0.13

 - Implement reload console command, add save command and RESET command.
 - Add list command for data querying.

# 0.12

- Support Lists- not fully functional yet.
- Add basic console UI.

## 0.11

- Support references to other DAO's, including circular class references, for storing structures such as trees.

## 0.10

- First release
- Auto-load JDBC drivers
- Implement support for simple object structure persistence and caching.
