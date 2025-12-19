package com.salesianos.triana.ejercicicio16.servicios;

import com.salesianos.triana.ejercicicio16.entidades.Event;
import com.salesianos.triana.ejercicicio16.entidades.Venue;
import com.salesianos.triana.ejercicicio16.repositorios.EventRepository;
import com.salesianos.triana.ejercicicio16.repositorios.OrganizerRepository;
import com.salesianos.triana.ejercicicio16.repositorios.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.IllegalFormatCodePointException;

@Service
@RequiredArgsConstructor
public class EventCreationService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final OrganizerRepository organizerRepository;


    public Event createEvent(Event event) {

        Venue venue = venueRepository.findById(event.getVenue().getId())
                .orElseThrow(() -> new IllegalArgumentException("Venue with ID " + event.getVenue().getId() + " does not exist."));

        Event newEvent = Event.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .venue(venue) // Si el Venue no existe, el propio codigo lanza la excepcion
                .organizer(organizerRepository.getReferenceById(event.getOrganizer().getId())) // Si el Organizer no existe, el propio codigo lanza la excepcion
                .build();

        if (venue.getEvents().stream().anyMatch(e -> newEvent.getStartAt().isBefore(e.getEndAt()) && newEvent.getEndAt().isAfter(e.getStartAt())))
            throw new IllegalArgumentException("The venue is already booked for the specified time range.");


        if (newEvent.getEndAt().isBefore(newEvent.getStartAt()))
            throw new IllegalArgumentException("The event's end time cannot be before its start time.");

        if (newEvent.getStartAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("The event's start time cannot be in the past.");



        return eventRepository.save(newEvent);
    }

}
