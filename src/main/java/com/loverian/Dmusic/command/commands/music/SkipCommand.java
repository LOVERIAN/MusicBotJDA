package com.loverian.Dmusic.command.commands.music;

import com.loverian.Dmusic.Config;
import com.loverian.Dmusic.audio.AudioHandler;
import com.loverian.Dmusic.audio.GuildMusicManager;
import com.loverian.Dmusic.audio.PlayerManager;
import com.loverian.Dmusic.audio.TrackScheduler;
import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.Icommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.io.filefilter.ConditionalFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SkipCommand implements Icommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkipCommand.class);

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx);
        TrackScheduler scheduler = musicManager.scheduler;
        AudioPlayer player = musicManager.player;

        final GuildVoiceState selfVoiceState = ctx.getMember().getVoiceState();

        if (player.getPlayingTrack() == null) {
            channel.sendMessage(Config.get("WARN") +" The player isn't playing anything").queue();

            return;
        }
        GuildVoiceState memberVoiceState = ctx.getMember().getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage(Config.get("WARN") +" You have to be in the same voice channel as me to use this").queue();
            return;
        }

        long u = (long) player.getPlayingTrack().getUserData();

        if (ctx.getAuthor().getIdLong() == u ||
                ctx.getMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                ctx.getMember().getRoles().stream().map(Role::getName).anyMatch("DJ"::equals)){

            channel.sendMessage(Config.get("SUCCESS") + " skipped... ` " + player.getPlayingTrack().getInfo().title + "`").queue();
            scheduler.nextTrack();
            return;
        }
        if ( scheduler.checkUser(ctx.getMember().getId()) ) {
            channel.sendMessage("Already voted").queue();
            return;
        }
        TrackScheduler.votes++;
        int req = selfVoiceState.getChannel().getMembers().size()/2;
        LOGGER.info(String.valueOf(TrackScheduler.votes));
        LOGGER.info(String.valueOf(selfVoiceState.getChannel().getMembers().size()));
        if (TrackScheduler.votes >= req) {
            scheduler.nextTrack();
            channel.sendMessage("Skipping the current track").queue();
            return;
        }
        scheduler.addUser(ctx.getMember().getId());
        channel.sendMessage("voted " + TrackScheduler.votes + "/" + selfVoiceState.getChannel().getMembers().size()).queue();
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public List<String> getAliases() {
        List<String> list=new ArrayList<>();
        //Adding elements in the List
        list.add("s");
        return list;
    }

    @Override
    public String getHelp() {
        return "skip the track";
    }
}
