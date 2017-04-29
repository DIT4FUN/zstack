package org.zstack.header.vo;

import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.utils.DebugUtils;
import org.zstack.utils.FieldUtils;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xing5 on 2017/4/29.
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ResourceVO {
    @Id
    @Column
    @Index
    protected String uuid;

    @Column
    private String resourceName;

    @Column
    private String resourceType;

    private static Map<Class, Field> nameFields = new ConcurrentHashMap<>();

    private Field getNameField() {
        Field f = nameFields.get(getClass());
        if (f != null) {
            return f;
        }

        ResourceAttributes at = getClass().getAnnotation(ResourceAttributes.class);
        f = FieldUtils.getField(at == null ? "name" : at.nameField(), getClass());
        if (f != null) {
            f.setAccessible(true);
            nameFields.put(getClass(), f);
        }

        return f;
    }

    @PrePersist
    private void prePersist() {
        resourceType = getClass().getSimpleName();

        try {
            Field nameField = getNameField();
            if (nameField != null) {
                resourceName = (String) nameField.get(this);
            }
        } catch (IllegalAccessException e) {
            throw new CloudRuntimeException(e);
        }
    }

    String getResourceName() {
        return resourceName;
    }

    void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    String getResourceType() {
        return resourceType;
    }

    void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
