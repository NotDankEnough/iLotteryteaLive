package kz.ilotterytea.bot.builtin.mc;

import com.google.gson.Gson;
import kz.ilotterytea.bot.Huinyabot;
import kz.ilotterytea.bot.SharedConstants;
import kz.ilotterytea.bot.api.commands.Command;
import kz.ilotterytea.bot.api.commands.Request;
import kz.ilotterytea.bot.api.commands.Response;
import kz.ilotterytea.bot.entities.channels.Channel;
import kz.ilotterytea.bot.i18n.LineIds;
import kz.ilotterytea.bot.models.serverresponse.mc.ServerInfo;
import kz.ilotterytea.bot.utils.ParsedMessage;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.List;

/**
 * A command for getting info about Minecraft servers.
 *
 * @author ilotterytea
 * @since 1.5
 */
public class MCServerInfoCommand implements Command {
    @Override
    public String getNameId() {
        return "mcserver";
    }

    @Override
    public List<String> getAliases() {
        return List.of("mcsrv", "mcs");
    }

    @Override
    public Response run(Request request) {
        ParsedMessage message = request.getMessage();
        Channel channel = request.getChannel();

        if (message.getMessage().isEmpty()) {
            return Response.ofSingle(Huinyabot.getInstance().getLocale().literalText(
                    channel.getPreferences().getLanguage(),
                    LineIds.NO_MESSAGE
            ));
        }

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request httpRequest = new okhttp3.Request.Builder()
                .get()
                .url(SharedConstants.MCSRVSTATUS_ENDPOINT + "/" + message.getMessage().get())
                .build();

        try (okhttp3.Response response = client.newCall(httpRequest).execute()) {
            if (response.body() == null || response.code() != 200) {
                return Response.ofSingle(Huinyabot.getInstance().getLocale().formattedText(
                        channel.getPreferences().getLanguage(),
                        LineIds.HTTP_ERROR,
                        String.valueOf(response.code()),
                        "MCSRVSTATUS"
                ));
            }

            ServerInfo serverInfo = new Gson().fromJson(response.body().string(), ServerInfo.class);

            if (!serverInfo.getOnline()) {
                return Response.ofSingle(Huinyabot.getInstance().getLocale().formattedText(
                        channel.getPreferences().getLanguage(),
                        LineIds.C_MCSERVER_SERVERISOFFLINE,
                        serverInfo.getHostname()
                ));
            }

            return Response.ofSingle(Huinyabot.getInstance().getLocale().formattedText(
                    channel.getPreferences().getLanguage(),
                    LineIds.C_MCSERVER_SUCCESS,
                    serverInfo.getHostname(),
                    (serverInfo.getMotd().containsKey("clean")) ? String.join(" ~ ", serverInfo.getMotd().get("clean")) : "N/A",
                    (serverInfo.getPlayers().containsKey("online")) ? String.valueOf(serverInfo.getPlayers().get("online")) : "N/A",
                    (serverInfo.getPlayers().containsKey("max")) ? String.valueOf(serverInfo.getPlayers().get("max")) : "N/A",
                    serverInfo.getVersion()
            ));
        } catch (IOException e) {
            return Response.ofSingle(Huinyabot.getInstance().getLocale().literalText(
                    channel.getPreferences().getLanguage(),
                    LineIds.SOMETHING_WENT_WRONG
            ));
        }

    }
}
