package dev.compactmods.machines.room;

import dev.compactmods.machines.CompactMachines;
import dev.compactmods.machines.advancement.AdvancementTriggers;
import dev.compactmods.machines.api.dimension.CompactDimension;
import dev.compactmods.machines.api.dimension.MissingDimensionException;
import dev.compactmods.machines.api.room.RoomTemplate;
import dev.compactmods.machines.api.room.history.IRoomHistoryItem;
import dev.compactmods.machines.api.room.registration.IRoomRegistration;
import dev.compactmods.machines.core.CompactMachinesNet;
import dev.compactmods.machines.location.LevelBlockPosition;
import dev.compactmods.machines.location.PreciseDimensionalPosition;
import dev.compactmods.machines.location.SimpleTeleporter;
import dev.compactmods.machines.room.capability.PlayerRoomMetadataProviderProvider;
import dev.compactmods.machines.room.client.RoomClientHelper;
import dev.compactmods.machines.room.exceptions.NonexistentRoomException;
import dev.compactmods.machines.room.graph.CompactRoomProvider;
import dev.compactmods.machines.room.history.PlayerRoomHistoryItem;
import dev.compactmods.machines.room.network.SyncRoomMetadataPacket;
import dev.compactmods.machines.room.server.RoomServerHelper;
import dev.compactmods.machines.util.PlayerUtil;
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class RoomHelper {
    public static Registry<RoomTemplate> getTemplates() {
        return EnvExecutor.unsafeRunForDist(() -> RoomClientHelper::getTemplates, () -> RoomServerHelper::getTemplates);
    }

    public static void teleportPlayerIntoMachine(Level machineLevel, ServerPlayer player, LevelBlockPosition machinePos, IRoomRegistration room) throws MissingDimensionException {
        MinecraftServer serv = machineLevel.getServer();

        // Recursion check. Player tried to enter the room they're already in.
        if (player.level.dimension().equals(CompactDimension.LEVEL_KEY)) {
            final boolean recursion = RoomCapabilities.ROOM_HISTORY.maybeGet(player).map(hist -> {
                if (room.chunks().anyMatch(chunk -> player.chunkPosition().equals(chunk))) {
                    AdvancementTriggers.RECURSIVE_ROOMS.trigger(player);
                    return true;
                }

                return false;
            }).orElse(false);

            if (recursion) return;
        }

        try {
            final var entry = PreciseDimensionalPosition.fromPlayer(player);


            teleportPlayerIntoRoom(serv, player, room);

            // Mark the player as inside the machine, set external spawn, and yeet
            RoomCapabilities.ROOM_HISTORY.maybeGet(player).ifPresent(hist -> {
                hist.getHistory().addHistory(new PlayerRoomHistoryItem(entry, machinePos));

                setCurrentRoom(serv, player, room);
            });
        } catch (MissingDimensionException | NonexistentRoomException e) {
            CompactMachines.LOGGER.fatal("Critical error; could not enter a freshly-created room instance.", e);
        }
    }

    public static void setCurrentRoom(MinecraftServer server, ServerPlayer player, IRoomRegistration room) {
        // Mark current room, invalidates any listeners + debug screen
        final var roomProvider = CompactRoomProvider.instance(server);
        final var roomOwner = room.owner(roomProvider);
        PlayerRoomMetadataProviderProvider.CURRENT_ROOM_META.maybeGet(player).ifPresent(provider -> {
            provider.getCurrentRoomMetadataProvider().setCurrent(new PlayerRoomMetadataProvider.CurrentRoomData(room.code(), roomOwner));
        });

        final var sync = new SyncRoomMetadataPacket(room.code(), roomOwner);
        CompactMachinesNet.CHANNEL.sendToClient(sync, player);
    }

    public static void teleportPlayerIntoRoom(MinecraftServer serv, ServerPlayer player, IRoomRegistration room) throws MissingDimensionException, NonexistentRoomException {
        teleportPlayerIntoRoom(serv, player, room, null);
    }

    public static void teleportPlayerIntoRoom(MinecraftServer serv, ServerPlayer player, IRoomRegistration room, @Nullable LevelBlockPosition from)
            throws MissingDimensionException, NonexistentRoomException {
        final var compactDim = CompactDimension.forServer(serv);
        final var roomProvider = CompactRoomProvider.instance(compactDim);

        serv.submitAsync(() -> {
            player.changeDimension(compactDim, SimpleTeleporter.to(room.spawnPosition(roomProvider), room.spawnRotation(roomProvider)));
        });

        if(from != null) {
            // Mark the player as inside the machine, set external spawn
            RoomCapabilities.ROOM_HISTORY.maybeGet(player).ifPresent(hist -> {
                var entry = PreciseDimensionalPosition.fromPlayer(player);
                hist.getHistory().addHistory(new PlayerRoomHistoryItem(entry, from));

            });
        }

        // Mark current room, invalidates any listeners + debug screen
        RoomHelper.setCurrentRoom(serv, player, room);
    }

    public static void teleportPlayerOutOfRoom(ServerLevel compactDim, @Nonnull ServerPlayer serverPlayer) {

        MinecraftServer serv = compactDim.getServer();
        if(!serverPlayer.level.dimension().equals(CompactDimension.LEVEL_KEY))
            return;

        RoomCapabilities.ROOM_HISTORY.maybeGet(serverPlayer)
                .ifPresentOrElse(hist -> {
                    if (hist.getHistory().hasHistory()) {
                        final var roomProvider = CompactRoomProvider.instance(compactDim);
                        final IRoomHistoryItem prevArea = hist.getHistory().pop();
                        // Mark current room, invalidates any listeners + debug screen
                        PlayerRoomMetadataProviderProvider.CURRENT_ROOM_META.maybeGet(serverPlayer).ifPresent(provider -> {
                            // Check entry dimension - if it isn't a machine room, clear room info
                            if(!prevArea.getEntryLocation().dimension().equals(CompactDimension.LEVEL_KEY))
                                provider.getCurrentRoomMetadataProvider().clearCurrent();
                            else {
                                roomProvider.findByChunk(prevArea.getEntryLocation().chunkPos()).ifPresent(roomMeta -> {
                                    provider.getCurrentRoomMetadataProvider().setCurrent(new PlayerRoomMetadataProvider.CurrentRoomData(roomMeta.code(), roomMeta.owner(roomProvider)));
                                });
                            }
                        });

                        var spawnPoint = prevArea.getEntryLocation();
                        final var enteredMachine = prevArea.getMachine().getBlockPosition();

                        final var level = spawnPoint.level(serv);
                        serverPlayer.changeDimension(level, SimpleTeleporter.lookingAt(spawnPoint.position(), enteredMachine));
                    } else {
                        PlayerUtil.howDidYouGetThere(serverPlayer);

                        hist.getHistory().clear();
                        PlayerUtil.teleportPlayerToRespawnOrOverworld(serv, serverPlayer);
                    }
                }, () -> {
                    PlayerUtil.howDidYouGetThere(serverPlayer);
                    PlayerUtil.teleportPlayerToRespawnOrOverworld(serv, serverPlayer);
                });
    }
}
