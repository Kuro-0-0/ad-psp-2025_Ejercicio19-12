package com.salesianos.triana.ejercicicio16.servicios;

import com.salesianos.triana.ejercicicio16.entidades.*;
import com.salesianos.triana.ejercicicio16.repositorios.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {

    private final VenueRepository venueRepository;
    private final StaffAssignmentRepository staffAssignmentRepository;
    private final EventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;

    public StaffAssignment assignStaffToEvent(StaffAssignment staffAssignment) {

        Event event = eventRepository.findById(staffAssignment.getEvent().getId()).orElseThrow(NoSuchElementException::new);

        if (event.getStatus() == EventStatus.Cancelled)
            throw new IllegalArgumentException("No se pueden asignar staff a un evento cancelado.");

        StaffAssignment newStaffAssignment = StaffAssignment.builder()
                .role(staffAssignment.getRole())
                .shiftStart(staffAssignment.getShiftStart())
                .shiftEnd(staffAssignment.getShiftEnd())
                .paid(staffAssignment.isPaid())
                .attendee(attendeeRepository.getReferenceById(staffAssignment.getAttendee().getId()))
                .event(event)
                .build();

        return staffAssignmentRepository.save(newStaffAssignment);
    }



    public Set<Ticket> listTicketByVenue(Long venueId) {
        Venue venue = venueRepository.findById(venueId).orElseThrow(NoSuchElementException::new);
        return venue.getEvents().stream().flatMap(e -> e.getTickets().stream()).collect(Collectors.toSet());
    }

}
