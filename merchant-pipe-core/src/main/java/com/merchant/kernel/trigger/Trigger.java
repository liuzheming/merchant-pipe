package com.merchant.kernel.trigger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Create on 2022/3/9
 */
@Data
public class Trigger implements Serializable {

    private Long serialVersionUID = 1L;

    private String uuid;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime launchTime;
    private String eventType;
    private String relationId;
    private String serverAddress;
    private String label;
    private String payload;
    private int retryTimes;

    public Trigger() {
    }

    public Trigger(String uuid) {
        this.uuid = uuid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trigger)) return false;
        Trigger trigger = (Trigger) o;
        return Objects.equals(uuid, trigger.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
