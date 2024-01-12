package cn.zurish.snow.rpc.rpc.codec;

import cn.zurish.snow.rpc.rpc.common.data.IdGenerator;
import cn.zurish.snow.rpc.rpc.common.data.SnowFlakeFactory;
import cn.zurish.snow.rpc.rpc.common.entity.SnowRequest;
import cn.zurish.snow.rpc.rpc.common.entity.SnowResponse;
import cn.zurish.snow.rpc.rpc.common.enums.RequestType;
import cn.zurish.snow.rpc.rpc.common.enums.SerializerType;
import cn.zurish.snow.rpc.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static cn.zurish.snow.rpc.rpc.common.constants.RpcConstants.MAGIC_NUMBER;
import static cn.zurish.snow.rpc.rpc.common.constants.RpcConstants.VERSION;


/**
 *
 * * custom protocol decoder
 *  * <pre>
 *  *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *  *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+-----+-----+-------+
 *  *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId                          |
 *  *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+-------------------+
 *  *   |                                                                                                                           |
 *  *   |                                              body                                                                         |
 *  *   |                                                                                                                           |
 *  *   |                                             ... ...                                                                       |
 *  *   +---------------------------------------------------------------------------------------------------------------------------+
 *  * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    4B messageType（消息类型）
 *  * 1B compress（压缩类型） 1B codec（序列化类型）    8B  requestId（请求的Id）
 *  * body（object类型数据）
 *  * </pre>
 * 2024/1/11 20:44
 */
public class SnowEncoder extends MessageToByteEncoder {

    private final Serializer serializer;

    public SnowEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        out.writeByte(VERSION);

        byte[] bytes = serializer.serialize(msg);
        out.writeByte(bytes.length);
        if (msg instanceof SnowRequest) {
            out.writeByte(RequestType.REQUEST_TYPE.getCode());
        } else if(msg instanceof SnowResponse<?>){
            out.writeByte(RequestType.RESPONSE_TYPE.getCode());
        }
        out.writeByte(0x00);
        out.writeByte(serializer.getCode());
        out.writeLong(IdGenerator.genarateId());
        out.writeBytes(bytes);
    }
}
