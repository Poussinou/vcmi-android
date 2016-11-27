package eu.vcmi.vcmi.mods;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import eu.vcmi.vcmi.BuildConfig;
import eu.vcmi.vcmi.util.FileUtil;
import eu.vcmi.vcmi.util.Log;

/**
 * @author F
 */
public class VCMIModContainer extends VCMIMod
{
    private VCMIMod mCoreStatus; // kept here to correctly save core object to modSettings

    private VCMIModContainer()
    {
    }

    public static VCMIModContainer createContainer(final List<File> modsList) throws IOException, JSONException
    {
        final VCMIModContainer mod = new VCMIModContainer();
        mod.mSubmods.putAll(loadSubmods(modsList));
        return mod;
    }

    public void updateContainerFromConfigJson(final JSONObject modsList, final JSONObject coreStatus) throws JSONException
    {
        updateChildrenFromConfigJson(modsList);
        if (coreStatus != null)
        {
            mCoreStatus = VCMIMod.buildFromConfigJson("core", coreStatus);
        }
        else
        {
            mCoreStatus = new VCMIMod();
            mCoreStatus.mId = "core";
            mCoreStatus.mActive = true;
        }
    }

    @Override
    public String toString()
    {
        if (!BuildConfig.DEBUG)
        {
            return "";
        }
        return String.format("mods:[%s]", TextUtils.join(",", mSubmods.values()));
    }

    public void saveToFile(final File location)
    {
        try
        {
            FileUtil.write(location, toJson());
        }
        catch (Exception e)
        {
            Log.e(this, "Could not save mod settings", e);
        }
    }

    protected String toJson() throws JSONException
    {
        final JSONObject root = new JSONObject();
        final JSONObject activeMods = new JSONObject();
        final JSONObject coreStatus = new JSONObject();
        root.put("activeMods", activeMods);
        submodsToJson(activeMods);

        mCoreStatus.toJsonInternal(coreStatus);
        root.put("core", coreStatus);

        return root.toString();
    }
}
