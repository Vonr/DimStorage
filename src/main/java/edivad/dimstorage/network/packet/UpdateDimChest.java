package edivad.dimstorage.network.packet;

import edivad.dimstorage.Main;
import edivad.dimstorage.tile.TileEntityDimChest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class UpdateDimChest extends UpdateDimBase {

    public UpdateDimChest(FriendlyByteBuf buf) {
        super(buf);
    }

    public UpdateDimChest(TileEntityDimChest tile) {
        super(tile);
    }

    @Override
    public void customHandle(Level level, ServerPlayer player) {
        BlockEntity tile = level.getBlockEntity(pos);

        if(!(tile instanceof TileEntityDimChest chest)) {
            Main.logger.error("Wrong type of tile entity (expected TileEntityDimChest)!");
            return;
        }

        chest.setFrequency(freq);
        chest.locked = locked;
        chest.setChanged();

        level.sendBlockUpdated(pos, chest.getBlockState(), chest.getBlockState(), BlockFlags.DEFAULT);
        NetworkHooks.openGui(player, chest, buf -> buf.writeBlockPos(pos).writeBoolean(true));
    }
}
