package com.loverian.Dmusic.command.commands.music;

import com.loverian.Dmusic.Config;
import com.loverian.Dmusic.audio.GuildMusicManager;
import com.loverian.Dmusic.audio.PlayerManager;
import com.loverian.Dmusic.audio.TrackScheduler;
import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.Icommand;
import com.loverian.Dmusic.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class FwdCommand implements Icommand {

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();

        if(ctx.getMember().hasPermission(Permission.MESSAGE_MANAGE) ||
                ctx.getMember().getRoles().stream().map(Role::getName).anyMatch("DJ"::equals)) {
            PlayerManager playerManager = PlayerManager.getInstance();
            GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx);
            AudioPlayer player = musicManager.player;
            AudioTrack track = player.getPlayingTrack();
            if (track == null){
                return;
            }
            List<String> args = ctx.getArgs();
            try {
                long duration = Long.parseLong(args.get(0));
                duration = duration * 1000;
                if (track.isSeekable()) {
                    duration = track.getPosition() + duration;
                    track.setPosition(duration);

                    channel.sendMessage("Forwarded at `"
                            + FormatUtil.formatTime(player.getPlayingTrack().getPosition()) + "`").queue();
                } } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }else {
            channel.sendMessage(Config.get("DENY") + " You don't have permission to use that").queue();
        }
    }

    @Override
    public String getName() {
        return "fwd";
    }

    @Override
    public String getHelp() {
        return "Forward track in seconds";
    }

    @Override
    public List<String> getAliases() {
        List<String> alias = new ArrayList<>();
        alias.add("forward");
        alias.add("seek");
        alias.add("f");
        return alias;
    }
}
