package com.songlanyun.msgCenter.utils;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class InputStreamCollector { private InputStream is;

    public void collectInputStream(InputStream is) {
        if (this.is == null) this.is = is;
        this.is = new SequenceInputStream(this.is, is);
    }

    public InputStream getInputStream() {
        return this.is;
    }
    // Flux<DataBuffer> body = exchange.getRequest().getBody();
//        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();
//        Flux<String> decodedRequest =requestBody.map(databuffer -> {
//           return InputStreamCollector.decodeDataBuffer(databuffer);
//
//        });
//
//         return decodedRequest.flatMap(s->{
//              System.out.print(s);
//            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
//                    ResponseInfo.info(200,"更新成功"), ResponseInfo.class);
//
//        });

    //         decodedRequest.doOnNext(s -> {
//            System.out.print(s);
//
//        });
//
    public static String decodeDataBuffer(DataBuffer dataBuffer) {
        Charset charset = StandardCharsets.UTF_8;
        CharBuffer charBuffer = charset.decode(dataBuffer.asByteBuffer());
        DataBufferUtils.release(dataBuffer);
        String value = charBuffer.toString();
        return value;
    }
    public static String getStringFromStream(InputStream inputStream) throws IOException {
        if(inputStream == null){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        String s ;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((s = in.readLine()) != null){
            sb.append(s);
        }
        in.close();
        inputStream.close();

        return sb.toString();
    }
    /**
     * 获取请求体中的字符串内容
     * @param serverHttpRequest
     * @return
     */
    public static String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest){
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder sb = new StringBuilder();

        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });
        return sb.toString();

    }
}