package cn.zurish.snow.rpc.core.common.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 2024/1/15 16:02:11
 */
public class Instance implements Serializable {

    private static final long serialVersionUID = 2003L;

    /**
     * unique id of this instance.
     */
    private String instanceId;

    /**
     * instance ip.
     */
    private String ip;

    /**
     * instance port.
     */
    private int port;

    /**
     * instance weight.
     */
    private double weight = 1.0D;

    /**
     * instance health status.
     */
    private boolean healthy = true;

    /**
     * If instance is enabled to accept request.
     */
    private boolean enabled = true;

    /**
     * If instance is ephemeral.
     *
     * @since 1.0.0
     */
    private boolean ephemeral = true;

    /**
     * cluster information of instance.
     */
    private String clusterName;

    /**
     * Service information of instance.
     */
    private String serviceName;

    /**
     * user extended attributes.
     */
    private Map<String, String> metadata = new HashMap<>();

    /**
     * add meta data.
     *
     * @param key   meta data key
     * @param value meta data value
     */
    public void addMetadata(final String key, final String value) {
        if (metadata == null) {
            metadata = new HashMap<>(4);
        }
        metadata.put(key, value);
    }


    @Override
    public String toString() {
        return "Instance{" + "instanceId='" + instanceId + '\'' + ", ip='" + ip + '\'' + ", port=" + port + ", weight="
                + weight + ", healthy=" + healthy + ", enabled=" + enabled + ", ephemeral=" + ephemeral
                + ", clusterName='" + clusterName + '\'' + ", serviceName='" + serviceName + '\'' + ", metadata="
                + metadata + '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Instance)) {
            return false;
        }

        final Instance host = (Instance) obj;
        return Instance.strEquals(host.toString(), toString());
    }

    private static boolean strEquals(final String str1, final String str2) {
        return Objects.equals(str1, str2);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
