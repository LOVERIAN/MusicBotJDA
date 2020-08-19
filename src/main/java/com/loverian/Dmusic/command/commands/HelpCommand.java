package com.loverian.Dmusic.command.commands;

import com.loverian.Dmusic.CommandManager;
import com.loverian.Dmusic.Config;
import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.Icommand;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.List;

public class HelpCommand implements Icommand {

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();

        if (args.isEmpty()) {
            EmbedBuilder eb = EmbedUtils.defaultEmbed();
            eb.setAuthor("All about " + ctx.getSelfMember().getEffectiveName() + "!", Config.get("LOGOu"), Config.get("LOGO"));
            eb.setDescription("The official Dynamo Gaming discord music bot owned by " +
                    Config.get("OWN"));
            StringBuilder builder = new StringBuilder();
            builder.append("\n\nList of commands\n");
            manager.getCommands().stream().map(Icommand::getName).forEach(
                    (it) -> { if (!it.equals("eval"))
                        builder.append('`').append(Config.get("PREFIX")).append(it).append("`\n");
                    });
            eb.appendDescription(builder.toString());
            eb.appendDescription("\n [Click here](https://discord.com/api/oauth2/authorize?client_id=665628292958781460&permissions=37080128&scope=bot) to invite to your server");
            eb.setColor(Color.orange);
            channel.sendMessage(eb.build()).queue();
            return;
        }
        String search = args.get(0);
        Icommand command = manager.getCommand(search);

        if (command == null) {
            channel.sendMessage("Nothing found for " + search).queue();
            return;
        }

        channel.sendMessage(command.getHelp()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows the list with commands in the bot\n" +
                "Usage: `!help [command]`";
    }
}
