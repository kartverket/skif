package no.statkart.skif.config;

import no.statkart.skif.config.internal.ConfigurationUtils;

import java.util.*;

/**
 * <p>A Map based Configuration.</p>
 * <p><em>Note:</em>Configuration objects of this type can be read concurrently
 * by multiple threads. However if one of these threads modifies the object,
 * synchronization has to be performed manually.</p>
 *
 * @author Emmanuel Bourg
 * @version $Revision: 548098 $, $Date: 2007-06-17 21:34:03 +0200 (So, 17 Jun 2007) $
 * @since 1.1
 */
public class MapConfiguration extends AbstractConfiguration implements Cloneable {
    /** The Map decorated by this configuration. */
    protected Map map;

    public MapConfiguration()
    {
        this.map = new HashMap();
    }

    /**
     * Create a Configuration decorator around the specified Map. The map is
     * used to store the configuration configuration, any change will also affect
     * the Map.
     *
     * @param map the map
     */
    public MapConfiguration(Map map)
    {
        this.map = map;
    }

    /**
     * Return the Map decorated by this configuration.
     *
     * @return the map this configuration is based onto
     */
    public Map getMap()
    {
        return map;
    }

    public Object getProperty(String key)
    {
        Object value = map.get(key);
        if ((value instanceof String) && (!isDelimiterParsingDisabled()))
        {
            List list = PropertyConverter.split((String) value, getListDelimiter());
            return list.size() > 1 ? list : list.get(0);
        }
        else
        {
            return value;
        }
    }

    protected void addPropertyDirect(String key, Object value)
    {
        Object previousValue = getProperty(key);

        if (previousValue == null)
        {
            map.put(key, value);
        }
        else if (previousValue instanceof List)
        {
            // the value is added to the existing list
            ((List) previousValue).add(value);
        }
        else
        {
            // the previous value is replaced by a list containing the previous value and the new value
            List list = new ArrayList();
            list.add(previousValue);
            list.add(value);

            map.put(key, list);
        }
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public boolean containsKey(String key)
    {
        return map.containsKey(key);
    }

    protected void clearPropertyDirect(String key)
    {
        map.remove(key);
    }

    public Iterator getKeys()
    {
        return map.keySet().iterator();
    }

    /**
     * Returns a copy of this object. The returned configuration will contain
     * the same properties as the original. Event listeners are not cloned.
     *
     * @return the copy
     * @since 1.3
     */
    public Object clone()
    {
        try
        {
            MapConfiguration copy = (MapConfiguration) super.clone();
            copy.map = (Map) ConfigurationUtils.clone(map);
            return copy;
        }
        catch (CloneNotSupportedException cex)
        {
            // cannot happen
            throw new ConfigurationRuntimeException(cex);
        }
    }
}
