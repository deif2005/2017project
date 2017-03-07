package com.mingyu.ices.zk;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO 一句话描述该类用途
 * <p/>
 * 创建时间: 14-8-5 下午4:41<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public class ZookeeperConfigurer extends PropertySourcesPlaceholderConfigurer {

    private Map<String, Object> ctxPropsMap = new HashMap<String, Object>();

    private ZookeeperResource zkLocation;
    private Resource[] localLocations = new Resource[0];

    @Override
    public void setLocation(Resource location) {
        zkLocation = (ZookeeperResource) location;

        super.setLocations((Resource[]) mergeArray(localLocations, zkLocation));
    }

    @Override
    public void setLocations(final Resource[] locations) {
        //this.localLocations = locations;
        System.arraycopy(locations, 0, this.localLocations, 0, locations.length);
        super.setLocations((Resource[]) mergeArray(locations, zkLocation));
    }
    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                     final ConfigurablePropertyResolver propertyResolver) throws BeansException {
        super.processProperties(beanFactoryToProcess, propertyResolver);

//        for (Object key : props.keySet()) {
//            ctxPropsMap.put(key.toString(), props.get(key));
//        }
    }
//    @Override
//    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
//            throws BeansException {
//        super.processProperties(beanFactoryToProcess, props);
//
//        for (Object key : props.keySet()) {
//            ctxPropsMap.put(key.toString(), props.get(key));
//        }
//    }

    public Object getProperty(String key) {
        return ctxPropsMap.get(key);
    }

    public ZookeeperResource getZkResoucre() {
        return zkLocation;
    }

    private static Resource[] mergeArray(Resource[] m1, Resource m2){
        Resource[] all = new Resource[m1.length + 1];
        if(m1.length > 0) {
            System.arraycopy(all, 0, m1, 0, m1.length);
            all[m1.length] = m2;
        } else {
            all[m1.length] = m2;
        }
        return all;
    }

    public static void main(String[] args) {
        Resource[] m1 = new Resource[2];
        Resource m2 = new Resource() {
            @Override
            public boolean exists() {
                return false;
            }

            @Override
            public boolean isReadable() {
                return false;
            }

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public URL getURL() throws IOException {
                return null;
            }

            @Override
            public URI getURI() throws IOException {
                return null;
            }

            @Override
            public File getFile() throws IOException {
                return null;
            }

            @Override
            public long contentLength() throws IOException {
                return 0;
            }

            @Override
            public long lastModified() throws IOException {
                return 0;
            }

            @Override
            public Resource createRelative(String relativePath) throws IOException {
                return null;
            }

            @Override
            public String getFilename() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }
        };

        Resource[] m3 = mergeArray(m1, m2);

        int i = m3.length;
    }
}