package org.abg.batchapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link org.abg.batchapi.entities.Visitor}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitorDto implements Serializable {
    String firstName;
    String lastName;
    String emailAddress;
    String phoneNumber;
    String address;
    Date visitDate;
}