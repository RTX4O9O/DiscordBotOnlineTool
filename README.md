# Discord Bot Online Tool

# A Super Simple Discord Bot Host Tool

## What is this

Discord Bot Online Tool is a Java-based application that allows users to run and manage Discord bots easily.
It provides a simple command-line interface to start, stop, and manage your Discord bot without the need for manual
command prompt interactions.

## What can this do

Start and manage your Discord bot with ease.
Ability to automatically save and load your bot's token for convenience.
Graceful shutdown of the bot to ensure a clean exit.
For more you can check our command list [here](#Anything-else)

## What else do I need

[Java Runtime Environment (JRE)](https://www.java.com/zh-TW/download/manual.jsp) version 8 or above is required to run
the Discord Bot Online Tool.

## How can I install this

download the zip file and unarchive it (everywhere you want!)
run the start.bat
and a little cute black console window should pop up
first, it will ask for your discord BOT token
all you need to do is just copy it from [discord developer portal](https://discord.com/developers/applications) and
paste it in
now, bot should work perfectly

<a name="Anything-else"></a>
## Anything else

there are several **__commands__** you can use in the console
- online status
`/onlinestatus` (or `/os`) show currently online status (ex. online, idle, dnd or invis)
`/onlinestatus set <0|1|2|3>` (or `/os set <0|1|2|3>` ) set online status of your bot
```
0 = Online (default)
1 = Idle
2 = Do Not Distrub (aka dnd)
3 = Invisible
```
- activity
`/activity` show currently activity 
`/activity template set <p|l|w|c> <activity name>` set bot activity with discord provided template
```
p = Playing
l = Listening
w = Watching
C = Competing
S = Streaming _for this, you have to add <url> as the last argument_
```
`/activity template set <p|l|w|c> <activity name>` set bot activity with discord provided template
`/activity clear` clear bot activity

- bot manage
`/stop` stop the bot and turn off the console
`/logout` logout from current BOT and you can log into another BOT you want

## What's next

[there are some ideas of super cool feature ideas](https://github.com/RTX4O9O/DiscordBotOnlineTool/discussions/9)
feel free to check it out and leave your idea

## Where can I contribute

Contributions are welcome!
If you have any suggestions, bug reports, or feature requests
please open an issue on the GitHub repository or submit a pull request.
ty a lot<3

## Disclaimer

The software and code provided in this repository are for educational and experimental purposes only.
The author makes no representations or warranties of any kind, express or implied, about the completeness, accuracy,
reliability, suitability, or availability of the code.
The author will not be liable for any errors, omissions, or any losses, injuries, or damages arising from the use of the
code.

Use the code at your own risk.
It is recommended to thoroughly review and test any code before using it in a production environment or with sensitive
data.
The author is not responsible for any direct, indirect, incidental, or consequential damages resulting from the use of
the code.

Always use caution when working with unfamiliar code, and make sure to understand its implications before executing it.
If you encounter any issues or have questions, feel free to create an issue in this repository, and the author will try
to assist as best as possible.

By using the code provided in this repository, you agree to this disclaimer and take full responsibility for your
actions.
te at 
