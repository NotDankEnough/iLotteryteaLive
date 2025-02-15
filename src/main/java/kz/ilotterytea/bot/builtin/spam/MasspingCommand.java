package kz.ilotterytea.bot.builtin.spam;

import com.github.twitch4j.helix.domain.Chatter;
import kz.ilotterytea.bot.Huinyabot;
import kz.ilotterytea.bot.SharedConstants;
import kz.ilotterytea.bot.api.commands.Command;
import kz.ilotterytea.bot.api.commands.CommandException;
import kz.ilotterytea.bot.api.commands.Request;
import kz.ilotterytea.bot.api.commands.Response;
import kz.ilotterytea.bot.entities.channels.Channel;
import kz.ilotterytea.bot.entities.permissions.Permission;
import kz.ilotterytea.bot.utils.ParsedMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Ping em, Fors! LUL
 *
 * @author ilotterytea
 * @since 1.0
 */
public class MasspingCommand implements Command {
    @Override
    public String getNameId() {
        return "massping";
    }

    @Override
    public int getDelay() {
        return 60000;
    }

    @Override
    public Permission getPermissions() {
        return Permission.MOD;
    }

    @Override
    public List<String> getAliases() {
        return List.of("mp", "масспинг", "мп", "massbing");
    }

    @Override
    public Response run(Request request) throws CommandException {
        ParsedMessage message = request.getMessage();
        Channel channel = request.getChannel();

        List<Chatter> chatters;

        try {
            chatters = Huinyabot.getInstance().getClient().getHelix().getChatters(
                    SharedConstants.TWITCH_TOKEN,
                    channel.getAliasId().toString(),
                    Huinyabot.getInstance().getCredential().getUserId(),
                    null,
                    null
            ).execute().getChatters();
        } catch (Exception ignored) {
            throw CommandException.insufficientRights(request);
        }

        String msgToAnnounce;

        if (message.getMessage().isEmpty()) {
            msgToAnnounce = "";
        } else {
            msgToAnnounce = message.getMessage().get();
        }

        ArrayList<String> msgs = new ArrayList<>();
        msgs.add("");

        int index = 0;

        for (Chatter chatter : chatters) {
            StringBuilder sb = new StringBuilder();

            if (new StringBuilder()
                    .append("\uD83D\uDCE3 ")
                    .append(msgToAnnounce)
                    .append(" \u00b7 ")
                    .append(msgs.get(index))
                    .append("@")
                    .append(chatter.getUserLogin())
                    .append(", ")
                    .length() < 500
            ) {
                sb.append(msgs.get(index)).append("@").append(chatter.getUserLogin()).append(", ");
                msgs.remove(index);
                msgs.add(index, sb.toString());
            } else {
                msgs.add("");
                index++;
            }
        }

        return Response.ofMultiple(msgs.stream().map((x) -> {
            x = x.trim();
            if (x.endsWith(",")) x = x.substring(0, x.length() - 1);
            return "\uD83D\uDCE3 " + msgToAnnounce + " \u00b7 " + x;
        }).toList());
    }
}
