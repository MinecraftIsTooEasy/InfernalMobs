package atomicstryker.infernalmobs.common;

import net.minecraft.Entity;
import net.minecraft.EntityLivingBase;
import net.minecraft.Chunk;

public class SaveEventHandler
{
    public static SaveEventHandler instance;

    public SaveEventHandler()
    {
        instance = this;
    }

    public void onChunkUnload(Chunk chunk)
    {
        Entity newEnt;
        for (int i = 0; i < chunk.entityLists.length; i++)
        {
            for (int j = 0; j < chunk.entityLists[i].size(); j++)
            {
                newEnt = (Entity) chunk.entityLists[i].get(j);
                if (newEnt instanceof EntityLivingBase)
                {
                    /*
                     * an EntityLiving was just dumped to a save file and
                     * removed from the world
                     */
                    if (InfernalMobsCore.getIsRareEntity((EntityLivingBase) newEnt))
                    {
                        InfernalMobsCore.removeEntFromElites((EntityLivingBase) newEnt);
                    }
                }
            }
        }
    }

    public void onChunkLoad(Chunk chunk)
    {
        Entity newEnt;
        for (int i = 0; i < chunk.entityLists.length; i++)
        {
            for (int j = 0; j < chunk.entityLists[i].size(); j++)
            {
                newEnt = (Entity) chunk.entityLists[i].get(j);
                if (newEnt instanceof EntityLivingBase)
                {
                    String savedMods = ((IEntity) newEnt).getEntityData().getString(InfernalMobsCore.instance().getNBTTag());
                    if (!savedMods.equals(""))
                    {
                        InfernalMobsCore.instance().addEntityModifiersByString((EntityLivingBase) newEnt, savedMods);
                    }
                }
            }
        }
    }

}
