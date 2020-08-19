package com.loverian.Dmusic.command.commands.music;

import ch.qos.logback.classic.spi.LoggerRemoteView;
import com.loverian.Dmusic.Config;
import com.loverian.Dmusic.Listener;
import com.loverian.Dmusic.audio.GuildMusicManager;
import com.loverian.Dmusic.audio.PlayerManager;
import com.loverian.Dmusic.command.CommandContext;
import com.loverian.Dmusic.command.Icommand;
import com.loverian.Dmusic.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class NpCommand implements Icommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(NpCommand.class);
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx);
        AudioPlayer player = musicManager.player;

        if (player.getPlayingTrack() == null) {
            channel.sendMessage("The player is not playing any song.").queue();

            return;
        }
        AudioTrackInfo info = player.getPlayingTrack().getInfo();
        String url = FormatUtil.extractYTId(player.getPlayingTrack().getInfo().uri);
        double progress = (double) player.getPlayingTrack().getPosition()/info.length;
        String user = ctx.getJDA().getUserById((long) player.getPlayingTrack().getUserData()).getName();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(0xffc300)
                .setTitle("Now Playing")
                .setDescription( "[" + info.title + "](" + info.uri + ")\n\n"
                        + (player.isPaused() ? Config.get("PAUSE") : Config.get("PLAY")
                        + " "+FormatUtil.progressBar(progress)
                        + " `[" + FormatUtil.formatTime(player.getPlayingTrack().getPosition())
                        + "/" + FormatUtil.formatTime(info.length) + "]`"))
                .addField("Requested By", "`"+ user + "`" ,true)
                .setThumbnail(Objects.requireNonNull(ctx.getJDA().getUserById((Long) player.getPlayingTrack().getUserData())).getAvatarUrl())

                .setImage(url);

        ctx.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public String getName() {
        return "np";
    }

    @Override
    public String getHelp() {
        return "shpws current playing song";
    }
}
