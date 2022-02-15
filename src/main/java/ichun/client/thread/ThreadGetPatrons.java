package ichun.client.thread;

import com.google.gson.Gson;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.iChunUtil;
import net.minecraft.client.Minecraft;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

@SideOnly(Side.CLIENT)
public class ThreadGetPatrons extends Thread
{
    public String patronList = "https://raw.githubusercontent.com/iChun/iChunUtil/1.7.10_legacy/src/main/resources/assets/ichunutil/mod/patrons.json";

    public ThreadGetPatrons()
    {
        this.setName("iChunUtil Patron Getter Thread");
        this.setDaemon(true);
    }

    @Override
    public void run()
    {
        try
        {
            Gson gson = new Gson();
            Reader fileIn = new InputStreamReader(new URL(patronList).openStream());
            String[] json = gson.fromJson(fileIn, String[].class);
            fileIn.close();

            if(json != null)
            {
                for(String s : json)
                {
                    if(s.replaceAll("-", "").equalsIgnoreCase(Minecraft.getMinecraft().getSession().getPlayerID()))
                    {
                        iChunUtil.isPatron = true;
                        iChunUtil.config.setCurrentCategory("patreon", "ichun.config.patreon.name", "ichun.config.patreon.comment");
                        iChunUtil.config.createIntBoolProperty("showPatronReward", "ichun.config.showPatronReward.name", "ichun.config.showPatronReward.comment", true, false, true);
                        iChunUtil.config.save();
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
