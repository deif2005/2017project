//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mingyu.ices.common;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mingyu.ices.domain.common.ResponseT;
import com.mingyu.ices.domain.common.RtnCodeEnum;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class ExtFastJsonHttpMessageConverter<T> extends AbstractHttpMessageConverter<T> {
    public static final Charset UTF8 = Charset.forName("UTF-8");
    private Charset charset;
    private SerializerFeature[] features;
  //  private ITypeReference iTypeReference;

    public ExtFastJsonHttpMessageConverter() {
        super(new MediaType[]{new MediaType("application", "json", UTF8), new MediaType("application", "*+json", UTF8)});
        this.charset = UTF8;
        this.features = new SerializerFeature[0];
    }

    protected boolean supports(Class<?> clazz) {
        return true;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public SerializerFeature[] getFeatures() {
        return this.features;
    }

    public void setFeatures(SerializerFeature... features) {
        this.features = features;
    }

    /*public ITypeReference getiTypeReference() {
        return this.iTypeReference;
    }

    public void setiTypeReference(ITypeReference iTypeReference) {
        this.iTypeReference = iTypeReference;
    }*/

    protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = inputMessage.getBody();
        byte[] buf = new byte[1024];

        while(true) {
            int bytes = in.read(buf);
            if(bytes == -1) {
                byte[] bytes1 = baos.toByteArray();
                String url = ((ServletServerHttpRequest)inputMessage).getServletRequest().getServletPath();
                int size = url.indexOf("?");
                if(size != -1) {
                    url = url.substring(0, size);
                }
                return null;
              //  return JSON.parseObject(bytes1, 0, bytes1.length, this.charset.newDecoder(), this.iTypeReference.getTypeReference(url).getType(), new Feature[0]);
            }

            if(bytes > 0) {
                baos.write(buf, 0, bytes);
            }
        }
    }

    protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        ResponseT response = new ResponseT(RtnCodeEnum.SUCCESS);
        response.setBizData(t);
        OutputStream out = outputMessage.getBody();

        String text = JSON.toJSONString(response, SerializerFeature.DisableCircularReferenceDetect);
        byte[] bytes = text.getBytes(this.charset);
        out.write(bytes);
    }
}
