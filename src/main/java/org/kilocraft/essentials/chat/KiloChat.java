package org.kilocraft.essentials.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.kilocraft.essentials.api.KiloEssentials;
import org.kilocraft.essentials.api.ModConstants;
import org.kilocraft.essentials.api.chat.LangText;
import org.kilocraft.essentials.api.chat.TextFormat;
import org.kilocraft.essentials.commands.CommandHelper;
import org.kilocraft.essentials.config.ConfigValueGetter;
import org.kilocraft.essentials.config.KiloConfig;
import org.kilocraft.essentials.config.provided.localVariables.UserConfigVariables;
import org.kilocraft.essentials.user.ServerUser;

import static org.kilocraft.essentials.api.KiloServer.getServer;

public class KiloChat {
	private static ConfigValueGetter config = KiloConfig.getProvider().getMain();
	private static ConfigValueGetter messages = KiloConfig.getProvider().getMessages();

	public static String getFormattedLang(String key) {
		return getFormattedString(ModConstants.getLang().getProperty(key), (Object) null);
	}

	public static String getFormattedLang(String key, Object... objects) {
		return getFormattedString(ModConstants.getLang().getProperty(key), objects);
	}

	public static String getFormattedString(String string, Object... objects) {
		return (objects[0] != null) ? String.format(string, objects) : string;
	}

	public static void sendMessageTo(ServerPlayerEntity player, ChatMessage chatMessage) {
		sendMessageTo(player, new LiteralText(chatMessage.getFormattedMessage()));
	}

	public static void sendMessageTo(ServerCommandSource source, ChatMessage chatMessage)
			throws CommandSyntaxException {
		sendMessageTo(source.getPlayer(), new LiteralText(chatMessage.getFormattedMessage()));
	}

	public static void sendMessageTo(ServerPlayerEntity player, Text text) {
		player.sendChatMessage(text, MessageType.CHAT);
	}

	public static void sendMessageTo(ServerCommandSource source, Text text) {
		source.sendFeedback(text, false);
	}

	public static void sendMessageToSource(ServerCommandSource source, ChatMessage message) {
		if (CommandHelper.isConsole(source))
			KiloEssentials.getInstance().getServer().sendMessage(message.getOriginal());
		else
			source.sendFeedback(new LiteralText(message.getFormattedMessage()), false);
	}

	public static void sendMessageToSource(ServerCommandSource source, Text text) {
		if (CommandHelper.isConsole(source))
			getServer().sendMessage(text.asString());
		else
			source.sendFeedback(text, false);
	}

	public static void sendLangMessageTo(ServerCommandSource source, String key) {
		if (CommandHelper.isConsole(source))
			getServer().sendMessage(getFormattedLang(key));
		else
			source.sendFeedback(LangText.get(true, key), false);
	}

	public static void sendLangMessageTo(ServerPlayerEntity player, String key) {
		sendMessageTo(player, LangText.get(true, key));
	}

	public static void sendLangMessageTo(ServerPlayerEntity player, String key, Object... objects) {
		sendMessageTo(player, LangText.getFormatter(true, key, objects));
	}

	public static void sendLangMessageTo(ServerCommandSource source, String key, Object... objects) {
		if (CommandHelper.isConsole(source))
			KiloEssentials.getInstance().getServer().sendMessage(getFormattedLang(key, objects));
		else
			source.sendFeedback(LangText.getFormatter(true, key, objects), false);
	}

	public static void sendPrivateMessageTo(ServerCommandSource source, ServerPlayerEntity player, String message)
			throws CommandSyntaxException {
		String format = config.getValue("chat.privateMessageFormat");

		String toSource = format.replace("%SOURCE%", "&r&aME&r")
				.replace("%TARGET%", "&r" + player.getName().asString() + "&r").replace("%MESSAGE%", message);
		String toTarget = format.replace("%SOURCE%", source.getName()).replace("%TARGET%", "&r&aME")
				.replace("%MESSAGE%", message);
		;

		sendMessageTo(source, new ChatMessage(toSource, true));
		sendMessageTo(player, new ChatMessage(toTarget, true));
	}

	public static void broadCastExceptConsole(ChatMessage chatMessage) {
		for (PlayerEntity entity : getServer().getPlayerList()) {
			entity.addChatMessage(new LiteralText(chatMessage.getFormattedMessage()), false);
		}
	}

	public static void broadCastLangExceptConsole(String key, Object... objects) {
		broadCastExceptConsole(new ChatMessage(getFormattedLang(key, objects), false));
	}

	public static void broadCastLangToConsole(String key, Object... objects) {
		broadCastToConsole(new ChatMessage(getFormattedLang(key, objects), false));
	}

	public static void broadCastToConsole(ChatMessage chatMessage) {
		chatMessage.setMessage(chatMessage.getOriginal(), false);
		getServer().sendMessage(chatMessage.getFormattedMessage());
	}

	public static void broadCast(ChatMessage chatMessage) {
		for (PlayerEntity entity : getServer().getPlayerList()) {
			entity.addChatMessage(new LiteralText(chatMessage.getFormattedMessage()), false);
		}

		getServer().sendMessage(TextFormat.removeAlternateColorCodes('&', chatMessage.getFormattedMessage()));
	}

	public static void broadCast(Text text) {
		for (PlayerEntity entity : getServer().getPlayerList()) {
			entity.sendMessage(text);
		}

		getServer().sendMessage(text.asString());
	}

	public static void broadCastLang(String key) {
		broadCastLang(key, (Object) null);
	}

	public static void broadCastLang(String key, Object... objects) {
		broadCast(new ChatMessage(getFormattedLang(key, objects), true));
	}

	public static void broadcastUserJoinEventMessage(ServerUser user) {
		broadCast(new ChatMessage(
				messages.getLocal(true, "events.userJoin", new UserConfigVariables(user))
					.replace("%USER_DISPLAYNAME%", user.getDisplayname()),
				true
		));

	}

	public static void broadcastUserLeaveEventMessage(ServerUser user) {
		broadCast(new ChatMessage(
				messages.getLocal(true, "events.userLeave", new UserConfigVariables(user))
						.replace("%USER_DISPLAYNAME%", user.getDisplayname()),
				true
		));

	}


}