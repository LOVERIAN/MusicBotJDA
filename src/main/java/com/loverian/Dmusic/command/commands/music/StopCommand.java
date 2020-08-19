package com.loverian.Dmusic.command.commands.music;

import com.loverian.Dmusic.audio.GuildMusicManager;
import com.loverian.Dmusic.audio.PlayerManager;
import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.Icommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class StopCommand implements Icommand {
    @Override
    public void handle(CommandContext ctx) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx);
        if(ctx.getMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                ctx.getMember().getRoles().stream().map(Role::getName).anyMatch("DJ"::equals)) {
            musicManager.scheduler.clearQueue();
            musicManager.player.stopTrack();
            musicManager.player.setPaused(false);

            ctx.getChannel().sendMessage("Stopping the player and clearing the queue").queue();
        }else{
            ctx.getChannel().sendMessage("You don't have permission, You need a role named `DJ`").queue();
        }
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getHelp() {
        return "stop and clear the queue";
    }

    @Override
    public List<String> getAliases() {
            List<String> list=new ArrayList<>();
            //Adding elements in the List
            list.add("clear");
            return list;
    }
}
