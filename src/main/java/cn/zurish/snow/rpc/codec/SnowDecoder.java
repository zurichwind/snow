package cn.zurish.snow.rpc.codec;

import cn.zurish.snow.rpc.common.convention.errorcode.RpcErrorCode;
import cn.zurish.snow.rpc.common.convention.exception.RpcException;
import cn.zurish.snow.rpc.common.entity.SnowRequest;
import cn.zurish.snow.rpc.common.entity.SnowResponse;
import cn.zurish.snow.rpc.common.enums.RequestType;
import cn.zurish.snow.rpc.common.enums.SerializerType;
import cn.zurish.snow.rpc.serializer.Serializer;
import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.zurish.snow.rpc.common.constants.RpcConstants.MAGIC_NUMBER;
import static cn.zurish.snow.rpc.common.constants.RpcConstants.VERSION;


/**
 * custom protocol decoder
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId                          |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+-------------------+
 *   |                                                                                                                           |
 *   |                                              body                                                                         |
 *   |                                                                                                                           |
 *   |                                             ... ...                                                                       |
 *   +---------------------------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    4B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    8B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 * <p>
 * {@link LengthFieldBasedFrameDecoder} is a length-based decoder , used to solve TCP unpacking and sticking problems.
 * </p >
 *
 * 2024/1/11 20:46
 */
@Slf4j
public class SnowDecoder extends ReplayingDecoder {
/*
    *//**
     * lengthFieldOffset: magic 4B + version 1B = 5B;
     * lengthFieldLength: full length is 4B
     * lengthAdjustment:
     * initialBytesToStrip:
     * *//*
    public  SnowDecoder() {

    }*/
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {


        int magic = in.readInt();
        if (magic != MAGIC_NUMBER) {
            log.info("不识别的协议包: {}", magic);
            throw new RpcException(RpcErrorCode.UNSUPPORT_PROTOCOL);
        }
        byte version = in.readByte();
        if ( version != VERSION) {
            log.info("不匹配的版本号! 请求版本号: {}, 服务端版本号: {}", version, VERSION);
            throw new RpcException(RpcErrorCode.UNMATCHED_VERSION);
        }
        int fullLength = in.readInt();
        byte[] bytes = new byte[fullLength];

        byte messageType = in.readByte();
        Class<?> messageTypeClass;
        //messageTypeClass = (Class<?>) RequestType.getByCode(messageType);
        if (messageType == RequestType.REQUEST_TYPE.getCode()) {
            messageTypeClass = SnowRequest.class;
        } else if (messageType == RequestType.RESPONSE_TYPE.getCode()) {
            messageTypeClass = SnowResponse.class;
        } else {
            log.error("不识别的数据包: {}", messageType);
            throw new RpcException(RpcErrorCode.UNKNOWN_PACKAGE_TYPE);
        }

        byte compressType = in.readByte();

        byte serializeType = in.readByte();
        Serializer serializer = SerializerType.getByCode(serializeType);
        if (serializer == null) {
            log.info("不识别的反序列化器: {}", serializeType);
            throw new RpcException(RpcErrorCode.UNKNOWN_SERIALIZER);
        }

        long requestId = in.readLong();

        in.readBytes(bytes);
        Object obj = serializer.deserialize(bytes, messageTypeClass);
        log.warn(JSON.toJSONString(obj));
        out.add(obj);
    }
}
