
# INTRODUCTION
 
This library will provide you a much faster way to develop your own plugins without the need to create complex classes or any other complex infrastructure.It will help you to declare your commands, events, mobs very easily keeping the SOLID principles.
 
# SETUP
 
Download: https://www.spigotmc.org/resources/class-mapper-api.90302/
 
To make this library work you have declare: Mapper.build(<base package>, <plugin instance>)
Base package. It will represent the package of your code, where it will scan for your classes
Plugin instance. The main plugin class (the one that extends JavaPlugin). 

All classes needs to have a public empty constructor 

And then you have specify which options you want to use in your plugin.
 
```java
public class PluginMain extends JavaPlugin {
    @Override
    public void onEnable() {
        String onWrongCommand = ChatColor.DARK_RED + "Command not found";
        String onWrongSender = ChatColor.DARK_RED + "You have to be in the server";
 
        Mapper.build("es.jaime", this)
                .commandMapper(onWrongCommand, onWrongSender)
                .mobMapper()
                .eventListenerMapper()
                .startScanning();
    }
}
``` 
 
# COMMAND MAPPER
A single command can be run in a separate class called command runner. To achieve this:
1. The command should be declared in plugin.yaml as usual
2. The command runner class will have to be annotated with @Command annoation, which will include data of the command (name, permissions, usage etc).
3. You will have to implement an interface: 
	- If the command has no arguments, it will have to implement CommandRunnerNonArgs, with method: void execute(CommandSender sender)
	- If the command has arguments, it will have to import CommandRunnerArgs void execute(T args, CommandSender sender). The args will be mapped to T args object via usage property in @Command 
 
```java
@Command("helloworld")
public class HelloWorldCommand implements CommandRunnerNonArgs {
	@Override
	public void execute(CommandSender sender) {
        	commandSender.sendMessage("Hello " + sender.getName());
	}
}
```

```java
@Command(value = "pay", usage = {"money", "to"})
public class PayCommandRunner implements CommandRunner<PayCommand> {
	@Override
	public void execute(PayCommand command, CommandSender sender) {
        	commandSender.sendMessage(String.format("You will pay %s %d$", command.getTo, command.getMoney));
	}
}
	
class PayCommand {
	private double money;
	private String to;
	
	//Getters...
}
```
	
## @Command
This annotation will contain information about the command and how it will be runned. Properties:

- String value() Indicates the command name or subcommand. Example @Command(value = "pay") -> /pay Or @Command(value = "balance pay") /balance pay sucommand
- boolean canBeTypedInConsole() default false If it can be typed in console
- String permissions() default "" Required permissions to run the command
- boolean isAsync() default false If set to true, the command will be runned in other thread.
- String helperCommand() default "" If there is some error while executing the command this command name will be shown to the player
- String explanation() default "" Indicates the explanation of your command, this is useful for autogenerated help command
- String[] args default "" Indicates the args for your command. This names of this arrays args will have to be the same to the class that will map the comamnd. See bellow TODO

## Map objects to command args
If a command has arguments the args can be mapped to an object. The object fields will have to match the args property in @Command. Example: 
```java
@Command(value = "pay", usage = {"money", "to"})
public class PayCommandRunner implements CommandRunner<PayCommand> {
	@Override
	public void execute(PayCommand command, CommandSender sender) {
        	commandSender.sendMessage(String.format("You will pay %s %d$", command.getTo, command.getMoney));
	}
}
	
class PayCommand {
	private double money;
	private String to;
	//Getters...
}
```
Some considerations of the object that will map the args: 
- It will need to have atleast one non args constructor.
- Inheretance fields  & final fields are not supported
- Supported types: double, int, boolean, short, long, float, String, UUID

### Optional arguments
You can specify optional arguments: [argument]  Considartions:
- A single optional argument can be specified per command, and it will have to go in the las position

```java
@Command(value = "balance pay", usage = {"money", "to", " [reason]"})
public class PayCommandRunner implements CommandRunner<PayCommand> {
	@Override
	public void execute(PayCommand command, CommandSender sender) {
		//command.getReason() can be null
        	commandSender.sendMessage(String.format("You will pay %s %d$", command.getTo, command.getMoney));
	}
}
User can "/balance pay 12 otherplayer" or "/balance pay 12 otherplayer reason" both will work

```
### Default value for optional arguments
To specify a default value for an optional argument: ¡a default value!
- This only works for optional arguments (the ones with "[arg name]")
- It will have to go at the end of the usage arg

```java
@Command(value = "balance pay", usage = {"money", "to", " [reason]¡why not!"})
public class PayCommandRunner implements CommandRunner<PayCommand> {
	@Override
	public void execute(PayCommand command, CommandSender sender) {
		//if reason not specify command.getReason() will return "why not"
        	commandSender.sendMessage(String.format("You will pay %s %d$", command.getTo, command.getMoney));
	}
}
```
### Long text argument
To specify an argument which will be composed of words separeted with space ..."arg name"
- It will be mapped to an string not an array.
- It will have to go before the arg name declaration
- It can be used in optional args & not optional args
- Only the las argument can be a "long text"

```java
@Command(value = "balance pay", usage = {"money", "to", " ...[reason]¡why not!"})
public class PayCommandRunner implements CommandRunner<PayCommand> {
	@Override
	public void execute(PayCommand command, CommandSender sender) {
		//if reason not specify command.getReason() will return "why not"
		//if user types "/balance pay 10 otherplayer I love you" command.getReason() will return "I love you"
        	commandSender.sendMessage(String.format("You will pay %s %d$", command.getTo, command.getMoney));
	}
}
```	
# TASK MAPPER

You can create your own task (the ones that extends BukktiRunnable) without taking care to start them. The time will be in ticks (every 20 ticks it is 1 second)

```java
@Task(40) //It will be executed every 2 seconds
public class TestTask extends TaskRunner {
    @Override
    public void run () {
        //TODO...
    }
}
```

If you want an initial delay of 1 minute and a period of 30 seconds:

```java
@Task(period = 30 * BukkitTimeUnit.SECOND, delay = BukkitTimeUnit.MINUTE)
public class TestTask extends TaskRunner {
      @Override
      public void run () {
      	 //TODO...
      }
}
``` 
 
# MOB MAPPER
 
If you want a mob/entity in a fixed location that the player can interact with, you can use this part of the plugin. 

```java
@Mob(x = 0, y = 70, z = 0)
public class StatsMob implements OnPlayerInteractMob {
    @Override
    public void execute (PlayerInteractEntityEvent event) {
        //TODO...
    }
}
```
 
# EVENT LISTENER MAPPER
 
When you create your plugin event listener you always have to register them. Now with this library you don't need to do it. It will register them for you. 
 
The event listener classes need to have an empty constructor.
 
 
# CONTRIBUTE
 
If you want to contribute to this project feel free to pull request to here: https://github.com/JaimeTruman/Easy-Bukkit-CommandManager
