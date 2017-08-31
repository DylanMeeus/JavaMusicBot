package ovh.not.javamusicbot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import ovh.not.javamusicbot.Command;
import ovh.not.javamusicbot.GuildMusicManager;

import java.util.List;

public class MoveCommand extends Command {
    public MoveCommand() {
        super("move");
    }

    @Override
    public void on(Context context) {
        GuildMusicManager musicManager = GuildMusicManager.get(context.getEvent().getGuild());
        if (musicManager == null || musicManager.player.getPlayingTrack() == null) {
            context.reply("No music is playing on this guild!");
            return;
        }
        if (musicManager.open && musicManager.player.getPlayingTrack() != null
                && !context.getEvent().getMember().hasPermission(musicManager.channel, Permission.VOICE_MOVE_OTHERS)) {
            context.reply("dabBot is already playing music in " + musicManager.channel.getName() + " so it cannot " +
                    "be moved. Members with the `VOICE_MOVE_OTHERS` permission are exempt from this.");
            return;
        }
        if (context.getArgs().length == 0) {
            context.reply("Usage: %prefix%move <voice channel name>");
            return;
        }
        Guild guild = context.getEvent().getGuild();
        List<VoiceChannel> channels = guild.getVoiceChannelsByName(String.join(" ", context.getArgs()), true);
        if (channels == null || channels.isEmpty()) {
            context.reply("Could not find the specified voice channel!");
            return;
        }
        VoiceChannel channel = channels.get(0);
        musicManager.player.setPaused(true);
        musicManager.close();
        musicManager.open(channel, context.getEvent().getAuthor());
        musicManager.player.setPaused(false);
        context.reply("Moved voice channel!");
    }
}
