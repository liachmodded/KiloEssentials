package org.kilocraft.essentials.user;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.DimensionType;
import org.kilocraft.essentials.KiloCommands;
import org.kilocraft.essentials.api.feature.ConfigurableFeature;
import org.kilocraft.essentials.api.user.OnlineUser;
import org.kilocraft.essentials.commands.teleport.BackCommand;
import org.kilocraft.essentials.extensions.homes.api.Home;
import org.kilocraft.essentials.extensions.homes.api.UnsafeHomeException;
import org.kilocraft.essentials.extensions.homes.commands.HomeCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author CODY_AI
 * A better way of handeling the User (Instance of player) Homes
 *
 * @see ServerUser
 * @see ServerUserManager
 */


public class UserHomeHandler implements ConfigurableFeature {
    private static boolean isEnabled = false;
    private static List<Home> loadedHomes = new ArrayList<>();
    private List<Home> userHomes;
    private ServerUser serverUser;

    @Override
    public boolean register() {
        isEnabled = true;
        HomeCommand.register(KiloCommands.getDispatcher());
        return true;
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public UserHomeHandler() {
    }

    public UserHomeHandler(ServerUser serverUser) {
        if (isEnabled()) {
            this.serverUser = serverUser;
            this.userHomes = new ArrayList<>();
        }

    }

    public void addHome(Home home) {
        this.userHomes.add(home);
        loadedHomes.add(home);
    }

    public void removeHome(String name) {
        removeHome(getHome(name));
    }

    public void removeHome(Home home) {
        this.userHomes.remove(home);
        loadedHomes.remove(home);
    }

    public Home getHome(String name) {
        for (Home home : this.userHomes) {
            if (home.getName().equals(name))
                return home;
        }

        return new Home();
    }

    public List<Home> getHomes() {
        return this.userHomes;
    }

    public boolean hasHome(String name) {
        boolean bool = false;
        for (Home userHome : this.userHomes) {
            if (userHome.getName().equals(name))
                bool = true;
        }

        return bool;
    }

    public static boolean hasHome(UUID uuid, String name) {
        boolean bool = false;
        for (Home loadedHome : loadedHomes) {
            if (loadedHome.getName().equals(name) && loadedHome.getOwner().equals(uuid))
                bool = true;
        }

        return bool;
    }

    public void teleportToHome(OnlineUser user, String name) throws UnsafeHomeException {
        teleportToHome(user, getHome(name));
    }

    public void teleportToHome(OnlineUser user, Home home) throws UnsafeHomeException {
        if (user.isOnline()) {
            ServerWorld world = user.getPlayer().getServer().getWorld(DimensionType.byId(home.getDimId()));
            if(world == null) {
                throw new UnsafeHomeException(home, Reason.MISSING_DIMENSION);
            }
            BackCommand.setLocation(user.getPlayer(), new Vector3f(user.getPlayer().getPos()), user.getPlayer().getServerWorld().getDimension().getType());
            Home.teleportTo(user, home);
        }

    }

    public CompoundTag serialize(CompoundTag tag) {
        for (Home userHome : this.userHomes) {
            tag.put(userHome.getName(), userHome.toTag());
        }

        return tag;
    }

    public void deserialize(CompoundTag compoundTag) {
        for (String key : compoundTag.getKeys()) {
            Home home = new Home(compoundTag.getCompound(key));
            home.setName(key);
            home.setOwner(this.serverUser.uuid);
            this.userHomes.add(home);
            loadedHomes.add(home);
        }

    }

    public static List<Home> getHomesOf(UUID uuid) {
        List<Home> list = new ArrayList<>();
        for (Home home : loadedHomes) {
            if  (home.getOwner().equals(uuid)) list.add(home);
        }

        return list;
    }

    public static List<Home> getLoadedHomes() {
        return loadedHomes;
    }

    public static CompletableFuture<Suggestions> suggestHomes(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        return CommandSource.suggestMatching(getHomesOf(context.getSource().getPlayer().getUuid()).stream().map(Home::getName), builder);
    }

    public enum Reason {
        UNSAFE_DESTINATION, MISSING_DIMENSION;
    }
}
