package com.soongsil.swcontest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Log extends DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ip;

    private String requestURI;

    private String requestHeader;

    @Lob
    private String requestBody;

    @Lob
    private String responseBody;
}
