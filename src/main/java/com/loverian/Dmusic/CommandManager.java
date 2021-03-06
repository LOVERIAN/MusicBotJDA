package com.loverian.Dmusic;

import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.Icommand;
import com.loverian.Dmusic.command.commands.HelpCommand;
import com.loverian.Dmusic.command.commands.PingCommand;
import com.loverian.Dmusic.command.commands.music.*;
import com.loverian.Dmusic.command.commands.owner.EvalCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<Icommand> commands = new ArrayList<>();

    public CommandManager() {
        addCommand(new PlayCommand());
        addCommand(new JoinCommand());
        addCommand(new LeaveCommand());
        addCommand(new NpCommand());
        addCommand(new QueueCommand());
        addCommand(new SkipCommand());
        addCommand(new StopCommand());
        addCommand(new FwdCommand());
        addCommand(new PingCommand());
        addCommand(new HelpCommand(this));
        addCommand(new EvalCommand());
    }

    private void addCommand(Icommand cmd) {
        boolean namefound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));

        if (namefound) {
            throw new IllegalArgumentException("A command with this name is already present");
        }
        commands.add(cmd);
    }

    public List<Icommand> getCommands() {
        return commands;
    }

    @Nullable
    public Icommand getCommand(String search){
        String searchLower = search.toLowerCase();

        for (Icommand cmd : this.commands) {
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)) {
                return cmd;
            }
        }
        return null;
    }

    void handle(GuildMessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(Config.get("PREFIX")), "")
                .split("\\s+");

        String invoke = split[0].toLowerCase();
        Icommand cmd = this.getCommand(invoke);

        if (cmd != null) {
            //event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }

    }
}
