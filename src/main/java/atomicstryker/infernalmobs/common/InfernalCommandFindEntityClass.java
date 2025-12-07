package atomicstryker.infernalmobs.common;

import java.util.Collection;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.CommandBase;
import net.minecraft.ICommand;
import net.minecraft.ICommandSender;
import net.minecraft.WrongUsageException;
import net.minecraft.EntityList;

public class InfernalCommandFindEntityClass extends CommandBase
{

    @Override
    public String getCommandName()
    {
        return "feclass";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/feclass X returns all currently registered Entities containing X in their classname's";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            throw new WrongUsageException("Invalid Usage of FindEntityClass command", (Object)args);
        }
        else
        {
            String classname = args[0];
            for (int i = 1; i < args.length; i++)
            {
                classname = classname + " " + args[i];
            }
            
            String result = "Found Entity classes: ";
            final Collection<String> classes = EntityList.classToStringMapping.values();
            boolean found = false;
            for (String entclass : classes)
            {
                if (entclass.toLowerCase().contains(classname.toLowerCase()))
                {
                    if (!found)
                    {
                        result += entclass;
                        found = true;
                    }
                    else
                    {
                        result += (", " + entclass);
                    }
                }
            }
            
            if (!found)
            {
                result += "Nothing found.";
            }
            
            FMLLog.getLogger().info(sender.getCommandSenderName()+ ": " + result);
        }
    }
    
    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof ICommand)
        {
            return ((ICommand)o).getCommandName().compareTo(getCommandName());
        }
        return 0;
    }

}
