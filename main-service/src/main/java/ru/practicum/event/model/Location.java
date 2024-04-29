package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Embeddable
public class Location {
    private float lat;
    private float lon;
}
