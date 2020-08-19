package com.loverian.Dmusic.command.commands.owner;

import com.loverian.Dmusic.Config;
import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.Icommand;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class EvalCommand implements Icommand {
    private final GroovyShell engine;
    private final String imports;

    public EvalCommand() {
        this.engine =  new GroovyShell();
        this.imports = "import java.io.*\n" +
                "import java.lang.*\n" +
                "import java.util.*\n" +
                "import java.util.concurrent.*\n" +
                "import net.dv8tion.jda.api.*\n" +
                "import net.dv8tion.jda.api.entities.*\n" +
                "import net.dv8tion.jda.api.entities.impl.*\n" +
                "import net.dv8tion.jda.api.managers.*\n" +
                "import net.dv8tion.jda.api.managers.impl.*\n" +
                "import net.dv8tion.jda.api.utils.*\n";
    }

    @Override
    public void handle(CommandContext ctx) {
        GuildMessageReceivedEvent event = ctx.getEvent();
        List<String> args = ctx.getArgs();
        if (!event.getAuthor().getId().equals(Config.get("OWENER"))) {
            return;
        }

        if (args.isEmpty()) {
            event.getChannel().sendMessage("Missing arguments").queue();

            return;
        }
        try{
        engine.setProperty("args", args);
        engine.setProperty("event", event);
        engine.setProperty("message", event.getMessage());
        engine.setProperty("channel", event.getChannel());
        engine.setProperty("jda", event.getJDA());
        engine.setProperty("guild", event.getGuild());
        engine.setProperty("member", event.getMember());

        String script = imports + event.getMessage().getContentRaw().split("\\s+", 2)[1];
        Object out = engine.evaluate(script);

        event.getChannel().sendMessage(out == null ? "Executed without error" : out.toString()).queue();
    }
        catch (Exception e) {
        event.getChannel().sendMessage(e.getMessage()).queue();
    }

    }

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public String getHelp() {
        return "";
    }
}
