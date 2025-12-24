package pepjebs.bunkbeds;

import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.block.BedBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class SpawnStackingComponent implements AutoSyncedComponent, ComponentV3, WorldComponentInitializer {

    private static final HashMap<String, List<BlockPos>> bedSpawns = new HashMap<>();

    @Override
    public boolean isRequiredOnClient() {
        return false;
    }

    @Override
    public void readData(ReadView readView) {
        int size = readView.getInt("size", 0);
        for (int i = 0; i < size; i++) {
            String name = readView.getString("name_" + i, "");
            int elemSize = readView.getInt("size_" + i, 0);
            bedSpawns.remove(name);
            bedSpawns.put(name, new ArrayList<>());
            for (int j  = 0; j < elemSize; j++) {
                String entry = readView.getString(i + "_elem_" + j, "");
                String[] entries = entry.split("::");
                String dim = entries[0];
                var coords = Arrays.stream(entries[1].split(",")).map(Integer::parseInt).toList();
                bedSpawns.get(name).add(new BlockPos(coords.get(0), coords.get(1), coords.get(2)));
            }
        }
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putInt("size", bedSpawns.size());
        var entries = bedSpawns.entrySet().stream().toList();
        for (int i = 0; i < entries.size(); i++){
            writeView.putString("name_"+i, entries.get(i).getKey());
            var blocks = entries.get(i).getValue();
            writeView.putInt("size_"+i, blocks.size());
            for (int j  = 0; j < blocks.size(); j++) {
                var entry = blocks.get(j);
                writeView.putString(i+"_elem_"+j, "minecraft:overworld::"+entry.getX()+","+entry.getY()+","+entry.getZ());
            }
        }
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(BunkBedsMod.PLAYER_BEDS_KEY, world -> new SpawnStackingComponent());
    }

    public List<BlockPos> playerSpawns(String playerName) {
        return bedSpawns.get(playerName);
    }

    public void popPlayerSpawnPos(ServerWorld world, String playerName) {
        if (!bedSpawns.containsKey(playerName)) return;
        bedSpawns.put(playerName, pruneBedPositions(world, bedSpawns.get(playerName)));
    }

    public void pushPlayerSpawnPos(@Nullable World world, String playerName, BlockPos globalPos) {
        var existing = bedSpawns.get(playerName);
        if (existing == null) existing = new ArrayList<>();
        existing.remove(globalPos);
        if (world != null) existing = pruneBedPositions(world, existing);
        if (existing.size() >= (BunkBedsMod.CONFIG == null
                ? BunkBedsMod.DEFAULT_MAX_BED_STACK : BunkBedsMod.CONFIG.maxBunkBedStackSize)) {
            existing.remove(existing.size() - 1);
        }
        existing.add(0, globalPos);
        bedSpawns.put(playerName, new ArrayList<>(existing));
    }

    private List<BlockPos> pruneBedPositions(World world, List<BlockPos> positions) {
        return positions.stream().filter(p -> {
            var block = world.getBlockState(p).getBlock();
            return block instanceof BedBlock; // Removed "&& BedBlock.isBedWorking(world)"
        }).collect(Collectors.toCollection(ArrayList::new));
    }
}
